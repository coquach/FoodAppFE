package com.se114.foodapp.ui.screen.warehouse.imports

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.filter.SupplierFilter
import com.example.foodapp.data.dto.request.ImportDetailRequest
import com.example.foodapp.data.dto.request.ImportRequest
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.ImportDetail
import com.example.foodapp.data.model.Ingredient
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.model.Supplier
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.data.repository.StaffRepository
import com.se114.foodapp.data.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ImportDetailsViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val supplierRepository: SupplierRepository,
    private val staffRepository: StaffRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<ImportDetailsState>(ImportDetailsState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<ImportDetailsEvents>()
    val event = _event.receiveAsFlow()

    private val _suppliers = MutableStateFlow<PagingData<Supplier>>(PagingData.empty())
    val suppliers = _suppliers

    private val _staffs = MutableStateFlow<PagingData<Staff>>(PagingData.empty())
    val staffs = _staffs

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients = _ingredients.asStateFlow()

    private val _importRequest = MutableStateFlow(
        ImportRequest(
            supplierId = null,
            staffId = null,
            importDate = "",
            importDetails = emptyList()
        )
    )
    val importRequest = _importRequest.asStateFlow()

    fun onChangeSupplerId(supplierId: Long) {
        _importRequest.update { it.copy(supplierId = supplierId) }
    }
    fun onChangStaffId(staffId: Long) {
        _importRequest.update { it.copy(staffId = staffId) }
    }


    private val _importDetailsListRequest = MutableStateFlow<List<ImportDetailUIModel>>(emptyList())
    val importDetailsListRequest = _importDetailsListRequest.asStateFlow()

    private val _importDetailsRequest = MutableStateFlow(
        ImportDetailUIModel()
    )

    fun onChangeIngredient(ingredient: Ingredient) {
        _importDetailsRequest.update { it.copy(ingredient = ingredient) }
    }
    fun onChangeExpiryDate(expiryDate: LocalDateTime) {
        _importDetailsRequest.update { it.copy(expiryDate = expiryDate) }
    }
    fun onChangeProductionDate(productionDate: LocalDateTime) {
        _importDetailsRequest.update { it.copy(productionDate = productionDate) }
    }
    fun onChangeQuantity(quantity: BigDecimal) {
        _importDetailsRequest.update { it.copy(quantity = quantity) }
    }
    fun onChangeCost(cost: BigDecimal) {
        _importDetailsRequest.update { it.copy(cost = cost) }
    }
    val importDetailsRequest = _importDetailsRequest.asStateFlow()

    fun loadImportDetails(importDetail: ImportDetailUIModel? = null) {
        if (importDetail != null) {
            _importDetailsRequest.update {
                it.copy(
                    localId = importDetail.localId,
                    id = importDetail.id,
                    ingredient = importDetail.ingredient,
                    expiryDate = importDetail.expiryDate,
                    productionDate = importDetail.productionDate,
                    quantity = importDetail.quantity,
                    cost = importDetail.cost
                )
            }
        } else {
            _importDetailsRequest.update {
                it.copy(
                    localId = UUID.randomUUID().toString(),
                    id = null,
                    ingredient = null,
                    expiryDate = LocalDateTime.now(),
                    productionDate = LocalDateTime.now(),
                    quantity = BigDecimal(1),
                    cost = BigDecimal(0)
                )
            }
        }
    }

    private var isUpdating by mutableStateOf(false)

    init {
            viewModelScope.launch {
                coroutineScope {
                    val ingredientsDeferred = async { getIngredients() }
                    val suppliersDeferred = async { getSuppliers() }
                    val staffsDeferred = async { getStaffs() }

                    // Chờ các API hoàn thành
                    ingredientsDeferred.await()
                    suppliersDeferred.await()
                    staffsDeferred.await()
                }
            }

    }

    fun setMode(mode: Boolean, import: Import?) {
        isUpdating = mode

        if (isUpdating && import != null) {
            loadInitialData(import)
        }
    }

    private fun loadInitialData(import: Import) {
        _importRequest.update {
            it.copy(
                supplierId = import.id,
                staffId = import.staffId,
                importDate = StringUtils.formatDateTime(import.importDate),
            )
        }
        _importDetailsListRequest.update {
            import.importDetails.map { detail ->
                ImportDetailUIModel(
                    localId = UUID.randomUUID().toString(),
                    id = detail.id,
                    ingredient = detail.ingredient,
                    expiryDate = detail.expiryDate,
                    productionDate = detail.productionDate,
                    quantity = detail.quantity,
                    cost = detail.cost
                )
            }
        }
    }
    private fun getSuppliers(){
        viewModelScope.launch {
            supplierRepository.getAllSuppliers(SupplierFilter(isActive = true)).cachedIn(viewModelScope).collect{
                _suppliers.value = it
            }
        }
    }
    private fun getStaffs(){
        viewModelScope.launch {
            staffRepository.getAllStaffs().cachedIn(viewModelScope).collect{
                _staffs.value = it
            }
        }
    }

    private fun getIngredients() {
        viewModelScope.launch {
            _uiState.value = ImportDetailsState.Loading
            try {
                val response = safeApiCall { foodApi.getActiveIngredients() }
                when (response) {
                    is ApiResponse.Success -> {
                        _ingredients.value = response.body ?: emptyList()
                        _uiState.value = ImportDetailsState.Success
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = ImportDetailsState.Error("Tải nguyên liệu thất bại")
                        Log.d("Fetch ingredient:", response.message)
                    }

                    else -> {
                        _uiState.value = ImportDetailsState.Error("Tải nguyên liệu thất bại")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ImportDetailsState.Error("Tải nguyên liệu thất bại")
                Log.d("Fetch ingredient:", e.message.toString())
            }
        }
    }

    fun addImportDetails() {
        val newImportDetails = _importDetailsRequest.value
        _importDetailsListRequest.update { oldList ->
            oldList + newImportDetails
        }
        loadImportDetails()
    }
    fun updateImportDetails(importDetailsId: String){
        val updatedImportDetails = _importDetailsRequest.value
        _importDetailsListRequest.update { oldList ->
            oldList.map { importDetails ->
                if (importDetails.localId == importDetailsId) updatedImportDetails else importDetails
            } }
        loadImportDetails()
    }
    fun deleteImportDetails(importDetailsId: String){
        _importDetailsListRequest.update { oldList ->
            oldList.filterNot { importDetails ->
                importDetails.localId == importDetailsId
            }
        }
    }
    fun addImport(){
        viewModelScope.launch {
            _uiState.value = ImportDetailsState.Loading
            _importRequest.update {
                it.copy(
                    importDate = StringUtils.getFormattedCurrentVietnamDateTime(),
                    importDetails = _importDetailsListRequest.value.map { importDetails -> ImportDetailRequest(
                        id = importDetails.id,
                        ingredientId = importDetails.ingredient!!.id,
                        expiryDate = StringUtils.formatDateTime(importDetails.expiryDate),
                        productionDate = StringUtils.formatDateTime(importDetails.productionDate),
                        quantity = importDetails.quantity,
                        cost = importDetails.cost
                    ) }
                )
            }
            val request = _importRequest.value
            delay(3000)

            try {
                val response = safeApiCall { foodApi.createImport(request) }
                when (response) {
                    is ApiResponse.Success -> {

                        _uiState.value = ImportDetailsState.Success
                        _event.send(ImportDetailsEvents.GoBack)
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = ImportDetailsState.Error("Tạo phiếu nhập thất bại")
                        Log.d("Add import", response.message)
                    }

                    else -> {
                        _uiState.value = ImportDetailsState.Error("Tạo phiếu nhập thất bại")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ImportDetailsState.Error("Tạo phiếu nhập thất bại")
                Log.d("Add import:", e.message.toString())
            }
        }
    }
    fun updateImport(importId: Long){
        viewModelScope.launch {
            _uiState.value = ImportDetailsState.Loading
            _importRequest.update {
                it.copy(
                    importDate = StringUtils.getFormattedCurrentVietnamDateTime(),
                    importDetails = _importDetailsListRequest.value.map { importDetails -> ImportDetailRequest(
                        id = importDetails.id,
                        ingredientId = importDetails.ingredient!!.id,
                        expiryDate = StringUtils.formatDateTime(importDetails.expiryDate),
                        productionDate = StringUtils.formatDateTime(importDetails.productionDate),
                        quantity = importDetails.quantity,
                        cost = importDetails.cost
                    ) }
                )
            }
            val request = _importRequest.value
            delay(3000)

            try {
                val response = safeApiCall { foodApi.updateImport(importId,request) }
                when (response) {
                    is ApiResponse.Success -> {

                        _uiState.value = ImportDetailsState.Success
                        _event.send(ImportDetailsEvents.GoBack)
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = ImportDetailsState.Error("Cập nhật phiếu nhập thất bại")
                        Log.d("Update import", response.message)
                    }

                    else -> {
                        _uiState.value = ImportDetailsState.Error("Cập nhật phiếu nhập thất bại")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = ImportDetailsState.Error("Cập nhật phiếu nhập thất bại")
                Log.d("Update import:", e.message.toString())
            }
        }
    }

    sealed class ImportDetailsState {
        data object Nothing : ImportDetailsState()
        data object Loading : ImportDetailsState()
        data object Success : ImportDetailsState()
        data class Error(val message: String) : ImportDetailsState()
    }

    sealed class ImportDetailsEvents {
        data object GoBack : ImportDetailsEvents()
    }
}

data class ImportDetailUIModel(
    val localId: String = UUID.randomUUID().toString(),
    val id: Long? = null,
    val ingredient: Ingredient?= null,
    val expiryDate: LocalDateTime = LocalDateTime.now(),
    val productionDate: LocalDateTime = LocalDateTime.now(),
    val quantity: BigDecimal = BigDecimal(1),
    val cost: BigDecimal= BigDecimal(0)
)