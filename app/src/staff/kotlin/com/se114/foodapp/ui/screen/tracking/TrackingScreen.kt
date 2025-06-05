//package com.se114.foodapp.ui.screen.tracking
//
//import android.Manifest
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.MyLocation
//import androidx.compose.material.icons.filled.Remove
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableDoubleStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.navigation.NavController
//import com.example.foodapp.R
//import com.example.foodapp.ui.screen.components.FoodAppDialog
//import com.example.foodapp.ui.screen.components.permissions.LocationPermissionHandler
//import com.example.foodapp.ui.screen.components.permissions.MapControlButtons
//import com.example.foodapp.ui.screen.components.permissions.flyToLocation
//import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import com.google.accompanist.permissions.rememberMultiplePermissionsState
//import com.mapbox.api.directions.v5.models.Bearing
//import com.mapbox.maps.CameraOptions
//import com.mapbox.maps.extension.compose.MapEffect
//import com.mapbox.maps.extension.compose.MapboxMap
//import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
//import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
//import com.mapbox.maps.extension.compose.annotation.rememberIconImage
//import com.mapbox.maps.extension.compose.style.MapStyle
//import com.mapbox.maps.plugin.PuckBearing
//import com.mapbox.maps.plugin.annotation.annotations
//import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation
//import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
//import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
//import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
//import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
//import com.mapbox.maps.plugin.locationcomponent.location
//
//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun TrackingScreen(
//    navController: NavController,
//    viewModel: TrackingViewModel = hiltViewModel()
//){
//    LocationPermissionHandler(
//        content = {
//            val marker = rememberIconImage(
//                key = "markerResourceId",
//                painter = painterResource(R.drawable.red_marker)
//            )
//
//            var currentZoom by remember { mutableDoubleStateOf(16.0) }
//            val mapViewportState = rememberMapViewportState{
//                setCameraOptions {
//                    zoom(currentZoom)
//                }
//            }
//
//            LaunchedEffect(mapViewportState.cameraState?.zoom) {
//                mapViewportState.cameraState?.zoom?.let {
//                    currentZoom = it
//                }
//            }
//            var hasFlown by remember { mutableStateOf(false) }
//            val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
//val route by viewModel.route.collectAsStateWithLifecycle()
//            val destinationPoint = viewModel.destinationPoint
//            LaunchedEffect(currentLocation) {
//                if (!hasFlown && currentLocation != null) {
//                    mapViewportState.flyToLocation(
//                        latitude = currentLocation!!.latitude,
//                        longitude = currentLocation!!.longitude,
//                        zoom = currentZoom
//                    )
//                    hasFlown = true
//                }
//            }
//            Box(modifier = Modifier.fillMaxSize()) {
//                MapboxMap(
//                    modifier = Modifier.fillMaxSize(),
//                    mapViewportState = mapViewportState,
//
//                    style = { MapStyle(style = "mapbox://styles/mapbox/streets-v12") }
//                ) {
//
//                    PointAnnotation(point =destinationPoint) {
//                        iconImage = marker
//                    }
//                }
//
//                var polylineManagerRef by remember { mutableStateOf<PolylineAnnotationManager?>(null) }
//                var currentPolyline by remember { mutableStateOf<List<PolylineAnnotation>>(emptyList()) }
//
//                MapEffect(Unit) { mapView ->
//
//                    val manager = mapView.annotations.createPolylineAnnotationManager()
//                    polylineManagerRef = manager
//                    mapView.location.updateSettings {
//                        locationPuck = createDefault2DPuck(withBearing = false)
//
//                        enabled = true
//                    }
//
//                }
//                LaunchedEffect(route) {
//                    val manager = polylineManagerRef ?: return@LaunchedEffect
//
//                    if (currentPolyline.isNotEmpty()) {
//                        manager.delete(currentPolyline)
//                    }
//
//                    if (route.isNotEmpty()) {
//                        val polylineOptions = PolylineAnnotationOptions()
//                            .withPoints(route)
//                            .withLineColor("#0000FF")
//                            .withLineWidth(5.0)
//
//                        currentPolyline = listOf(manager.create(polylineOptions))
//                    }
//                }
//
//
//
//                MapControlButtons(
//                    modifier = Modifier.align(Alignment.TopEnd),
//                    currentZoom = currentZoom,
//                    currentLocation = currentLocation,
//                    onZoomIn = { zoom ->
//                        mapViewportState.setCameraOptions(
//                            CameraOptions.Builder().zoom(zoom + 1).build()
//                        )
//                    },
//                    onZoomOut = { zoom ->
//                        mapViewportState.setCameraOptions(
//                            CameraOptions.Builder().zoom(zoom - 1).build()
//                        )
//                    },
//                    onFlyToLocation = { lat, lon, zoom ->
//                        mapViewportState.flyToLocation(
//                            latitude = lat,
//                            longitude = lon,
//                            zoom = zoom
//                        )
//                    }
//                )
//
//            }
//        },
//        onPermissionDenied = {
//            navController.popBackStack()
//        }
//    )
//}