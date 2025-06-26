package com.se114.foodapp.ui.screen.tracking

import android.location.Location
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow

import com.se114.foodapp.data.remote.OsrmApi
import com.example.foodapp.location.LocationManager
import com.example.foodapp.navigation.Tracking
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.style.expressions.dsl.generated.distance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val osrmApi: OsrmApi,
    private val locationManager: LocationManager,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val arguments = savedStateHandle.toRoute<Tracking>()
    val destinationPoint: Point = Point.fromLngLat(arguments.long, arguments.lat)

    private val _uiState = MutableStateFlow<TrackingState>(TrackingState.Nothing)
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<TrackingEvent>()
    val event get() = _event.receiveAsFlow()
    private val _route = MutableStateFlow<List<Point>>(emptyList())
    val route = _route.asStateFlow()
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation = _currentLocation.asStateFlow()



    private val _distance = MutableStateFlow<Double?>(null)
    val distance = _distance.asStateFlow()
    private val _duration = MutableStateFlow<Double?>(null)
    val duration = _duration.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            locationManager.startLocationUpdate()
            locationManager.locationUpdate.filterNotNull()
                .distinctUntilChanged().collectLatest {
                    _currentLocation.value = it
                    getRoute(Point.fromLngLat(it.longitude, it.latitude), destinationPoint)
                }

        }
    }

    private fun getRoute(start: Point, end: Point) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val coordinateString =
                    "${start.longitude()},${start.latitude()};${end.longitude()},${end.latitude()}"
                apiRequestFlow { osrmApi.getRoute(coordinateString) }.collect { response ->

                    when (response) {
                        is ApiResponse.Loading -> {

                        }

                        is ApiResponse.Success -> {
                            val routeCoordinates = response.data.routes
                                .firstOrNull()
                            val routeGeometry = routeCoordinates?.geometry
                                ?.coordinates
                                ?.map { Point.fromLngLat(it[0], it[1]) }
                            val duration = routeCoordinates?.duration
                            val distance = routeCoordinates?.distance
                            if (routeGeometry != null) {
                               _route.value = routeGeometry
                                _distance.value = distance
                                _duration.value = duration
                                Log.d(
                                    "OSRM",
                                    "Đã lấy route thành công với ${routeGeometry.size} điểm."
                                )
                            } else {
                                Log.e("OSRM", "Không có route.")
                                _uiState.value = TrackingState.Error("Không có route.")
                            }

                        }

                        is ApiResponse.Failure -> {
                           _uiState.value = TrackingState.Error(response.errorMessage)
                        }
                    }

                }
            } catch (e: Exception) {
                Log.e("OSRM", "Lỗi get route by OSRM", e)
            }


        }
    }

   fun onBackToOrderDetails() {
        viewModelScope.launch {
            _event.send(TrackingEvent.BackToOrderDetails)
        }
   }

    override fun onCleared() {
        super.onCleared()
        locationManager.stopLocationUpdate()
    }
    sealed class TrackingEvent {
        data object BackToOrderDetails : TrackingEvent()

    }

    sealed class TrackingState {
        data object Nothing : TrackingState()
        data object Loading : TrackingState()
        data object Success : TrackingState()
        data class Error(val message: String) : TrackingState()
    }

}

