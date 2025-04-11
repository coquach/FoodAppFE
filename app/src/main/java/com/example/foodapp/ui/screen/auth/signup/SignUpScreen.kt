package com.example.foodapp.ui.screen.auth.signup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.R
import com.example.foodapp.ui.screen.components.BasicDialog
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.Login
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.utils.ValidateField
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {

    val email = viewModel.email.collectAsStateWithLifecycle()
    val password = viewModel.password.collectAsStateWithLifecycle()
    val confirmPassword = viewModel.confirmPassword.collectAsStateWithLifecycle()


    val emailError = viewModel.emailError
    val passwordError = viewModel.passwordError
    val conFirmPasswordError = viewModel.confirmPasswordError

    var isTouched by remember { mutableStateOf(false) }

    var showPassword by remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showErrorSheet by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }




    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null)
            scope.launch {
                showErrorSheet = true
            }
    }

    LaunchedEffect(true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is SignUpViewModel.SignUpNavigationEvent.NavigateHome -> {
                    navController.navigate(Home) {
                        popUpTo(Auth) {
                            inclusive = true
                        }
                    }
                }

                is SignUpViewModel.SignUpNavigationEvent.NavigateLogin -> {
                    navController.navigate(Login) {
                        popUpTo(Auth) {
                            inclusive = true
                        }
                    }
                }

                is SignUpViewModel.SignUpNavigationEvent.showSuccesDialog -> {
                    showSuccessDialog = true
                }
            }

        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {


        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        when (uiState.value) {
            is SignUpViewModel.SignUpEvent.Error -> {

                loading.value = false
                errorMessage.value = "Failed"
            }

            is SignUpViewModel.SignUpEvent.Loading -> {
                loading.value = true
                errorMessage.value = null
            }

            else -> {
                loading.value = false
                errorMessage.value = null
            }
        }



        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center


        ) {

            Text(
                text = stringResource(id = R.string.sign_up),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary

            )
            Spacer(modifier = Modifier.size(20.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FoodAppTextField(
                        value = email.value,
                        onValueChange = {
                            viewModel.onEmailChanged(it)
                            if (!isTouched) isTouched = true
                        },
                        labelText = stringResource(R.string.email),
                        isError = emailError.value != null,
                        errorText = emailError.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (isTouched && !focusState.isFocused) {
                                    ValidateField(
                                        email.value,
                                        emailError,
                                        "Email không hợp lệ"
                                    ) { it.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")) }
                                }
                            },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        maxLines = 1
                    )
                    FoodAppTextField(
                        value = password.value,
                        onValueChange = {
                            viewModel.onPasswordChanged(it)
                            if (!isTouched) isTouched = true
                        },
                        labelText = stringResource(R.string.password),
                        isError = passwordError.value != null,
                        errorText = passwordError.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (isTouched && !focusState.isFocused) {
                                    ValidateField(
                                        password.value,
                                        passwordError,
                                        "Mật khẩu phải có ít nhất 6 ký tự"
                                    ) { it.length >= 6 }
                                }
                            },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {

                            IconButton(
                                onClick = {
                                    showPassword = !showPassword
                                },
                            ) {
                                if (!showPassword) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_eye),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_slash_eye),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        maxLines = 1

                    )
                    FoodAppTextField(
                        value = confirmPassword.value,
                        onValueChange = {
                            viewModel.onConfirmPasswordChanged(it)
                            if (!isTouched) isTouched = true
                        },
                        labelText = stringResource(R.string.confirm_password),
                        isError = conFirmPasswordError.value != null,
                        errorText = conFirmPasswordError.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (isTouched && !focusState.isFocused) {
                                    ValidateField(
                                        confirmPassword.value,
                                        conFirmPasswordError,
                                        "Mật khẩu không trùng khớp"
                                    ) { it == password.value }
                                }
                            },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {

                            IconButton(
                                onClick = {
                                    showPassword = !showPassword
                                },
                            ) {
                                if (!showPassword) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_eye),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_slash_eye),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        maxLines = 1

                    )
                }
            }


            Spacer(modifier = Modifier.size(30.dp))
            LoadingButton(
                onClick = viewModel::onSignUpClick,
                text = stringResource(R.string.sign_up),
                loading = loading.value
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = stringResource(id = R.string.already_have_account),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        viewModel.onLoginClick()
                    }
                    .fillMaxWidth(),
                textAlign = TextAlign.Center

            )

        }
    }
    if (showSuccessDialog) {

        FoodAppDialog(
            title = "Đăng kí thành công",
            titleColor = Color.Green.copy(alpha = 0.8f),
            message = "Bây giờ bạn có thể đăng nhập được rồi nha",
            onDismiss = {

                showSuccessDialog = false
            },
            onConfirm = {
                viewModel.onLoginClick()
                showSuccessDialog = false

            },
            confirmText = "Đăng nhập",
            dismissText = "Đóng",
            showConfirmButton = true
        )


    }

    if (showErrorSheet) {
        ModalBottomSheet(onDismissRequest = { showErrorSheet = false }, sheetState = sheetState) {
            BasicDialog(
                title = viewModel.error,
                description = viewModel.errorDescription,
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        showErrorSheet = false
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    FoodAppTheme {
        SignUpScreen(rememberNavController())
    }
}