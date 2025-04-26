package com.se114.foodapp.ui.screen.employee.add_employee

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.model.enums.Gender



import com.example.foodapp.ui.screen.components.ComboBoxSample
import com.example.foodapp.ui.screen.components.DatePickerSample
import com.example.foodapp.ui.screen.components.DateRangePickerSample
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.ImagePickerBottomSheet
import com.example.foodapp.ui.screen.components.RadioGroupWrap
import com.example.foodapp.data.model.Staff
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


@Composable
fun AddEmployeeScreen(
    navController: NavController,
    viewModel: AddEmployeeViewModel = hiltViewModel(),
    isUpdating: Boolean = false,
    staff: Staff? = null
) {

    val imageUri = viewModel.imageUrl.collectAsStateWithLifecycle()
    val showSheetImage = remember { mutableStateOf(false) }

    val fullName by viewModel.fullName.collectAsStateWithLifecycle()
    val phone by viewModel.phone.collectAsStateWithLifecycle()
    val position by viewModel.position.collectAsStateWithLifecycle()
    val gender by viewModel.gender.collectAsStateWithLifecycle()
    val birthDate by viewModel.birthDate.collectAsStateWithLifecycle()
    val basicSalary by viewModel.basicSalary.collectAsStateWithLifecycle()
    val address by viewModel.address.collectAsStateWithLifecycle()
    val startDate by viewModel.startDate.collectAsStateWithLifecycle()
    val endDate by viewModel.endDate.collectAsStateWithLifecycle()

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = isUpdating) {
        delay(100)
        viewModel.setMode(isUpdating, staff)
    }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is AddEmployeeViewModel.AddEmployeeEvents.GoBack -> {
                    if (isUpdating) {
                        Toast.makeText(
                            navController.context,
                            "Cập nhật nhân viên thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.previousBackStackEntry?.savedStateHandle?.set("updated", true)
                    } else {
                        Toast.makeText(
                            navController.context,
                            "Thêm nhân viên thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.previousBackStackEntry?.savedStateHandle?.set("added", true)
                    }

                    navController.navigateUp()

                }


                is AddEmployeeViewModel.AddEmployeeEvents.ShowErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_LONG).show()
                }

            }
        }
    }
    when (uiState.value) {
        is AddEmployeeViewModel.AddEmployeeState.Error -> {}
        is AddEmployeeViewModel.AddEmployeeState.Loading -> {}
        is AddEmployeeViewModel.AddEmployeeState.Nothing -> {}
        is AddEmployeeViewModel.AddEmployeeState.Success -> {}
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderDefaultView(
            text = "Thông tin nhân viên",
            onBack = {
                navController.popBackStack()
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally,


            ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.BottomEnd
            ) {

                AsyncImage(
                    model = imageUri.value ?: R.drawable.avatar_placeholder,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(8.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.avatar_placeholder),
                    error = painterResource(id = R.drawable.avatar_placeholder)
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .shadow(8.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
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
                value = fullName,
                onValueChange = {
                    viewModel.onFullNameChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                labelText = "Họ và tên"
            )
            RadioGroupWrap(
                text = "Giới tính",
                options = Gender.entries.map { it.display },
                selectedOption = gender ?: "",
                onOptionSelected = {
                    Log.d("GenderSelect", "Selected: $it")
                    viewModel.onGenderChange(it)
                }
            )
            FoodAppTextField(
                value = phone,
                onValueChange = {
                    viewModel.onPhoneChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                labelText = "Số điện thoại"
            )
            ComboBoxSample(
                title = "Chức vụ",
                selected = position,
                onPositionSelected = {
                    if (it != null) {
                        viewModel.onPositionChange(it)
                    }
                },
                textPlaceholder = "Chọn chức vụ...",
                options = listOf("Bán hàng", "Giao Hàng", "Quản lí kho")
            )
            DatePickerSample(
                text = "Ngày sinh",
                selectedDate = birthDate,
                onDateSelected = { viewModel.onBirthDateChange(it) }
            )

            DateRangePickerSample(
                startDate = startDate,
                endDate = endDate,
                onDateRangeSelected = { start, end ->
                    viewModel.onStartDateChange(start)
                    viewModel.onEndDateChange(end)
                }
            )
            FoodAppTextField(
                value = address,
                onValueChange = {
                    viewModel.onAddressChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                labelText = "Địa chỉ"
            )
            FoodAppTextField(
                value = basicSalary.toString(),
                onValueChange = {
                    viewModel.onBasicSalaryChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                labelText = "Lương cơ bản"
            )
            Button(
                onClick = {
                    if (isUpdating && staff != null) {

                            viewModel.updateStaff(staff.id)

                    } else {
                        viewModel.addStaff()
                    }
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
    }
    if (showSheetImage.value) {
        ImagePickerBottomSheet(
            showSheet = showSheetImage.value,
            onDismiss = { showSheetImage.value = false },
            onImageSelected = { uri -> viewModel.onImageUrlChange(uri) })
    }
}