//package com.se114.foodapp.ui.screen.tracking
//
//import android.location.Location
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.foodapp.data.dto.ApiResponse
//import com.example.foodapp.data.dto.safeApiCall
//import com.example.foodapp.data.remote.OsrmApi
//import com.example.foodapp.location.LocationManager
//import com.mapbox.geojson.Point
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.flow.distinctUntilChanged
//import kotlinx.coroutines.flow.filterNotNull
//import kotlinx.coroutines.flow.receiveAsFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class TrackingViewModel @Inject constructor(
//    private val orsApi: OsrmApi,
//    private val locationManager: LocationManager
//) :ViewModel() {
//    private val _uiState = MutableStateFlow<TrackingState>(TrackingState.Nothing)
//    val uiState = _uiState.asStateFlow()
//
//    private val _event = Channel<TrackingEvent>()
//    val event = _event.receiveAsFlow()
//
//    private val _route = MutableStateFlow<List<Point>>(emptyList())
//    val route = _route.asStateFlow()
//    private val _currentLocation = MutableStateFlow<Location?>(null)
//    val currentLocation = _currentLocation.asStateFlow()
//
//    val destinationPoint: Point = Point.fromLngLat(106.695833, 10.776889)
//    init {
//        viewModelScope.launch(Dispatchers.IO) {
//            locationManager.locationUpdate.filterNotNull()
//                .distinctUntilChanged().collectLatest {
//                    _currentLocation.value = it
//                    getRoute(Point.fromLngLat(it.longitude, it.latitude), destinationPoint)
//                }
//            locationManager.startLocationUpdate()
//        }
//
//
//    }
//    private fun getRoute(start: Point, end: Point) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val coordinateString = "${start.longitude()},${start.latitude()};${end.longitude()},${end.latitude()}"
//                val response = safeApiCall { orsApi.getRoute(coordinateString) }
//                when (response) {
//                    is ApiResponse.Success -> {
//
//                        val routeCoordinates = response.body?.routes
//                            ?.firstOrNull()
//                            ?.geometry
//                            ?.coordinates
//                            ?.map { Point.fromLngLat(it[0], it[1]) }
//
//                        if (routeCoordinates != null) {
//                            _route.value = routeCoordinates
//                            Log.d("OSRM", "Đã lấy route thành công với ${routeCoordinates.size} điểm.")
//                        } else {
//                            Log.e("OSRM", "Không có route.")
//                        }
//
//                    }
//
//                    is ApiResponse.Error -> {
//                        Log.e("OSRM", "Lỗi get route by OSRM: ${response.message}")
//                    }
//
//                    else -> {
//                        Log.e("OSRM", "Lỗi get route by OSRM: Không xác định")
//                    }
//                }
//            }catch (e: Exception){
//                Log.e("OSRM", "Lỗi get route by OSRM", e)
//            }
//
//
//        }
//    }
//    override fun onCleared() {
//        super.onCleared()
//        locationManager.stopLocationUpdate()
//    }
//    sealed class TrackingEvent {
//        data object NavigateToAddressList : TrackingEvent()
//
//    }
//
//    sealed class TrackingState {
//        data object Nothing : TrackingState()
//        data object Loading : TrackingState()
//        data object Success : TrackingState()
//        data class Error(val message: String) : TrackingState()
//    }
//}