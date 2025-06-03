package com.example.foodapp.ui.screen.components.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionHandler(
    content: @Composable () -> Unit,
    onPermissionDenied: () -> Unit = {},

) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    var showRationaleDialog by remember { mutableStateOf(false) }
    var allPermissionsGranted by remember { mutableStateOf(false) }

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
            showRationaleDialog = true
        }
    }

    LaunchedEffect(Unit) {
        val allGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            allPermissionsGranted = true
        } else {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    if (allPermissionsGranted) {
        content()
    }

    if (showRationaleDialog) {
        FoodAppDialog(
            title = "Cần quyền truy cập vị trí",
            message = "Ứng dụng cần quyền vị trí để hoạt động chính xác. Vui lòng cấp quyền.",
            onDismiss = {
                showRationaleDialog = false
                onPermissionDenied()
            },
            containerConfirmButtonColor = MaterialTheme.colorScheme.primary,
            labelConfirmButtonColor = MaterialTheme.colorScheme.onPrimary,
            onConfirm = {
                showRationaleDialog = false
                permissionLauncher.launch(permissions.toTypedArray())
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

fun android.content.Context.findActivity(): android.app.Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is android.app.Activity) return context
        context = context.baseContext
    }
    return null
}