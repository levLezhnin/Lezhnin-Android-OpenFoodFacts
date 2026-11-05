package ru.LezhninLev.openfoodfacts.feature.profile

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

data class UserProfile(
    val fullName: String = "",
    val resumeUrl: String = "",
    val avatarUri: String? = null
)

/** Small profile fields fit SharedPreferences; the image itself lives in private app files. */
class ProfileRepository(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val profileDirectory = File(appContext.filesDir, "profile")

    val initialProfile = UserProfile(
        fullName = preferences.getString(KEY_FULL_NAME, "").orEmpty(),
        resumeUrl = preferences.getString(KEY_RESUME_URL, "").orEmpty(),
        avatarUri = preferences.getString(KEY_AVATAR_URI, null)
    )

    fun saveProfile(profile: UserProfile) {
        preferences.edit()
            .putString(KEY_FULL_NAME, profile.fullName)
            .putString(KEY_RESUME_URL, profile.resumeUrl)
            .apply()
    }

    fun copyAvatarFrom(uri: Uri): String {
        check(profileDirectory.exists() || profileDirectory.mkdirs()) {
            "Не удалось создать папку для фотографии"
        }
        val temporaryFile = File(profileDirectory, "avatar_${UUID.randomUUID()}.tmp")
        appContext.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Не удалось открыть выбранное изображение" }
            temporaryFile.outputStream().use(input::copyTo)
        }
        val avatarFile = File(profileDirectory, "avatar_${UUID.randomUUID()}.jpg")
        replaceAvatar(temporaryFile, avatarFile)
        val savedUri = persistAvatarUri(avatarFile)
        deleteCameraCapture(uri)
        return savedUri
    }

    fun createCameraCaptureUri(): Uri {
        check(profileDirectory.exists() || profileDirectory.mkdirs()) {
            "Не удалось создать папку для фотографии"
        }
        val captureFile = File(profileDirectory, "capture_${UUID.randomUUID()}.jpg")
        check(captureFile.createNewFile()) { "Не удалось подготовить файл для фотографии" }
        return FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            captureFile
        )
    }

    fun deleteCameraCapture(uri: Uri) {
        if (uri.authority != "${appContext.packageName}.fileprovider") return
        val fileName = uri.lastPathSegment ?: return
        if (fileName.startsWith("capture_") && fileName.endsWith(".jpg")) {
            File(profileDirectory, fileName).delete()
        }
    }

    private fun replaceAvatar(temporaryFile: File, avatarFile: File) {
        check(temporaryFile.renameTo(avatarFile)) { "Не удалось сохранить изображение" }
    }

    private fun persistAvatarUri(avatarFile: File): String {
        val previousUri = preferences.getString(KEY_AVATAR_URI, null)
        val uri = FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            avatarFile
        ).toString()
        preferences.edit().putString(KEY_AVATAR_URI, uri).apply()
        deletePreviousAvatar(previousUri, avatarFile)
        return uri
    }

    private fun deletePreviousAvatar(previousUri: String?, currentFile: File) {
        if (previousUri == null) return
        val oldUri = Uri.parse(previousUri)
        if (oldUri.authority != "${appContext.packageName}.fileprovider") return
        val fileName = oldUri.lastPathSegment ?: return
        val isManagedAvatar = fileName == "avatar.jpg" ||
            (fileName.startsWith("avatar_") && fileName.endsWith(".jpg"))
        if (fileName != currentFile.name && isManagedAvatar) {
            File(profileDirectory, fileName).delete()
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "user_profile"
        const val KEY_FULL_NAME = "full_name"
        const val KEY_RESUME_URL = "resume_url"
        const val KEY_AVATAR_URI = "avatar_uri"
    }
}
