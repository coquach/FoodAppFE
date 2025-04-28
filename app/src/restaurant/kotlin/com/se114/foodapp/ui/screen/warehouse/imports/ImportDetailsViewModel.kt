package com.se114.foodapp.ui.screen.warehouse.imports

import androidx.lifecycle.ViewModel
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.request.ImportDetailRequest
import com.example.foodapp.data.dto.request.ImportRequest
import com.example.foodapp.data.remote.FoodApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class ImportDetailsViewModel @Inject constructor(
    private val foodApi: FoodApi
): BaseViewModel() {
    private  val _uiState = MutableStateFlow<ImportDetailsState>(ImportDetailsState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<ImportDetailsEvents>()
    val event = _event.receiveAsFlow()

    private val _suppliers = Mu

    private val _importRequest = MutableStateFlow(
        ImportRequest(
            supplierId = null,
            staffId = null,
            importDate = "",
            importDetails = emptyList()
        )
    )
    val importRequest = _importRequest.asStateFlow()

    private val _importDetailsRequest = MutableStateFlow<List<ImportDetailRequest>>(emptyList())
    val importDetailsRequest = _importDetailsRequest.asStateFlow()



    sealed class ImportDetailsState{
        data object Nothing: ImportDetailsState()
        data object Loading: ImportDetailsState()
        data object Success: ImportDetailsState()
        data class Error(val message: String): ImportDetailsState()
    }
    sealed class ImportDetailsEvents {
        data object GoBack : ImportDetailsEvents()
    }
}