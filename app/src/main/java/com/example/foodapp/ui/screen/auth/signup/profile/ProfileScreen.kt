package com.example.foodapp.ui.screen.auth.signup.profile

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.foodapp.R
import com.example.foodapp.data.model.enums.Gender
import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Login
import com.example.foodapp.ui.screen.components.BasicDialog
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
import com.example.foodapp.utils.ImageUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    isUpdating: Boolean = false,
    viewModel: ProfileViewModel = hiltViewModel(),
    ) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSheetImage by remember { mutableStateOf(false) }
    var showErrorSheet by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    var isLoading by remember { mutableStateOf(false) }

    Log.d("Profile" , "${profile.avatar}")

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        delay(100)
        viewModel.setMode(isUpdating)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                ProfileViewModel.ProfileEvents.NavigateAuth -> {
                    navController.navigate(Auth) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                ProfileViewModel.ProfileEvents.NavigateLogin -> {
                    navController.navigate(Login) {
                        popUpTo(Auth) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }

                }

                ProfileViewModel.ProfileEvents.BackToSetting -> {
                    Toast.makeText(
                        context,
                        "Cập nhật thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.previousBackStackEntry?.savedStateHandle?.set("shouldRefresh", true)
                    navController.popBackStack()
                }
                ProfileViewModel.ProfileEvents.ShowDialog -> {
                    showSuccessDialog = true
                }
            }
        }
    }

   when(uiState){
        ProfileViewModel.ProfileState.Loading -> {
            isLoading = true
        }
       ProfileViewModel.ProfileState.Error ->{
           isLoading = false
           showErrorSheet = true
       }
        else -> {
            isLoading =false
        }

    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        if (isUpdating)
            HeaderDefaultView(
                text = "Thông tin cá nhân",
                onBack = {
                    navController.navigateUp()
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
                    model = profile.avatar,
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
                value = profile.displayName,
                onValueChange = { viewModel.onChangeDisplayName(it) },
                labelText = stringResource(R.string.full_name),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )

            RadioGroupWrap(
                text = "Giới tính",
                options = Gender.entries.map { it.display },
                selectedOption = profile.gender ?: "",
                onOptionSelected = {
                    Log.d("GenderSelect", "Selected: $it")
                    viewModel.onChangeGender(it)
                }
            )
            DatePickerSample(
                text = "Ngày sinh",
                selectedDate = profile.dob,
                onDateSelected = { viewModel.onChangeDob(it) }
            )

            FoodAppTextField(
                value = profile.phoneNumber,
                onValueChange = {
                    viewModel.onChangePhoneNumber(it)
                },
                labelText = stringResource(R.string.phone_number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )


            LoadingButton(
                onClick ={if(isUpdating) viewModel.updateProfile(isUpdating = true) else viewModel.updateProfile() } ,
                modifier = Modifier.fillMaxWidth(),
                text = if (isUpdating) "Cập nhật" else "Xác nhận",
                loading = isLoading,
            )
        }

    }
    if (showSuccessDialog) {

        FoodAppDialog(
            title = "Đăng kí thành công",
            titleColor = MaterialTheme.colorScheme.confirm,
            message = "Bây giờ bạn có thể đăng nhập được rồi nha",
            onDismiss = {
                viewModel.onAuthClick()
                showSuccessDialog = false
            },
            onConfirm = {
                viewModel.onLoginClick()
                showSuccessDialog = false

            },
            containerConfirmButtonColor = MaterialTheme.colorScheme.confirm,
            labelConfirmButtonColor = MaterialTheme.colorScheme.onConfirm,
            confirmText = "Đăng nhập",
            dismissText = "Trở về",
            showConfirmButton = true
        )


    }
    ImagePickerBottomSheet(
        showSheet = showSheetImage,
        onDismiss = { showSheetImage = false },
        onImageSelected = { uri -> viewModel.onChangeAvatar(uri) })

    if (showErrorSheet) {
        ErrorModalBottomSheet(
            title = viewModel.error,
            description = viewModel.errorDescription,
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
