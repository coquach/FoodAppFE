package com.se114.foodapp.ui.screen.menu.add_menu_item

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.enums.Gender
import com.example.foodapp.ui.screen.components.ChipsGroupWrap
import com.example.foodapp.ui.screen.components.ComboBoxSample
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.ImagePickerBottomSheet
import com.example.foodapp.ui.screen.components.RadioGroupWrap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddFoodScreen(
    navController: NavController,
    viewModel: AddFoodViewModel = hiltViewModel(),
    isUpdating: Boolean = false,
    Food: Food? = null
) {

    val name = viewModel.name.collectAsStateWithLifecycle()
    val description = viewModel.description.collectAsStateWithLifecycle()
    val price = viewModel.price.collectAsStateWithLifecycle()

    val menuName = viewModel.menuName.collectAsStateWithLifecycle()
    val menusAvailable = viewModel.menusAvailable.collectAsStateWithLifecycle()

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = remember { mutableStateOf(false) }

    val imageUri = viewModel.imageUrl.collectAsStateWithLifecycle()
    val showSheetImage = remember { mutableStateOf(false) }




    LaunchedEffect(key1 = isUpdating) {
        delay(100)
        viewModel.setMode(isUpdating, Food)
    }


    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                is AddFoodViewModel.AddFoodEvent.GoBack -> {
                    Log.d("Food goback", "Done")
                    if (isUpdating) {
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

                is AddFoodViewModel.AddFoodEvent.ShowErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    when (uiState.value) {
        is AddFoodViewModel.AddFoodState.Error -> {}
        is AddFoodViewModel.AddFoodState.Loading -> {
        }

        is AddFoodViewModel.AddFoodState.Nothing -> {}
        is AddFoodViewModel.AddFoodState.Success -> {

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
                navController.popBackStack()
            },
            text = "Thông tin món ăn"
        )
        Spacer(modifier = Modifier.size(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.BottomEnd
            ) {
                AsyncImage(
                    model = imageUri.value?: R.drawable.ic_placeholder,
                    contentDescription = "Food Image",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .border(4.dp, Color.White, RoundedCornerShape(8.dp)),

                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_placeholder),
                    error = painterResource(id = R.drawable.ic_placeholder)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .shadow(8.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { showSheetImage.value = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Edit Avatar",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            FoodAppTextField(
                value = name.value,
                onValueChange = {
                    viewModel.onNameChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                labelText = "Tên"
            )
            ChipsGroupWrap(
                text = "Danh mục",
                options =  menusAvailable.value.map { it.name },
                selectedOption = menuName.value,
                onOptionSelected = { selectedName ->
                    val selectedMenu = menusAvailable.value.find { it.name == selectedName }
                    selectedMenu?.let {
                        viewModel.onMenuIdChange(it.id)
                        viewModel.onMenuNameChange(it.name)
                    }
                },
            )

            FoodAppTextField(
                value = description.value, onValueChange = {
                    viewModel.onDescriptionChange(it)
                },
                modifier = Modifier.fillMaxWidth(), labelText = "Mô tả"
            )
            FoodAppTextField(
                value = price.value.toPlainString(),
                onValueChange = {
                    viewModel.onPriceChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                labelText = "Giá"
            )

            Button(
                onClick = {
                    if (isUpdating && Food != null) {

                        viewModel.updateFood(Food.id)

                    } else {
                        viewModel.addFood()
                    }},
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isUpdating) "Cập nhật" else "Tạo",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }


    }
    if (showSheetImage.value) {
        ImagePickerBottomSheet(
            showSheet = showSheetImage.value,
            onDismiss = { showSheetImage.value = false },
            onImageSelected = { uri -> viewModel.onImageUrlChange(uri) })
    }
}