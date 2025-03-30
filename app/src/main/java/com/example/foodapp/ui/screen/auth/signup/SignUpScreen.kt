package com.example.foodapp.ui.screen.auth.signup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.foodapp.ui.BasicDialog
import com.example.foodapp.ui.FoodAppTextField
import com.example.foodapp.ui.GroupSocialButtons
import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.Login
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
    val username = viewModel.username.collectAsStateWithLifecycle()
    val email = viewModel.email.collectAsStateWithLifecycle()
    val password = viewModel.password.collectAsStateWithLifecycle()
    val fullName = viewModel.fullName.collectAsStateWithLifecycle()
    val phoneNumber = viewModel.phoneNumber.collectAsStateWithLifecycle()

    val usernameError = viewModel.usernameError
    val emailError = viewModel.emailError
    val passwordError = viewModel.passwordError
    val fullNameError = viewModel.fullNameError
    val phoneNumberError = viewModel.phoneNumberError
    var isTouched by remember { mutableStateOf(false) }

    var showPassword by remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }


    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null)
            scope.launch {
                showDialog = true
            }
    }

    val context = LocalContext.current
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

                else -> {
                    // Handle other navigation events
                }
            }

        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {


        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        when (uiState.value) {
            is SignUpViewModel.SignUpEvent.Error -> {
                // show error
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
            FoodAppTextField(
                value = fullName.value,
                onValueChange = { viewModel.onFullNameChanged(it) },
                label = {
                    Text(text = stringResource(id = R.string.full_name))
                },
                isError = fullNameError.value != null,
                errorText = fullNameError.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (isTouched && !focusState.isFocused) {
                            ValidateField(
                                fullName.value,
                                fullNameError,
                                "Họ tên không được để trống"
                            ) { it.isNotBlank() }
                        }
                    },
                singleLine = true,
                maxLines = 1
            )
            FoodAppTextField(
                value = username.value,
                onValueChange = {
                    viewModel.onUsernameChanged(it)
                    if (!isTouched) isTouched = true
                },
                label = {
                    Text(text = stringResource(id = R.string.username))
                },
                isError = usernameError.value != null,
                errorText = usernameError.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (isTouched && !focusState.isFocused) {
                            ValidateField(
                                username.value,
                                usernameError,
                                "Tên đăng nhập phải có ít nhất 4 ký tự"
                            ) { it.length >= 4 }
                        }
                    },
                singleLine = true,
                maxLines = 1
            )
            FoodAppTextField(
                value = email.value,
                onValueChange = {
                    viewModel.onEmailChanged(it)
                    if (!isTouched) isTouched = true
                },
                label = {
                    Text(text = stringResource(id = R.string.email))
                },
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
                singleLine = true,
                maxLines = 1
            )
            FoodAppTextField(
                value = password.value,
                onValueChange = {
                    viewModel.onPasswordChanged(it)
                    if (!isTouched) isTouched = true
                },
                label = {
                    Text(text = stringResource(id = R.string.password))
                },
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

                    Image(
                        painter = if (showPassword) painterResource(id = R.drawable.ic_eye) else painterResource(
                            id = R.drawable.ic_slash_eye
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showPassword = !showPassword }

                    )
                },
                singleLine = true,
                maxLines = 1

            )
            FoodAppTextField(
                value = phoneNumber.value,
                onValueChange = {
                    viewModel.onPhoneNumberChanged(it)
                    if (!isTouched) isTouched = true
                },
                label = {
                    Text(text = stringResource(id = R.string.phone_number))
                },
                isError = phoneNumberError.value != null,
                errorText = phoneNumberError.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (isTouched && !focusState.isFocused) {
                            ValidateField(
                                phoneNumber.value,
                                phoneNumberError,
                                "Số điện thoại không hợp lệ"
                            ) { it.matches(Regex("^(\\+84|0)[0-9]{9,10}\$")) }
                        }
                    },
                singleLine = true,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.size(30.dp))
            Button(
                onClick = viewModel::onSignUpClick,
                modifier = Modifier
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Box {
                    AnimatedContent(
                        targetState = loading.value,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith
                                    fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
                        }
                    ) { target ->
                        if (target) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .size(24.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.sign_up),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }

                    }
                }

            }
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
    if (showDialog) {
        ModalBottomSheet(onDismissRequest = { showDialog = false }, sheetState = sheetState) {
            BasicDialog(
                title = viewModel.error,
                description = viewModel.errorDescription,
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        showDialog = false
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