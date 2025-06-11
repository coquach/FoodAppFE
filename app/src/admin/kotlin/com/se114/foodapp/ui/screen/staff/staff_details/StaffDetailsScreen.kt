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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MonetizationOn
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.model.SalaryHistory
import com.example.foodapp.data.model.enums.Gender
import com.example.foodapp.ui.screen.components.ChipsGroupWrap
import com.example.foodapp.ui.screen.components.ComboBoxSample
import com.example.foodapp.ui.screen.components.DatePickerSample
import com.example.foodapp.ui.screen.components.DateRangePickerSample
import com.example.foodapp.ui.screen.components.DetailsTextRow
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.ImagePickerBottomSheet
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.RadioGroupWrap
import com.example.foodapp.utils.StringUtils


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDetailsScreen(
    navController: NavController,
    viewModel: StaffDetailsViewModel = hiltViewModel(),
) {


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }
    var showSheetImage by rememberSaveable { mutableStateOf(false) }
    var showHistorySalary by rememberSaveable { mutableStateOf(false) }


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
            },
            icon = if (uiState.isUpdating) Icons.AutoMirrored.Filled.MenuBook else null,
            iconClick = {
                showHistorySalary = true
            }
        )
        Column(
            modifier = Modifier
                .fillMaxWidth().weight(1f)
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)


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
                selectedOption = Gender.fromName(uiState.staff.gender).display,
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

            ChipsGroupWrap(
                modifier = Modifier.fillMaxWidth(),
                text = "Chức vụ",
                options = listOf("Bán hàng", "Giao Hàng"),
                selectedOption = uiState.staff.position,
                onOptionSelected = {
                        viewModel.onAction(StaffDetails.Action.OnChangePosition(it))
                },
                containerColor = MaterialTheme.colorScheme.inversePrimary,
                isFlowLayout = true,
                shouldSelectDefaultOption = true
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
                },
                modifier = Modifier.fillMaxWidth()
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
                value = uiState.staff.basicSalary.toPlainString(),
                onValueChange = {
                    viewModel.onAction(StaffDetails.Action.OnChangeBasicSalary(it.toBigDecimalOrNull()))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                labelText = "Lương cơ bản"
            )

        }
        LoadingButton(
            onClick = {
                if (uiState.isUpdating) {

                    viewModel.onAction(StaffDetails.Action.UpdateStaff)

                } else {
                    viewModel.onAction(StaffDetails.Action.AddStaff)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            text = if (uiState.isUpdating) "Cập nhật" else "Tạo",
            loading = uiState.isLoading,

        )
        
    }
    if (showSheetImage) {
        ImagePickerBottomSheet(
            onDismiss = { showSheetImage = false },
            onImageSelected = { uri -> viewModel.onAction(StaffDetails.Action.OnChangeAvatar(uri)) })
    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.errorMessage ?: "Lỗi không xác định",
            onDismiss = {
                showErrorSheet = false
            },
        )
    }
    if (showHistorySalary) {
        Dialog(
            onDismissRequest = {
                showHistorySalary = false
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(30.dp)

            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {


                    Text(
                        text = "Thông tin",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(
                            items = uiState.staff.salaryHistories,
                            key = { it.currentSalary }
                        ) {
                            HistorySalarySection(
                                data = it,
                            )
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun HistorySalarySection(
    data: SalaryHistory,
    modifier: Modifier = Modifier,
) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            DetailsTextRow(
                text = "${data.month}/${data.year}",
                icon = Icons.Default.DateRange
            )
            DetailsTextRow(
                text = StringUtils.formatCurrency(data.currentSalary),
                icon = Icons.Default.MonetizationOn,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }



}