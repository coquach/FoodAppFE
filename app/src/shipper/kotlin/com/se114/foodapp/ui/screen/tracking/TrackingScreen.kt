package com.se114.foodapp.ui.screen.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.ui.screen.components.DetailsTextRow
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.permissions.LocationPermissionHandler
import com.example.foodapp.ui.screen.components.permissions.MapControlButtons
import com.example.foodapp.ui.screen.components.permissions.flyToLocation
import com.example.foodapp.utils.StringUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    navController: NavController,
    viewModel: TrackingViewModel = hiltViewModel(),
) {
    val uiSate by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentLocation by viewModel.currentLocation.collectAsStateWithLifecycle()
    val route by viewModel.route.collectAsStateWithLifecycle()
    val destinationPoint = viewModel.destinationPoint
    val distance by viewModel.distance.collectAsStateWithLifecycle()
    val duration by viewModel.duration.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                TrackingViewModel.TrackingEvent.BackToOrderDetails -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "distance",
                        distance
                    )
                    navController.popBackStack()
                }
            }
        }
    }
    LocationPermissionHandler(
        content = {
            val marker = rememberIconImage(
                key = "markerResourceId",
                painter = painterResource(R.drawable.red_marker)
            )

            var currentZoom by remember { mutableDoubleStateOf(16.0) }
            val mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(currentZoom)
                }
            }

            LaunchedEffect(mapViewportState.cameraState?.zoom) {
                mapViewportState.cameraState?.zoom?.let {
                    currentZoom = it
                }
            }
            var hasFlown by remember { mutableStateOf(false) }

            LaunchedEffect(currentLocation) {
                if (!hasFlown && currentLocation != null) {
                    mapViewportState.flyToLocation(
                        latitude = currentLocation!!.latitude,
                        longitude = currentLocation!!.longitude,
                        zoom = currentZoom
                    )
                    hasFlown = true
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                MapboxMap(
                    modifier = Modifier.fillMaxSize(),
                    mapViewportState = mapViewportState,

                    style = { MapStyle(style = "mapbox://styles/mapbox/streets-v12") }
                ) {

                    PointAnnotation(point = destinationPoint) {
                        iconImage = marker
                    }


                    var polylineManagerRef by remember {
                        mutableStateOf<PolylineAnnotationManager?>(
                            null
                        )
                    }
                    var currentPolyline by remember {
                        mutableStateOf<List<PolylineAnnotation>>(
                            emptyList()
                        )
                    }

                    MapEffect(Unit) { mapView ->

                        val manager = mapView.annotations.createPolylineAnnotationManager()
                        polylineManagerRef = manager
                        mapView.location.updateSettings {
                            locationPuck = createDefault2DPuck(withBearing = false)

                            enabled = true
                        }

                    }
                    LaunchedEffect(route) {
                        val manager = polylineManagerRef ?: return@LaunchedEffect

                        if (currentPolyline.isNotEmpty()) {
                            manager.delete(currentPolyline)
                        }

                        if (route.isNotEmpty()) {
                            val polylineOptions = PolylineAnnotationOptions()
                                .withPoints(route)
                                .withLineColor("#FF8C42")
                                .withLineWidth(5.0)

                            currentPolyline = listOf(manager.create(polylineOptions))
                        }
                    }
                }



                MapControlButtons(
                    modifier = Modifier.align(Alignment.TopEnd),
                    currentZoom = currentZoom,
                    currentLocation = currentLocation,
                    onZoomIn = { zoom ->
                        mapViewportState.setCameraOptions(
                            CameraOptions.Builder().zoom(zoom + 1).build()
                        )
                    },
                    onZoomOut = { zoom ->
                        mapViewportState.setCameraOptions(
                            CameraOptions.Builder().zoom(zoom - 1).build()
                        )
                    },
                    onFlyToLocation = { lat, lon, zoom ->
                        mapViewportState.flyToLocation(
                            latitude = lat,
                            longitude = lon,
                            zoom = zoom
                        )
                    }
                )
                FloatingActionButton(
                    onClick = {
                        viewModel.onBackToOrderDetails()
                    },
                    modifier = Modifier
                        .padding(vertical = 30.dp, horizontal = 16.dp)
                        .size(50.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(12.dp),
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary,

                    ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back"
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 25.dp)
                        .align(Alignment.BottomCenter),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .shadow(4.dp, RoundedCornerShape(12.dp), clip = true)
                            .background(MaterialTheme.colorScheme.outline)
                            .padding(10.dp)
                    ) {
                        DetailsTextRow(
                            text = "Thời gian dự kiến: ${StringUtils.formatDurationDetailed(duration ?: 0.0)}",
                            icon = Icons.Default.Timer,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))

                            .shadow(4.dp, RoundedCornerShape(12.dp), clip = true)
                            .background(MaterialTheme.colorScheme.outline)
                            .padding(10.dp)
                    ) {
                        DetailsTextRow(
                            text = "Khoảng cách: ${StringUtils.formatDistance(distance ?: 0.0)}",
                            icon = Icons.Default.Navigation,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

            }
        },
        onPermissionDenied = {
            navController.popBackStack()
        }

    )

}