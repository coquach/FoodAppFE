package com.se114.foodapp.ui.screen.menu.food_details

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.ui.screen.components.ChipsGroupWrap
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.ImageListPicker
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.ValidateTextField
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailsAdminScreen(
    navController: NavController,
    viewModel: FoodDetailsAdminViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showErrorSheet by remember { mutableStateOf(false) }

val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collectLatest {
            when (it) {
                is AddFood.Event.OnBackAfterUpdate -> {
                    Log.d("Food goback", "Done")
                    if (uiState.isUpdating) {
                        Toast.makeText(
                            navController.context,
                            "Cập nhật món ăn thành công",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            navController.context,
                            "Tạo món ăn thành công",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    navController.popBackStack()

                }

                is AddFood.Event.ShowError -> {
                   showErrorSheet = true
                }

                AddFood.Event.OnBack -> {
                    navController.popBackStack()
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderDefaultView(
            onBack = {
                viewModel.onAction(AddFood.Action.OnBack)
            },
            text = "Thông tin món ăn"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth().weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ImageListPicker(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                imageList = uiState.foodAddUi.images ?: emptyList(),
                onUpdateImages = {
                    viewModel.onAction(AddFood.Action.OnImagesChange(it))
                }
            )
            ValidateTextField(
                value = uiState.foodAddUi.name,
                onValueChange = {
                    viewModel.onAction(AddFood.Action.OnNameChange(it))
                },
                modifier = Modifier.fillMaxWidth(),
                labelText = "Tên",
                errorMessage = uiState.nameError,
                validate = {
                    viewModel.validate("name")
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
            )
            ChipsGroupWrap(
                text = "Danh mục",
                options = uiState.menus.map { it.name },
                selectedOption = uiState.foodAddUi.menuName,
                onOptionSelected = { selectedName ->
                    val selectedMenu = uiState.menus.find { it.name == selectedName }
                    selectedMenu?.let {
                        viewModel.onAction(AddFood.Action.OnMenuChange(it.id!!, it.name))
                    }
                },
                modifier = Modifier.padding(8.dp),
                containerColor = MaterialTheme.colorScheme.inversePrimary,
                shouldSelectDefaultOption = true
            )

            FoodAppTextField(
                value = uiState.foodAddUi.description,
                onValueChange = {
                    viewModel.onAction(AddFood.Action.OnDescriptionChange(it))
                },
                modifier = Modifier.fillMaxWidth(), labelText = "Mô tả"
            )
            ValidateTextField(
                value = uiState.foodAddUi.price.toPlainString(),
                onValueChange = {
                    viewModel.onAction(AddFood.Action.OnPriceChange(it.toBigDecimalOrNull()))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                labelText = "Giá",
                errorMessage = uiState.priceError,
                validate = {
                    viewModel.validate("price")
                },

            )
            ValidateTextField(
                value = uiState.foodAddUi.defaultQuantity.toString(),
                onValueChange = {
                    viewModel.onAction(AddFood.Action.OnDefaultQuantityChange(it.toIntOrNull()))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                labelText = "Số lượng",
                errorMessage = uiState.defaultQuantityError,
                validate = {
                    viewModel.validate("defaultQuantity")
                },
            )


        }
        LoadingButton(
            onClick = {
                if (uiState.isUpdating) {

                    viewModel.onAction(AddFood.Action.UpdateFood)

                } else {
                    viewModel.onAction(AddFood.Action.AddFood)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            text = if (uiState.isUpdating) "Cập nhật" else "Tạo",
            loading = uiState.isLoading,
            enabled = uiState.isValid


        )


    }
    if(showErrorSheet){
        ErrorModalBottomSheet(
            description = uiState.error.toString(),
            onDismiss = {
                showErrorSheet = false
            },
        )
    }

}