package com.se114.foodapp.ui.screen.staff.staff_details

import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.foodapp.data.model.enums.Gender



import com.example.foodapp.ui.screen.components.ComboBoxSample
import com.example.foodapp.ui.screen.components.DatePickerSample
import com.example.foodapp.ui.screen.components.DateRangePickerSample
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.ImagePickerBottomSheet
import com.example.foodapp.ui.screen.components.RadioGroupWrap
import com.example.foodapp.data.model.Staff
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDetailsScreen(
    navController: NavController,
    viewModel: StaffDetailsViewModel = hiltViewModel(),
) {


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }
    var showSheetImage by rememberSaveable { mutableStateOf(false) }


    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                is StaffDetails.Event.BackToListAfterModify -> {
                    if (uiState.isUpdating) {
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

                    navController.popBackStack()

                }

                StaffDetails.Event.GoBack -> {
                    navController.popBackStack()
                }
                StaffDetails.Event.ShowErrorMessage -> {
                    showErrorSheet = true
                }
            }
        }
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
                viewModel.onAction(StaffDetails.Action.GoBack)
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
                    model = uiState.staff.avatar?.url,
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
                        .clickable { showSheetImage = true },
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
                value = uiState.staff.fullName,
                onValueChange = {
                    viewModel.onAction(StaffDetails.Action.OnChangeFullName(it))
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
                selectedOption = uiState.staff.gender,
                onOptionSelected = {
                    Log.d("GenderSelect", "Selected: $it")
                    viewModel.onAction(StaffDetails.Action.OnChangeGender(it))
                }
            )
            FoodAppTextField(
                value = uiState.staff.phone,
                onValueChange = {
                    viewModel.onAction(StaffDetails.Action.OnChangePhone(it))
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
                selected = uiState.staff.position,
                onPositionSelected = {
                    if (it != null) {
                        viewModel.onAction(StaffDetails.Action.OnChangePosition(it))
                    }
                },
                textPlaceholder = "Chọn chức vụ...",
                options = listOf("Bán hàng", "Giao Hàng", "Quản lí kho"),
                modifier = Modifier.fillMaxWidth()
            )
            DatePickerSample(
                text = "Ngày sinh",
                selectedDate = uiState.staff.birthDate,
                onDateSelected = { viewModel.onAction(StaffDetails.Action.OnChangeBirthDate(it)) }
            )

            DateRangePickerSample(
                startDate = uiState.staff.startDate,
                endDate = uiState.staff.endDate,
                onDateRangeSelected = { start, end ->
                    viewModel.onAction(StaffDetails.Action.OnChangeStartDate(start))
                    viewModel.onAction(StaffDetails.Action.OnChangeEndDate(end))
                }
            )
            FoodAppTextField(
                value = uiState.staff.address,
                onValueChange = {
                    viewModel.onAction(StaffDetails.Action.OnChangeAddress(it))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                labelText = "Địa chỉ"
            )
            FoodAppTextField(
                value = uiState.staff.basicSalary.toString(),
                onValueChange = {
                    viewModel.onAction(StaffDetails.Action.OnChangeBasicSalary(it.toDoubleOrNull()))
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
                    if (uiState.isUpdating) {

                            viewModel.onAction(StaffDetails.Action.UpdateStaff)

                    } else {
                        viewModel.onAction(StaffDetails.Action.AddStaff)
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
    if (showSheetImage) {
        ImagePickerBottomSheet(
            onDismiss = { showSheetImage = false },
            onImageSelected = { uri -> viewModel.onAction(StaffDetails.Action.OnChangeAvatar(uri)) })
    }
    if (showErrorSheet){
        ErrorModalBottomSheet(
            description = uiState.errorMessage ?: "Lỗi không xác định",
            onDismiss = {
                showErrorSheet = false
            },
        )
    }
}