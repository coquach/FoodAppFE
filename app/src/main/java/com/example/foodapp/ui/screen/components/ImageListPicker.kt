package com.example.foodapp.ui.screen.components

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.foodapp.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageListPicker(
    modifier: Modifier = Modifier,
    imageList: List<Uri>,
    onUpdateImages: (List<Uri>) -> Unit
) {

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            onUpdateImages(uris)
        }
    )
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission = permission)

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)
    val full = LocalConfiguration.current.screenWidthDp.dp
    val pad = (full - 200.dp) / 2

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (imageList.isEmpty()) {
            ImagesPlaceholder(
                modifier = Modifier.align(Alignment.Center)
            ) {
                if (permissionState.status.isGranted) {
                    multiplePhotoPickerLauncher.launch(arrayOf("image/*"))
                } else {
                    permissionState.launchPermissionRequest()
                }
            }
        } else {
            // Trường hợp đã có ảnh
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                state = lazyListState,
                flingBehavior = snapBehavior,
                contentPadding = PaddingValues(horizontal = pad)
            ) {
                items(imageList) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Selected image",
                        modifier = Modifier
                            .size(200.dp)
                            .shadow(8.dp, shape = RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Nút cuối cùng để thêm ảnh
                item {
                    ImagesPlaceholder(
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        if (permissionState.status.isGranted) {
                            multiplePhotoPickerLauncher.launch(arrayOf("image/*"))
                            scope.launch {
                                lazyListState.animateScrollToItem(0)
                            }
                        } else {
                            permissionState.launchPermissionRequest()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImagesPlaceholder(
    modifier: Modifier = Modifier,
    onClick: () ->Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(100.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        ),
        contentPadding = PaddingValues(20.dp),
    ){
        Image(
            painter = painterResource(id = R.drawable.ic_images) ,
            contentDescription = "Add Images",
            colorFilter = ColorFilter.tint(Color.White)

        )
    }
}