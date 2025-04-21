package com.se114.foodapp.ui.screen.menu.add_menu_item

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.ImagePickerBottomSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddMenuItemScreen(
    navController: NavController,
    viewModel: AddMenuItemViewModel = hiltViewModel(),
    isUpdating: Boolean = false,
    menuItem: MenuItem? = null
) {

    val name = viewModel.name.collectAsStateWithLifecycle()
    val description = viewModel.description.collectAsStateWithLifecycle()
    val price = viewModel.price.collectAsStateWithLifecycle()
    val uiState = viewModel.addMenuItemState.collectAsStateWithLifecycle()
    val isLoading = remember { mutableStateOf(false) }

    val showSheetImage = remember { mutableStateOf(false) }
    val selectedImage = viewModel.imageUrl.collectAsStateWithLifecycle()

//    val imageUri =
//        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Uri?>("imageUri", null)
//            ?.collectAsStateWithLifecycle()


    LaunchedEffect(key1 = isUpdating) {
        delay(100)
        viewModel.setMode(isUpdating, menuItem)
    }


    LaunchedEffect(key1 = true) {
        viewModel.addMenuItemEvent.collectLatest {
            when (it) {
                is AddMenuItemViewModel.AddMenuItemEvent.GoBack -> {
                    Toast.makeText(
                        navController.context, "Đã thêm món thành công", Toast.LENGTH_SHORT
                    ).show()
                    navController.previousBackStackEntry?.savedStateHandle?.set("added", true)
                    navController.popBackStack()

                }

                is AddMenuItemViewModel.AddMenuItemEvent.ShowErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    when (uiState.value) {
        is AddMenuItemViewModel.AddMenuItemState.Error -> {}
        is AddMenuItemViewModel.AddMenuItemState.Loading -> {
            isLoading.value = true
        }

        is AddMenuItemViewModel.AddMenuItemState.Nothing -> {}
        is AddMenuItemViewModel.AddMenuItemState.Success -> {

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
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                model = selectedImage.value,
                contentDescription = "Food Image",
                modifier = Modifier
                    .size(200.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .border(4.dp, Color.White, RoundedCornerShape(8.dp))
                    .background(LightGray),

                contentScale = ContentScale.Crop
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
        FoodAppTextField(
            value = description.value, onValueChange = {
                viewModel.onDescriptionChange(it)
            },
            modifier = Modifier.fillMaxWidth(), labelText = "Mô tả"
        )
        FoodAppTextField(
            value = price.value,
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

            },
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
    if (showSheetImage.value) {
        ImagePickerBottomSheet(
            showSheet = showSheetImage.value,
            onDismiss = { showSheetImage.value = false },
            onImageSelected = { uri -> viewModel.onImageUrlChange(uri) })
    }
}