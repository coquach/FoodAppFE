package com.se114.foodapp.ui.screen.warehouse.imports


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.StaffFilter
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Ingredient
import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.model.Supplier
import com.example.foodapp.domain.use_case.staff.GetStaffUseCase
import com.example.foodapp.navigation.ImportDetails
import com.example.foodapp.navigation.importNavType
import com.se114.foodapp.data.dto.filter.SupplierFilter
import com.se114.foodapp.data.mapper.toImportDetailUiModel
import com.se114.foodapp.domain.use_case.imports.CreateImportUseCase
import com.se114.foodapp.domain.use_case.imports.UpdateImportUseCase
import com.se114.foodapp.domain.use_case.ingredient.GetActiveIngredientsUseCase
import com.se114.foodapp.domain.use_case.supplier.GetSupplierUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import kotlin.reflect.typeOf

@HiltViewModel
class ImportDetailsViewModel @Inject constructor(
    private val createImportUseCase: CreateImportUseCase,
    private val updateImportUseCase: UpdateImportUseCase,
    private val getStaffUseCase: GetStaffUseCase,
    private val getSupplierUseCase: GetSupplierUseCase,
    private val getIngredientUseCase: GetActiveIngredientsUseCase,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val arguments = savedStateHandle.toRoute<ImportDetails>(
        typeMap = mapOf( typeOf<com.example.foodapp.data.model.Import>() to importNavType)
    )
    private val import = arguments.import
    private val importDetails = import.importDetails.map{
        it.toImportDetailUiModel()
    }
    private val mode = arguments.isUpdating
    private val isEditable = import.importDate?.plusDays(3)?.isAfter(LocalDate.now()) == true


    private val _uiState =
        MutableStateFlow(ImportDetailsState.UiState(import = import, isUpdating = mode, isEditable = isEditable, importDetails = importDetails))
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<ImportDetailsState.Event>()
    val event get() = _event.receiveAsFlow()

    val suppliers: StateFlow<PagingData<Supplier>> = getSupplierUseCase.invoke(SupplierFilter()).cachedIn(viewModelScope).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PagingData.empty()
    )
    val staffs: StateFlow<PagingData<Staff>> = getStaffUseCase.invoke(StaffFilter()).cachedIn(viewModelScope).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PagingData.empty()
    )
    init {
        getIngredients()
    }

    private fun getIngredients() {
        viewModelScope.launch(Dispatchers.IO) {
            getIngredientUseCase.invoke().catch {  }.collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(ingredients = response.data) }
                    }
                    is ApiResponse.Failure -> {
                        _uiState.update { it.copy(error = response.errorMessage) }
                        _event.send(ImportDetailsState.Event.ShowError)

                    }
                    is ApiResponse.Loading -> {

                         }
                }
            }
        }
    }



    private fun addImportDetails() {
        val newImportDetails = _uiState.value.importDetailsSelected
        _uiState.update {
            it.copy(
                importDetails = it.importDetails + newImportDetails
            )
        }
    }

    private fun updateImportDetails() {
        val updatedImportDetails = _uiState.value.importDetailsSelected
        _uiState.update {
            it.copy(
                importDetails = it.importDetails.map { importDetails ->
                    if (importDetails.localId == _uiState.value.importDetailsSelected.localId) {
                        updatedImportDetails
                    } else {
                        importDetails
                    }
                })
        }

    }

    private fun deleteImportDetails(id: String) {
        _uiState.update {
            it.copy(
                importDetails = it.importDetails.filter { importDetails ->
                    importDetails.localId != id
                }
            )
        }
    }

    private fun createImport() {
        viewModelScope.launch {
            createImportUseCase.invoke(_uiState.value.import, _uiState.value.importDetails).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _event.send(ImportDetailsState.Event.BackToAfterModify)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(ImportDetailsState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun updateImport() {
        viewModelScope.launch {
            updateImportUseCase(_uiState.value.import, _uiState.value.importDetails).collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        _event.send(ImportDetailsState.Event.BackToAfterModify)
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = response.errorMessage
                            )
                        }
                        _event.send(ImportDetailsState.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }


    }
    fun onAction(action: ImportDetailsState.Action) {
        when (action) {
            is ImportDetailsState.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(ImportDetailsState.Event.OnBack)
                }
            }
            is ImportDetailsState.Action.OnImportDetailsSelected -> {
                _uiState.update {
                    it.copy(
                        importDetailsSelected = action.importDetails
                    )}}
            is ImportDetailsState.Action.OnChangeSupplierId -> {
                _uiState.update {
                    it.copy(
                        import = it.import.copy(supplierId = action.supplierId)
                    )}}

            is ImportDetailsState.Action.OnChangeIngredient -> {
                _uiState.update {
                    it.copy(
                        importDetailsSelected = it.importDetailsSelected.copy(ingredient = action.ingredient)
                    )}}
            is ImportDetailsState.Action.OnChangeExpiryDate -> {
                _uiState.update {
                    it.copy(
                        importDetailsSelected = it.importDetailsSelected.copy(expiryDate = action.expiryDate)
                    )}}
            is ImportDetailsState.Action.OnChangeProductionDate -> {
                _uiState.update {
                    it.copy(
                        importDetailsSelected = it.importDetailsSelected.copy(productionDate = action.productionDate)
                    )}}
            is ImportDetailsState.Action.OnChangeQuantity -> {
                _uiState.update {
                    it.copy(
                        importDetailsSelected = it.importDetailsSelected.copy(quantity = if (action.quantity > BigDecimal.ZERO) action.quantity else BigDecimal.ONE)
                    )}}
            is ImportDetailsState.Action.OnChangeCost -> {
                _uiState.update {
                    it.copy(
                        importDetailsSelected = it.importDetailsSelected.copy(cost = action.cost ?: BigDecimal.ZERO)
                    )}}
            is ImportDetailsState.Action.AddImportDetails -> {
                addImportDetails()
            }
            is ImportDetailsState.Action.UpdateImportDetails -> {
                updateImportDetails()
            }
            is ImportDetailsState.Action.DeleteImportDetails -> {
                deleteImportDetails(id = action.importDetailsId)
            }
            is ImportDetailsState.Action.CreateImport -> {
                createImport()}
            is ImportDetailsState.Action.UpdateImport -> {
                updateImport()}

            is ImportDetailsState.Action.NotifyCantEdit -> {
                viewModelScope.launch {
                    _event.send(ImportDetailsState.Event.NotifyCantEdit)
                }
            }
        }
    }
}

    data class ImportDetailUIModel(
        val localId: String = UUID.randomUUID().toString(),
        val id: Long? = null,
        val ingredient: Ingredient? = null,
        val expiryDate: LocalDate = LocalDate.now(),
        val productionDate: LocalDate = LocalDate.now(),
        val quantity: BigDecimal = BigDecimal(1),
        val cost: BigDecimal = BigDecimal(0),
    )

    object ImportDetailsState {
        data class UiState(
            val isLoading: Boolean = false,
            val error: String? = null,
            val import: Import,
            val importDetails: List<ImportDetailUIModel> = emptyList(),
            val isUpdating: Boolean = false,
            val ingredients: List<Ingredient> = emptyList(),
            val importDetailsSelected: ImportDetailUIModel = ImportDetailUIModel(),
            val isEditable: Boolean = true,
        )

        sealed interface Event {
            data object OnBack : Event
            data object ShowError : Event
            data object BackToAfterModify : Event
            data object NotifyCantEdit : Event

        }

        sealed interface Action {
            data object OnBack : Action
            data class OnImportDetailsSelected(val importDetails: ImportDetailUIModel) : Action
            data class OnChangeSupplierId(val supplierId: Long) : Action

            data class OnChangeIngredient(val ingredient: Ingredient) : Action
            data class OnChangeExpiryDate(val expiryDate: LocalDate) : Action
            data class OnChangeProductionDate(val productionDate: LocalDate) : Action
            data class OnChangeQuantity(val quantity: BigDecimal) : Action
            data class OnChangeCost(val cost: BigDecimal?) : Action
            data object AddImportDetails : Action
            data object UpdateImportDetails : Action
            data class DeleteImportDetails(val importDetailsId: String) : Action
            data object CreateImport : Action
            data object UpdateImport : Action
            data object NotifyCantEdit : Action
        }
    }