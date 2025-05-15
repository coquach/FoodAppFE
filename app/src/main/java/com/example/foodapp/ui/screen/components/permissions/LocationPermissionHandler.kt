package com.example.foodapp.ui.screen.components.permissions

import android.Manifest
import android.location.Location
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionHandler(
    content: @Composable () -> Unit,
    onPermissionDenied: () -> Unit = {},
    rationaleTitle: String = "Cần quyền truy cập vị trí",
    rationaleMessage: String = "Ứng dụng cần quyền vị trí để hoạt động chính xác. Vui lòng cấp quyền.",

) {
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var showRationaleDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }

    // Xử lý khi cần hiển thị rationale
    if (permissionsState.shouldShowRationale) {
        showRationaleDialog = true
    }

    if (permissionsState.shouldShowRationale && !permissionsState.allPermissionsGranted) {
        showRationaleDialog = true
    }

    if (permissionsState.allPermissionsGranted) {
        content()
    }

    // Dialog hiển thị lý do
    if (showRationaleDialog) {
        FoodAppDialog(
            title = rationaleTitle,
            message = rationaleMessage,
            onDismiss = {
                showRationaleDialog = false
                onPermissionDenied
            },
            containerConfirmButtonColor = MaterialTheme.colorScheme.primary,
            labelConfirmButtonColor = MaterialTheme.colorScheme.onPrimary,
            onConfirm = {
                showRationaleDialog = false
                permissionsState.launchMultiplePermissionRequest()
            },
            confirmText = "Đồng ý",
            dismissText = "Huỷ"
        )
    }
}
fun MapViewportState.flyToLocation(
    latitude: Double,
    longitude: Double,
    zoom: Double = 15.0
) {
    this.flyTo(
        cameraOptions = CameraOptions.Builder()
            .center(Point.fromLngLat(longitude, latitude))
            .zoom(zoom)
            .build()
    )
}

@Composable
fun MapControlButtons(
    modifier: Modifier = Modifier,
    currentZoom: Double,
    currentLocation: Location?,
    onZoomIn: (Double) -> Unit,
    onZoomOut: (Double) -> Unit,
    onFlyToLocation: (latitude: Double, longitude: Double, zoom: Double) -> Unit
) {
    Column(
        modifier = modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Nút Zoom In
        FloatingActionButton(
            onClick = { onZoomIn(currentZoom) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Zoom In")
        }


        FloatingActionButton(
            onClick = { onZoomOut(currentZoom) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
        }


        FloatingActionButton(
            onClick = {
                currentLocation?.let {
                    onFlyToLocation(it.latitude, it.longitude, currentZoom)
                }
            },
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "My location")
        }
    }
}

