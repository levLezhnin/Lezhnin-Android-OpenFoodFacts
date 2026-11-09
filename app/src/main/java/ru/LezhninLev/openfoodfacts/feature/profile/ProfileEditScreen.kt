package ru.LezhninLev.openfoodfacts.feature.profile

import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged

import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage

private fun isValidTime(value: String): Boolean {
    val parts = value.split(":")
    if (parts.size != 2) return false
    val hour = parts[0].toIntOrNull() ?: return false
    val minute = parts[1].toIntOrNull() ?: return false
    return parts[0].length == 2 && parts[1].length == 2 &&
        hour in 0..23 && minute in 0..59
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(viewModel: ProfileViewModel, onDone: () -> Unit, onBack: () -> Unit) {
    val profile by viewModel.profile.collectAsState()
    var fullName by remember(profile.fullName) { mutableStateOf(profile.fullName) }
    var resumeUrl by remember(profile.resumeUrl) { mutableStateOf(profile.resumeUrl) }
    var pairTime by remember(profile.pairTime) { mutableStateOf(profile.pairTime) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val columnRootY = remember { mutableStateOf(0f) }
    val timeFieldRootY = remember { mutableStateOf(0f) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // System Photo Picker (Android 13+) with the platform document-picker fallback on older versions;
    // both grant access only to the selected image, so READ_MEDIA/READ_EXTERNAL_STORAGE is unnecessary.
    val galleryPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let(viewModel::setAvatar) }

    val isTimeValid = remember(pairTime) { isValidTime(pairTime) }
    // The system camera writes into this app-owned URI; no CAMERA or storage permission is needed.
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        val captureUri = pendingCameraUri
        pendingCameraUri = null
        if (saved && captureUri != null) {
            viewModel.setAvatar(captureUri)
        } else {
            captureUri?.let(viewModel::discardCameraCapture)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактирование профиля") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                if (profile.avatarUri != null) {
                    AsyncImage(
                        model = profile.avatarUri,
                        contentDescription = "Аватар пользователя",
                        modifier = Modifier.height(136.dp).fillMaxWidth(0.4f).clip(CircleShape)
                            .clickable { showPhotoOptions = true },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.AddAPhoto,
                        contentDescription = "Выбрать фотографию",
                        modifier = Modifier.height(136.dp).fillMaxWidth(0.4f)
                            .clickable { showPhotoOptions = true },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            TextButton(onClick = { showPhotoOptions = true }) { Text("Изменить фото") }

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("ФИО") },
                placeholder = { Text("Иванов Иван Иванович") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = resumeUrl,
                onValueChange = { resumeUrl = it },
                label = { Text("Ссылка на резюме / портфолио") },
                placeholder = { Text("https://example.com/resume.pdf") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pairTime,
                onValueChange = { pairTime = it },
                label = { Text("Время любимой пары") },
                placeholder = { Text("HH:mm") },
                singleLine = true,
                isError = pairTime.isNotBlank() && !isTimeValid,
                supportingText = {
                    if (pairTime.isNotBlank() && !isTimeValid) {
                        Text("Укажите время в формате HH:mm")
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(Icons.Default.Alarm, contentDescription = "Выбрать время")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                scrollState.animateScrollTo(scrollState.maxValue)
                            }
                        }
                    }
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    viewModel.saveProfile(fullName, resumeUrl, pairTime)
                    onDone()
                },
                enabled = pairTime.isBlank() || isTimeValid,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Готово") }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = pairTime.substringBefore(':', "18").toIntOrNull()?.coerceIn(0, 23) ?: 18,
            initialMinute = pairTime.substringAfter(':', "0").toIntOrNull()?.coerceIn(0, 59) ?: 0
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Выберите время") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    pairTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("ОК") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Отмена") }
            }
        )
    }

    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text("Выберите фото") },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("Галерея") },
                        leadingContent = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showPhotoOptions = false
                            galleryPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Камера") },
                        leadingContent = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showPhotoOptions = false
                            runCatching { viewModel.createCameraCaptureUri() }
                                .onSuccess { uri ->
                                    pendingCameraUri = uri
                                    try {
                                        camera.launch(uri)
                                    } catch (_: ActivityNotFoundException) {
                                        pendingCameraUri = null
                                        viewModel.discardCameraCapture(uri)
                                        Toast.makeText(
                                            context,
                                            "Не найдено приложение камеры",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                                .onFailure { error ->
                                    Toast.makeText(
                                        context,
                                        error.message ?: "Не удалось запустить камеру",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPhotoOptions = false }) { Text("Отмена") }
            }
        )
    }
}
