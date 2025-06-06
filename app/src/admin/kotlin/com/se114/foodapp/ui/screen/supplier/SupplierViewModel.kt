package com.se114.foodapp.ui.screen.supplier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.OrderFilter
import com.se114.foodapp.data.dto.filter.SupplierFilter
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.Supplier
import com.example.foodapp.data.model.enums.OrderStatus
import com.se114.foodapp.domain.use_case.supplier.AddSupplierUseCase
import com.se114.foodapp.domain.use_case.supplier.GetSupplierUseCase
import com.se114.foodapp.domain.use_case.supplier.UpdateStatusSupplierUseCase
import com.se114.foodapp.domain.use_case.supplier.UpdateSupplierUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SupplierViewModel @Inject constructor(
    private val getSupplierUseCase: GetSupplierUseCase,
    private val addSupplierUseCase: AddSupplierUseCase,
    private val updateSupplierUseCase: UpdateSupplierUseCase,
    private val updateStatusSupplierUseCase: UpdateStatusSupplierUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SupplierState.UiState())
    val uiState: StateFlow<SupplierState.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<SupplierState.Event>()
    val event get() = _event.receiveAsFlow()


    private val suppliersCache = mutableMapOf<Int, StateFlow<PagingData<Supplier>>>()

    private fun refreshAllTabs() {
        suppliersCache.clear()
    }

    fun getSuppliersByTab(index: Int): StateFlow<PagingData<Supplier>> {
        return suppliersCache.getOrPut(index) {
            val status = when (index) {
                0 -> true
                1 -> false
                else -> true
            }

            val filter = SupplierFilter(isActive = status)

            getSupplierUseCase.invoke(filter)
                .cachedIn(viewModelScope)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    PagingData.empty()
                )
        }
    }


    private fun addSupplier() {
        viewModelScope.launch {
            addSupplierUseCase.invoke(_uiState.value.supplierSelected).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _event.send(SupplierState.Event.Refresh)


                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(SupplierState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun updateSupplier() {
        viewModelScope.launch {
            updateSupplierUseCase.invoke(_uiState.value.supplierSelected).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _event.send(SupplierState.Event.Refresh)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(SupplierState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun setActiveSupplier(isActive: Boolean) {
        viewModelScope.launch {
            updateStatusSupplierUseCase.invoke(_uiState.value.supplierSelected.id!!, isActive)
                .collect { response ->
                    when (response) {
                        is ApiResponse.Success -> {
                            _uiState.update { it.copy(isLoading = false) }
                            _event.send(SupplierState.Event.Refresh)
                        }

                        is ApiResponse.Failure -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = response.errorMessage
                                )
                            }
                            _event.send(SupplierState.Event.ShowError)
                        }

                        is ApiResponse.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }

        }
    }


    fun onAction(action: SupplierState.Action) {
        when (action) {
            SupplierState.Action.AddSupplier -> {
                addSupplier()
            }

            SupplierState.Action.UpdateSupplier -> {
                updateSupplier()
            }

            is SupplierState.Action.SetActiveSupplier -> {
                setActiveSupplier(
                    action.isActive
                )
            }

            is SupplierState.Action.OnNameChange -> {
                _uiState.update {
                    it.copy(
                        supplierSelected = _uiState.value.supplierSelected.copy(
                            name = action.name
                        )
                    )
                }
            }

            is SupplierState.Action.OnPhoneChange -> {
                _uiState.update {
                    it.copy(
                        supplierSelected = _uiState.value.supplierSelected.copy(
                            phone = action.phone
                        )
                    )
                }
            }

            is SupplierState.Action.OnEmailChange -> {
                _uiState.update {
                    it.copy(
                        supplierSelected = _uiState.value.supplierSelected.copy(
                            email = action.email
                        )
                    )
                }
            }

            is SupplierState.Action.OnAddressChange -> {
                _uiState.update {
                    it.copy(
                        supplierSelected = _uiState.value.supplierSelected.copy(
                            address = action.address
                        )
                    )
                }
            }

            is SupplierState.Action.OnUpdateStatus -> {
                _uiState.update {
                    it.copy(
                        isUpdating = action.isUpdating
                    )
                }
            }

            is SupplierState.Action.OnUpdateHide -> {
                _uiState.update {
                    it.copy(
                        isHide = action.isHide
                    )
                }
            }

            SupplierState.Action.OnRefresh -> {
                refreshAllTabs()
            }

            is SupplierState.Action.OnSupplierSelected -> {
                _uiState.update {
                    it.copy(
                        supplierSelected = action.supplier
                    )
                }
            }

            SupplierState.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(SupplierState.Event.OnBack)
                }
            }

            is SupplierState.Action.OnTabSelected -> {
                _uiState.update { it.copy(tabIndex = action.index) }
            }

        }
    }


}

object SupplierState {
    data class UiState(
        val tabIndex: Int = 0,
        val isLoading: Boolean = false,
        val error: String? = null,
        val supplierSelected: Supplier = Supplier(),
        val isUpdating: Boolean = false,
        val isHide: Boolean = false,
    )

    sealed interface Event {
        data object OnBack : Event
        data object ShowError : Event
        data object Refresh : Event

    }

    sealed interface Action {
        data class OnNameChange(val name: String) : Action
        data class OnPhoneChange(val phone: String) : Action
        data class OnEmailChange(val email: String) : Action
        data class OnAddressChange(val address: String) : Action
        data object AddSupplier : Action
        data object UpdateSupplier : Action
        data class SetActiveSupplier(val isActive: Boolean) : Action
        data class OnUpdateStatus(val isUpdating: Boolean) : Action
        data class OnUpdateHide(val isHide: Boolean) : Action
        data class OnSupplierSelected(val supplier: Supplier) : Action
        data object OnBack : Action
        data object OnRefresh : Action
        data class OnTabSelected(val index: Int) : Action

    }
}