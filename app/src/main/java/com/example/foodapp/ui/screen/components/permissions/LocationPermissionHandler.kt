package com.example.foodapp.ui.screen.components.permissions

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import kotlinx.coroutines.delay

@Composable
fun LocationPermissionHandler(
    content: @Composable () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showNeverAskAgainDialog by remember { mutableStateOf(false) }
    var allPermissionsGranted by remember { mutableStateOf(false) }
    var shouldRequestPermission by remember { mutableStateOf(false) }

    val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val granted = permissionsResult.values.all { it }
        allPermissionsGranted = granted

        if (!granted) {
            // Check xem có đang bị "Never ask again" không
            val shouldShowRationale = permissions.any { permission ->
                androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity, permission
                )
            }
            showRationaleDialog = shouldShowRationale
            showNeverAskAgainDialog = !shouldShowRationale
        } else {
            showRationaleDialog = false
            showNeverAskAgainDialog = false

        }
        shouldRequestPermission = false
    }

    LaunchedEffect(Unit) {
        val allGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) {
            allPermissionsGranted = true
        } else{
                permissionLauncher.launch(permissions.toTypedArray())
            }

    }

    LaunchedEffect(shouldRequestPermission) {
        if (shouldRequestPermission) {
            permissionLauncher.launch(permissions.toTypedArray())
            shouldRequestPermission = false
        }
    }

    if (allPermissionsGranted) {
        content()
    }

    if (showRationaleDialog) {
        FoodAppDialog(
            title = "Cần quyền truy cập vị trí",
            titleColor = MaterialTheme.colorScheme.error,
            message = "Ứng dụng cần quyền vị trí để hoạt động chính xác. Vui lòng cấp quyền.",
            onDismiss = {
                showRationaleDialog = false
                onPermissionDenied()
            },
            onConfirm = {
                showRationaleDialog = false
                shouldRequestPermission = true
            },
            confirmText = "Đồng ý",
            dismissText = "Huỷ"
        )
    }

    if (showNeverAskAgainDialog) {
        FoodAppDialog(
            title = "Quyền bị từ chối vĩnh viễn",
            titleColor = MaterialTheme.colorScheme.error,
            message = "Bạn đã từ chối quyền truy cập vị trí và chọn không hỏi lại. Vui lòng vào Cài đặt để bật quyền thủ công.",
            onDismiss = {
                showNeverAskAgainDialog = false
                onPermissionDenied()
            },
            onConfirm = {
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
                showNeverAskAgainDialog = false
            },
            confirmText = "Mở Cài đặt",
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

