package com.se114.foodapp.ui.screen.warehouse.material

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.IngredientRequest
import com.example.foodapp.data.dto.request.MenuRequest
import com.example.foodapp.data.dto.request.UnitRequest
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Ingredient
import com.example.foodapp.data.model.Menu
import com.example.foodapp.data.model.Unit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialViewModel @Inject constructor(
    private val foodApi: FoodApi
) : ViewModel() {
    private val _uiState = MutableStateFlow<MaterialState>(MaterialState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<MaterialEvents>()
    val event = _event.receiveAsFlow()

    private val _activeUnits = MutableStateFlow<List<Unit>>(emptyList())
    val activeUnits = _activeUnits.asStateFlow()
    private val _hiddenUnits = MutableStateFlow<List<Unit>>(emptyList())
    val hiddenUnits = _hiddenUnits.asStateFlow()

    private val _activeIngredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val activeIngredients = _activeIngredients.asStateFlow()

    private val _hiddenIngredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val hiddenIngredients = _hiddenIngredients.asStateFlow()

    private val _unitName = MutableStateFlow("")
    val unitName = _unitName.asStateFlow()

    fun onChangeUnitName(unitName: String) {
        _unitName.value = unitName
    }

    private val _ingredientRequest = MutableStateFlow(
        IngredientRequest(
            name = "",
            unitId = null,
        )
    )
    val ingredientRequest = _ingredientRequest.asStateFlow()
    fun onChangeIngredientName(ingredientName: String) {
        _ingredientRequest.update { it.copy(name = ingredientName) }
    }
    fun onChangeIngredientId(ingredientId: Long) {
        _ingredientRequest.update { it.copy(unitId = ingredientId) }
    }

    init {
        getUnits()
        getIngredients()
    }

    fun loadIngredient(ingredient: Ingredient?=null) {
        if(ingredient!= null) {
            _ingredientRequest.update {
                it.copy(
                    name = ingredient.name,
                    unitId = ingredient.unitId
                )
            }
        }else {
            _ingredientRequest.update {
                it.copy(
                    name = "",
                    unitId = null
                )
            }
        }
    }



    private fun getUnits() {
        viewModelScope.launch {
            _uiState.value = MaterialState.Loading
            try {
                coroutineScope {
                    val activeUnitsDeferred = async { safeApiCall { foodApi.getActiveUnits() } }
                    val hiddenUnitsDeferred = async { safeApiCall { foodApi.getHiddenUnits() } }

                    val activeUnitsResponse = activeUnitsDeferred.await()
                    val hiddenUnitsResponse = hiddenUnitsDeferred.await()

                    if (activeUnitsResponse is ApiResponse.Success) {
                        _activeUnits.value = activeUnitsResponse.body ?: emptyList()
                        _uiState.value = MaterialState.Success
                    } else {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Tải đơn vị tính đang hiển thị thất bại"))
                    }

                    if (hiddenUnitsResponse is ApiResponse.Success) {
                        _hiddenUnits.value = hiddenUnitsResponse.body ?: emptyList()
                        _uiState.value = MaterialState.Success
                    } else {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Tải đơn vị tính ẩn thất bại"))
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = MaterialState.Error
                _event.send(MaterialEvents.ShowErrorMessage("Lỗi không xác định: ${e.message}"))
            }
        }
    }

    private fun getIngredients() {
        viewModelScope.launch {
            _uiState.value = MaterialState.Loading
            try {
                coroutineScope {
                    val activeIngredientsDeferred = async { safeApiCall { foodApi.getActiveIngredients() } }
                    val hiddenIngredientsDeferred = async { safeApiCall { foodApi.getHiddenIngredients() } }

                    val activeIngredientsResponse = activeIngredientsDeferred.await()
                    val hiddenIngredientsResponse = hiddenIngredientsDeferred.await()

                    if (activeIngredientsResponse is ApiResponse.Success) {
                        _activeIngredients.value = activeIngredientsResponse.body ?: emptyList()
                        _uiState.value = MaterialState.Success
                    } else {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Tải nguyên liệu đang hiển thị thất bại"))
                    }

                    if (hiddenIngredientsResponse is ApiResponse.Success) {
                        _hiddenIngredients.value = hiddenIngredientsResponse.body ?: emptyList()
                        _uiState.value = MaterialState.Success
                    } else {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Tải nguyên liệu tính ẩn thất bại"))
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = MaterialState.Error
                _event.send(MaterialEvents.ShowErrorMessage("Lỗi không xác định: ${e.message}"))
            }
        }
    }

    fun addUnit() {
        viewModelScope.launch {
            _uiState.value = MaterialState.Loading
            try {
                val request = UnitRequest(
                    name = _unitName.value
                )

                val response = safeApiCall { foodApi.createUnit(request) }
                when (response) {
                    is ApiResponse.Success -> {
                        Log.d("Add menu", "Done")
                        val newUnit = response.body
                        _activeUnits.update { oldList ->
                            oldList + newUnit!!
                        }
                        _unitName.value = ""
                        _uiState.value = MaterialState.Success
                        getUnits()
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Thêm đơn vị tính thất bại"))
                        Log.d("Add menu", response.message)
                    }

                    else -> {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Lỗi không xác định khi thêm"))

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _event.send(MaterialEvents.ShowErrorMessage("Lỗi không xác định"))
                Log.d("error add unit", e.message.toString())
            }
        }
    }

    fun addIngredient() {
        viewModelScope.launch {
            _uiState.value = MaterialState.Loading
            try {
                val request = _ingredientRequest.value

                val response = safeApiCall { foodApi.createIngredient(request) }
                when (response) {
                    is ApiResponse.Success -> {
                        Log.d("Add ingredient", "Done")
                        val newIngredient = response.body
                        _activeIngredients.update { oldList ->
                            oldList + newIngredient!!
                        }
                        loadIngredient()
                        _uiState.value = MaterialState.Success
                        getIngredients()
                    }

                    is ApiResponse.Error -> {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Thêm nguyên liệu thất bại"))
                        Log.d("Add ingredient", response.message)
                    }

                    else -> {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Lỗi không xác định khi thêm"))

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _event.send(MaterialEvents.ShowErrorMessage("Lỗi không xác định"))
                Log.d("error add ingredient", e.message.toString())
            }
        }
    }

    fun updateUnit(unitId: Long) {
        viewModelScope.launch {
            _uiState.value = MaterialState.Loading
            try {
                val request = UnitRequest(
                    name = _unitName.value
                )

                val response = safeApiCall { foodApi.updateUnit(unitId, request) }
                when (response) {
                    is ApiResponse.Success -> {
                        val updatedUnit = response.body

                        if(updatedUnit!= null) {
                            _activeUnits.update { oldList ->
                                oldList.map { unit ->
                                    if (unit.id == unitId) updatedUnit else unit
                                }
                            }

                            // Cập nhật trong danh sách hidden
                            _hiddenUnits.update { oldList ->
                                oldList.map { unit ->
                                    if (unit.id == unitId) updatedUnit else unit
                                }
                            }
                            _unitName.value = ""
                            _uiState.value = MaterialState.Success
                            getUnits()
                        }else {
                            _uiState.value = MaterialState.Error
                            _event.send(MaterialEvents.ShowErrorMessage("Cập nhật đơn vị tính thất bại, dữ liệu trống"))
                        }

                    }

                    is ApiResponse.Error -> {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Sửa đơn vị tính thất bại"))
                        Log.d("Update unit", response.message)
                    }

                    else -> {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Lỗi không xác định khi sửa"))

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _event.send(MaterialEvents.ShowErrorMessage("Lỗi không xác định"))
                Log.d("error update unit", e.message.toString())
            }
        }
    }

    fun updateIngredient(ingredientId: Long) {
        viewModelScope.launch {
            _uiState.value = MaterialState.Loading
            try {
                val request = _ingredientRequest.value

                val response = safeApiCall { foodApi.updateIngredient(ingredientId, request) }
                when (response) {
                    is ApiResponse.Success -> {
                        val updatedIngredient = response.body

                        if (updatedIngredient != null) {

                            _activeIngredients.update { oldList ->
                                oldList.map { ingredient ->
                                    if (ingredient.id == ingredientId) updatedIngredient else ingredient
                                }
                            }

                            _hiddenIngredients.update { oldList ->
                                oldList.map { ingredient ->
                                    if (ingredient.id == ingredientId) updatedIngredient else ingredient
                                }
                            }

                            loadIngredient()
                            _uiState.value = MaterialState.Success
                            getIngredients()
                        } else {
                            _uiState.value = MaterialState.Error
                            _event.send(MaterialEvents.ShowErrorMessage("Cập nhật nguyên liệu thất bại, dữ liệu trống"))
                        }

                    }

                    is ApiResponse.Error -> {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Sửa nguyên liệu thất bại"))
                        Log.d("Update ingredient", response.message)
                    }

                    else -> {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Lỗi không xác định khi sửa"))

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _event.send(MaterialEvents.ShowErrorMessage("Lỗi không xác định"))
                Log.d("error update ingredient", e.message.toString())
            }
        }
    }

    fun recoverUnit(unitId : Long, isActive: Boolean){
        viewModelScope.launch {
            _uiState.value = MaterialState.Loading
            try {
                val response = safeApiCall { foodApi.recoverUnit(unitId, isActive) }
                when(response){
                    is ApiResponse.Success -> {
                        _uiState.value = MaterialState.Success
                        getUnits()
                    }
                    is ApiResponse.Error -> {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Sửa đơn vị tính thất bại: "))
                        Log.d("Recover Unit: ", ": ${response.message}")
                    }
                    else -> {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Sửa đơn vị tính thất bại"))
                    }
                }
            }catch (e: Exception) {
                e.printStackTrace()
                _event.send(MaterialEvents.ShowErrorMessage("Lỗi không xác định"))
                Log.d("error setActive unit", e.message.toString())
            }
        }
    }
    fun recoverIngredient(ingredientId: Long, isActive: Boolean) {
        viewModelScope.launch {
            _uiState.value = MaterialState.Loading
            try {
                val response = safeApiCall { foodApi.setActiveIngredient(ingredientId, isActive) }
                when(response){
                    is ApiResponse.Success -> {
                        _uiState.value = MaterialState.Success
                        getIngredients()
                    }
                    is ApiResponse.Error -> {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Sửa nguyên liệu thất bại: "))
                        Log.d("Recover Ingredient: ", ": ${response.message}")
                    }
                    else -> {
                        _uiState.value = MaterialState.Error
                        _event.send(MaterialEvents.ShowErrorMessage("Sửa nguyên liệu thất bại"))
                    }
                }
            }catch (e: Exception) {
                e.printStackTrace()
                _event.send(MaterialEvents.ShowErrorMessage("Lỗi không xác định"))
                Log.d("error setActive Ingredient", e.message.toString())
            }
        }
    }


    sealed class MaterialState {
        object Nothing : MaterialState()
        object Loading : MaterialState()
        object Success : MaterialState()
        object Error : MaterialState()
    }

    sealed class MaterialEvents {
        data class ShowErrorMessage(val message: String) : MaterialEvents()
    }
}