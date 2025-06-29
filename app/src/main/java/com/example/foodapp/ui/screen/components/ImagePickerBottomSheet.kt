package com.example.foodapp.ui.screen.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.foodapp.utils.ImageUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ImagePickerBottomSheet(
    onDismiss: () -> Unit,
    onImageSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val galleryPermissionState = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        null // API < 33 không cần
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { onImageSelected(it) }
            onDismiss()
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                imageUri.value?.let { onImageSelected(it) }
            }
            onDismiss()
        }

    fun launchCameraWithNewUri() {
        val uri = ImageUtils.createImageUri(context)
        imageUri.value = uri
        uri?.let { cameraLauncher.launch(it) }
    }


        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Chọn ảnh", fontSize = 20.sp, style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary)


                // Button chọn từ thư viện
                AppButton(
                    onClick = {
                        if (galleryPermissionState != null) {
                            if (galleryPermissionState.status.isGranted) {
                                galleryLauncher.launch("image/*")
                            } else {
                                galleryPermissionState.launchPermissionRequest()
                            }
                        } else {
                            galleryLauncher.launch("image/*")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Chọn ảnh từ thư viện"
                )




                // Button chụp ảnh
                AppButton(
                    onClick = {
                        if (cameraPermissionState.status.isGranted) {
                            launchCameraWithNewUri()
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Chụp ảnh"
                )

                AppButton(
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    text = "Hủy",
                    backgroundColor = MaterialTheme.colorScheme.outline
                )

            }
        }


    LaunchedEffect(cameraPermissionState.status, galleryPermissionState?.status) {
        when {
            cameraPermissionState.status.shouldShowRationale -> {
                Toast.makeText(
                    context,
                    "Ứng dụng cần quyền máy ảnh để chụp ảnh",
                    Toast.LENGTH_SHORT
                ).show()
            }

            galleryPermissionState?.status?.shouldShowRationale == true -> {
                Toast.makeText(
                    context,
                    "Ứng dụng cần quyền đọc ảnh để chọn từ thư viện",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}