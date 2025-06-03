package com.se114.foodapp.ui.screen.menu.food_details

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.model.Food
import com.example.foodapp.ui.screen.components.ChipsGroupWrap
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.ImageListPicker
import com.example.foodapp.ui.screen.components.ImagePickerBottomSheet
import kotlinx.coroutines.delay
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
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("shouldRefresh", true)
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderDefaultView(
            onBack = {
                viewModel.onAction(AddFood.Action.OnBack)
            },
            text = "Thông tin món ăn"
        )
        Spacer(modifier = Modifier.size(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
          ImageListPicker(
              modifier = Modifier.align(Alignment.CenterHorizontally),
              imageList = uiState.foodAddUi.images?: emptyList(),
              onUpdateImages = {
                  viewModel.onAction(AddFood.Action.OnImagesChange(it))
              }
          )
            FoodAppTextField(
                value = uiState.foodAddUi.name,
                onValueChange = {
                    viewModel.onAction(AddFood.Action.OnNameChange(it))
                },
                modifier = Modifier.fillMaxWidth(),
                labelText = "Tên"
            )
//            ChipsGroupWrap(
//                text = "Danh mục",
//                options = menusAvailable.value.map { it.name },
//                selectedOption = menuName.value,
//                onOptionSelected = { selectedName ->
//                    val selectedMenu = menusAvailable.value.find { it.name == selectedName }
//                    selectedMenu?.let {
//                        viewModel.onMenuIdChange(it.id)
//                        viewModel.onMenuNameChange(it.name)
//                    }
//                },
//            )

            FoodAppTextField(
                value = uiState.foodAddUi.description,
                onValueChange = {
                    viewModel.onAction(AddFood.Action.OnDescriptionChange(it))
                },
                modifier = Modifier.fillMaxWidth(), labelText = "Mô tả"
            )
            FoodAppTextField(
                value = uiState.foodAddUi.price.toPlainString(),
                onValueChange = {
                    viewModel.onAction(AddFood.Action.OnPriceChange(it.toBigDecimalOrNull()))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                labelText = "Giá"
            )
            FoodAppTextField(
                value = uiState.foodAddUi.defaultQuantity.toString(),
                onValueChange = {
                    viewModel.onAction(AddFood.Action.OnDefaultQuantityChange(it.toIntOrNull()))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                labelText = "Số lượng"
            )

            Button(
                onClick = {
                    if (uiState.isUpdating) {

                        viewModel.onAction(AddFood.Action.UpdateFood)

                    } else {
                        viewModel.onAction(AddFood.Action.AddFood)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (uiState.isUpdating) "Cập nhật" else "Tạo",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }


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