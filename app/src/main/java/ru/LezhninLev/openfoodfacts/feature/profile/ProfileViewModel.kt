package ru.LezhninLev.openfoodfacts.feature.profile

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.app.AlarmManager
import android.app.PendingIntent

import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar

private fun String.toTodayMillis(): Long? {
    val parts = split(":")
    if (parts.size != 2) return null
    val hour = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].toIntOrNull() ?: return null
    if (hour !in 0..23 || minute !in 0..59) return null
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis <= System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
    return calendar.timeInMillis
}

sealed interface ProfileEvent {
    data class OpenDocument(val uri: Uri, val mimeType: String) : ProfileEvent
    data class ShowMessage(val message: String) : ProfileEvent
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProfileRepository(application)
    private val _profile = MutableStateFlow(repository.initialProfile)
    val profile = _profile.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading = _isDownloading.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    fun saveProfile(fullName: String, resumeUrl: String, pairTime: String) {
        val updated = _profile.value.copy(
            fullName = fullName.trim(),
            resumeUrl = resumeUrl.trim(),
            pairTime = pairTime.trim()
        )
        repository.saveProfile(updated)
        _profile.value = updated
        schedulePairAlarm(updated)
    }

    private fun schedulePairAlarm(profile: UserProfile) {
        val triggerAtMillis = profile.pairTime.toTodayMillis() ?: return
        val alarmManager = getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(getApplication(), PairAlarmReceiver::class.java).apply {
            putExtra(EXTRA_USER_NAME, profile.fullName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        runCatching { alarmManager.cancel(pendingIntent) }
        val canSchedule = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
        if (canSchedule) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    companion object {
        const val EXTRA_USER_NAME = "extra_user_name"
    }

    fun setAvatar(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) { repository.copyAvatarFrom(uri) }
            }.onSuccess { savedUri ->
                _profile.value = _profile.value.copy(avatarUri = savedUri)
            }.onFailure { error ->
                withContext(Dispatchers.IO) { repository.deleteCameraCapture(uri) }
                _events.emit(ProfileEvent.ShowMessage(error.message ?: "Не удалось сохранить фото"))
            }
        }
    }

    fun createCameraCaptureUri(): Uri = repository.createCameraCaptureUri()

    fun discardCameraCapture(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteCameraCapture(uri) }
    }

    fun downloadResume() {
        val url = _profile.value.resumeUrl.trim()
        if (url.isBlank()) {
            viewModelScope.launch { _events.emit(ProfileEvent.ShowMessage("Сначала добавьте ссылку на резюме")) }
            return
        }
        if (_isDownloading.value) return

        viewModelScope.launch {
            _isDownloading.value = true
            try {
                val parsedUrl = Uri.parse(if (url.contains("://")) url else "https://$url")
                require(parsedUrl.scheme == "https" || parsedUrl.scheme == "http") {
                    "Укажите корректную ссылку на файл"
                }
                val extension = parsedUrl.lastPathSegment
                    ?.substringAfterLast('.', "")
                    ?.takeIf { it.matches(Regex("[A-Za-z0-9]{1,8}")) }
                    ?: "pdf"
                val mimeType = android.webkit.MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(extension.lowercase()) ?: "application/pdf"
                val request = DownloadManager.Request(parsedUrl)
                    .setTitle("Резюме")
                    .setDescription("Загрузка резюме")
                    .setMimeType(mimeType)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalFilesDir(
                        getApplication(),
                        Environment.DIRECTORY_DOWNLOADS,
                        "resume_${System.currentTimeMillis()}.$extension"
                    )
                val downloadManager = getApplication<Application>()
                    .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val downloadId = withContext(Dispatchers.IO) { downloadManager.enqueue(request) }

                while (true) {
                    val status = withContext(Dispatchers.IO) {
                        downloadManager.query(DownloadManager.Query().setFilterById(downloadId)).use { cursor ->
                            if (!cursor.moveToFirst()) null else cursor.getInt(
                                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                            )
                        }
                    }
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> break
                        DownloadManager.STATUS_FAILED, null -> error("Не удалось скачать файл")
                    }
                    delay(400)
                }
                val documentUri = downloadManager.getUriForDownloadedFile(downloadId)
                    ?: error("Скачанный файл недоступен")
                _events.emit(ProfileEvent.OpenDocument(documentUri, mimeType))
            } catch (error: Exception) {
                _events.emit(ProfileEvent.ShowMessage(error.message ?: "Ошибка загрузки файла"))
            } finally {
                _isDownloading.value = false
            }
        }
    }
}
