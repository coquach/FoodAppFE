package com.example.foodapp.ui.screen.auth.signup.profile

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.model.enums.Gender
import com.example.foodapp.navigation.Auth
import com.example.foodapp.navigation.Login
import com.example.foodapp.ui.screen.components.DatePickerSample
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.ImagePickerBottomSheet
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.RadioGroupWrap
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.ui.theme.confirm
import com.example.foodapp.ui.theme.onConfirm
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp.lifecycleOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import androidx.core.net.toUri


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSheetImage by remember { mutableStateOf(false) }
    var showErrorSheet by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { event -> // Renamed 'it' for clarity
                when (event) {
                    Profile.Event.NavigateAuth -> {
                        navController.navigate(Auth) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }

                    Profile.Event.NavigateLogin -> {
                        navController.navigate(Login) {
                            popUpTo(Auth) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }

                    }

                    Profile.Event.BackToSetting -> {
                        Toast.makeText(
                            context,
                            "Cập nhật thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "shouldRefresh",
                            true
                        )
                        navController.popBackStack()
                    }

                    Profile.Event.ShowDialog -> {
                        showSuccessDialog = true
                    }

                    Profile.Event.ShowError -> {
                        showErrorSheet = true
                    }

                    Profile.Event.BackToPrevious -> {
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

        ) {
        if (uiState.isUpdating)
            HeaderDefaultView(
                text = "Thông tin cá nhân",
                onBack = {
                    viewModel.onAction(Profile.Action.OnBack)
                }
            )
        else HeaderDefaultView(
            text = "Thông tin cá nhân"
        )
        Spacer(modifier = Modifier.size(20.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)


        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.BottomEnd
            ) {

                AsyncImage(
                    model = uiState.profile.avatar,
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
                value = uiState.profile.displayName,
                onValueChange = { viewModel.onAction(Profile.Action.OnDisplayNameChanged(it)) },
                labelText = stringResource(R.string.full_name),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )

            RadioGroupWrap(
                text = "Giới tính",
                options = Gender.entries.map { it.display },
                selectedOption = uiState.profile.gender,
                onOptionSelected = {
                    Log.d("GenderSelect", "Selected: $it")
                    viewModel.onAction(Profile.Action.OnGenderChanged(it))
                }
            )
            DatePickerSample(
                text = "Ngày sinh",
                selectedDate = uiState.profile.dob,
                onDateSelected = { viewModel.onAction(Profile.Action.OnDateOfBirthChanged(it)) }
            )

            FoodAppTextField(
                value = uiState.profile.phoneNumber,
                onValueChange = {
                    viewModel.onAction(Profile.Action.OnPhoneNumberChanged(it))
                },
                labelText = stringResource(R.string.phone_number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )


            LoadingButton(
                onClick = { viewModel.onAction(Profile.Action.OnUpdateProfile) },
                modifier = Modifier.fillMaxWidth(),
                text = if (uiState.isUpdating) "Cập nhật" else "Xác nhận",
                loading = uiState.isLoading,
            )
        }

    }
    if (showSuccessDialog) {

        FoodAppDialog(
            title = "Đăng kí thành công",
            titleColor = MaterialTheme.colorScheme.confirm,
            message = "Bây giờ bạn có thể đăng nhập được rồi nha",
            onDismiss = {
                viewModel.onAction(Profile.Action.OnBackAuth)
                showSuccessDialog = false
            },
            onConfirm = {
                viewModel.onAction(Profile.Action.OnBackLogin)
                showSuccessDialog = false

            },
            containerConfirmButtonColor = MaterialTheme.colorScheme.confirm,
            labelConfirmButtonColor = MaterialTheme.colorScheme.onConfirm,
            confirmText = "Đăng nhập",
            dismissText = "Trở về",
            showConfirmButton = true
        )


    }
    if (showSheetImage){
        ImagePickerBottomSheet(
            onDismiss = { showSheetImage = false },
            onImageSelected = { uri -> viewModel.onAction(Profile.Action.OnAvatarChanged(uri)) })
    }


    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error ?: "Lỗi không xác định",
            onDismiss = {
                showErrorSheet = false
            },

            )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    FoodAppTheme {
        ProfileScreen(rememberNavController())
    }
}
