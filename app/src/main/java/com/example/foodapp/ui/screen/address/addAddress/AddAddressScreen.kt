package com.example.foodapp.ui.screen.address.addAddress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun AddAddressScreen(
    navController: NavController,
    viewModel: AddAddressViewModel = hiltViewModel()
) {
    val location = viewModel.getLocation().collectAsStateWithLifecycle(initialValue = null)
    Column {
        location.value?.let {
            val cameraState = rememberCameraPositionState()
            cameraState.position =
                CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 13f)
            GoogleMap(
                cameraPositionState = cameraState,
                modifier = Modifier.fillMaxSize()
            )
        }

    }
}