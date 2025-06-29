package com.se114.foodapp.ui.screen.supplier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Supplier
import com.se114.foodapp.data.dto.filter.SupplierFilter
import com.se114.foodapp.domain.use_case.supplier.AddSupplierUseCase
import com.se114.foodapp.domain.use_case.supplier.GetSupplierUseCase
import com.se114.foodapp.domain.use_case.supplier.UpdateStatusSupplierUseCase
import com.se114.foodapp.domain.use_case.supplier.UpdateSupplierUseCase
import com.se114.foodapp.ui.screen.supplier.SupplierState.GetSupplierState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
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


    fun getSuppliers() {
        viewModelScope.launch {
            getSupplierUseCase.invoke(_uiState.value.filter).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update {
                            it.copy(
                                getSupplierState = GetSupplierState.Success,
                                suppliers = response.data
                            )
                        }
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                getSupplierState = GetSupplierState.Error(response.errorMessage)
                            )
                        }
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(getSupplierState = GetSupplierState.Loading) }
                    }

                }
            }
        }
    }


    private fun addSupplier() {
        viewModelScope.launch {
            addSupplierUseCase.invoke(_uiState.value.supplierSelected).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(isLoading = false,
                            suppliers = it.suppliers + response.data) }
                        _event.send(SupplierState.Event.ShowToastSuccess("Thêm thành công"))


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
                        _uiState.update { it.copy(isLoading = false,
                            suppliers = it.suppliers.map { supplier ->
                                if (supplier.id == response.data.id) response.data else supplier
                            }) }
                        _event.send(SupplierState.Event.ShowToastSuccess("Cập nhật thành công"))

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
                            _event.send(SupplierState.Event.ShowToastSuccess("Cập nhật trạng thái thành công"))
                            onAction(SupplierState.Action.OnRefresh)
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
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(
                            shouldRefresh = UUID.randomUUID().toString()
                        )
                    )
                }
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

            is SupplierState.Action.OnStatusFilterChange -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(
                            isActive = action.status
                        )
                    )
                }
            }

            is SupplierState.Action.OnNameSearchChange -> {
                _uiState.update {
                    it.copy(
                        nameSearch = action.name
                    )
                }
            }

            SupplierState.Action.OnSearchFilterChange -> {
                _uiState.update {
                    it.copy(
                        filter = it.filter.copy(
                            name = _uiState.value.nameSearch
                        )
                    )
                }
            }


        }
    }


}

object SupplierState {
    data class UiState(
        val filter: SupplierFilter = SupplierFilter(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val supplierSelected: Supplier = Supplier(),
        val isUpdating: Boolean = false,
        val isHide: Boolean = false,
        val getSupplierState: GetSupplierState = GetSupplierState.Loading,
        val suppliers: List<Supplier> = emptyList(),
        val nameSearch: String = "",
    )

    sealed interface GetSupplierState {
        data object Loading : GetSupplierState
        data object Success : GetSupplierState
        data class Error(val message: String) : GetSupplierState
    }

    sealed interface Event {
        data object OnBack : Event
        data object ShowError : Event
        data class ShowToastSuccess(val message: String) : Event

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
        data class OnStatusFilterChange(val status: Boolean) : Action
        data class OnNameSearchChange(val name: String) : Action
        data object OnSearchFilterChange : Action

    }
}