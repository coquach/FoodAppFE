package com.se114.foodapp.ui.screen.address.addAddress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.foodapp.ui.screen.components.permissions.LocationPermissionHandler
import com.example.foodapp.ui.screen.components.permissions.MapControlButtons
import com.example.foodapp.ui.screen.components.permissions.flyToLocation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions


import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation

import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddAddressScreen(
    navController: NavController,
    viewModel: AddAddressViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showErrorSheet by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                is AddAddress.Event.BackToAddressList -> {
                    val address = it.address
                    navController.previousBackStackEntry?.savedStateHandle?.set("address", address)
                    navController.popBackStack()
                }

                AddAddress.Event.OnBack -> {
                    navController.popBackStack()
                }

                AddAddress.Event.ShowError -> {
                    showErrorSheet = true
                }
            }
        }
    }

    LocationPermissionHandler(
        content = {
            var selectedLocation by remember { mutableStateOf<Point?>(null) }

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

            val currentLocation by viewModel.currentLocation.collectAsState(initial = null)

            LaunchedEffect(currentLocation) {
                currentLocation?.let {
                    viewModel.onAction(AddAddress.Action.OnReverseGeocode(it.latitude, it.longitude))
                    mapViewportState.flyToLocation(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        zoom = currentZoom
                    )
                }
            }
            LaunchedEffect(selectedLocation) {
                selectedLocation?.let { point ->
                    val lat = point.latitude()
                    val lon = point.longitude()
                    viewModel.onAction(AddAddress.Action.OnReverseGeocode(lat, lon))
                    mapViewportState.flyToLocation(
                        latitude = lat,
                        longitude = lon,
                        zoom = currentZoom
                    )
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                MapboxMap(
                    modifier = Modifier.fillMaxSize(),
                    mapViewportState = mapViewportState,
                    onMapClickListener = { clickedPoint ->
                        selectedLocation = clickedPoint
                        true
                    },
                    style = { MapStyle(style = "mapbox://styles/mapbox/streets-v12") }
                ) {
                    if (selectedLocation != null) {
                        PointAnnotation(point = selectedLocation!!) {
                            iconImage = marker
                        }
                    }


                    MapEffect(Unit) { mapView ->


                        mapView.location.updateSettings {
                            locationPuck = createDefault2DPuck(withBearing = false)
                            puckBearing = PuckBearing.HEADING
                            puckBearingEnabled = true
                            enabled = true
                        }

                    }

                }


                FloatingActionButton(
                    onClick = {
                        navController.navigateUp()
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
                        viewModel.onAction(AddAddress.Action.OnReverseGeocode(lat, lon))
                        selectedLocation = Point.fromLngLat(lon, lat)
                        mapViewportState.flyToLocation(
                            latitude = lat,
                            longitude = lon,
                            zoom = zoom
                        )
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 25.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(12.dp)
                            )
                            .shadow(4.dp, RoundedCornerShape(12.dp), clip = true)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = uiState.address?.formatAddress.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.onAction(AddAddress.Action.OnAddAddress(uiState.address!!))
                        },
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)

                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddLocationAlt,
                            contentDescription = "Send Location",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(30.dp)
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

