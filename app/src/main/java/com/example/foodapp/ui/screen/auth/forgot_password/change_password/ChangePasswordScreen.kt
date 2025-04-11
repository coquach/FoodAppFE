package com.example.foodapp.ui.screen.auth.forgot_password.change_password

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.ui.navigation.ResetPasswordSuccess
import com.example.foodapp.ui.screen.auth.signup.SignUpViewModel
import com.example.foodapp.ui.screen.components.BasicDialog
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.OTPTextFields
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.utils.ValidateField
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    oobCode: String = "",
    method: String = "",
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val password = viewModel.password.collectAsStateWithLifecycle()
    val confirmPassword = viewModel.confirmPassword.collectAsStateWithLifecycle()


    val passwordError = viewModel.passwordError
    val conFirmPasswordError = viewModel.confirmPasswordError

    var isTouched by remember { mutableStateOf(false) }

    var showPassword by remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showErrorSheet by remember { mutableStateOf(false) }



    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null)
            scope.launch {
                showErrorSheet = true
            }
    }
    LaunchedEffect(oobCode, method) {
        viewModel.setResetPasswordArgs(oobCode, method)
    }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                ChangePasswordViewModel.ChangePasswordEvents.NavigateToSuccess -> {
                    navController.navigate(ResetPasswordSuccess)
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        when (uiState.value) {
            is ChangePasswordViewModel.ChangePasswordState.Error -> {

                loading.value = false
                errorMessage.value = "Failed"
            }

            is ChangePasswordViewModel.ChangePasswordState.Loading -> {
                loading.value = true
                errorMessage.value = null
            }

            else -> {
                loading.value = false
                errorMessage.value = null
            }
        }
        Text(
            text = stringResource(R.string.reset_password),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            fontWeight = FontWeight.ExtraBold,
        )
        Image(
            painter = painterResource(id = R.drawable.change_password),
            contentDescription = "",
            modifier = Modifier.size(240.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FoodAppTextField(
                    labelText = "Nhập mật khẩu mới",
                    value = password.value,
                    onValueChange = {
                        viewModel.onPasswordChanged(it)
                        if (!isTouched) isTouched = true
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
                )

                FoodAppTextField(
                    labelText = "Xác nhận mật khẩu",
                    value = confirmPassword.value,
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
                    onValueChange = {
                        viewModel.onConfirmPasswordChanged(it)
                        if (!isTouched) isTouched = true
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
                )


                Spacer(modifier = Modifier.size(20.dp))
                LoadingButton(
                    onClick = viewModel::resetPassword,
                    text = "Xác nhận",
                    loading = loading.value
                )


            }

        }


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
fun ChangePasswordScreenPreview() {
    var showPassword by remember { mutableStateOf(false) }
    FoodAppTheme {
        Column {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.reset_password),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    fontWeight = FontWeight.ExtraBold,
                )
                Image(
                    painter = painterResource(id = R.drawable.change_password),
                    contentDescription = "",
                    modifier = Modifier.size(240.dp)
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FoodAppTextField(
                            labelText = "Nhập mật khẩu mới",
                            value = "",
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth(),
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
                        )

                        FoodAppTextField(
                            labelText = "Xác nhận mật khẩu",
                            value = "",
                            modifier = Modifier
                                .fillMaxWidth(),
                            onValueChange = {},
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
                        )



                        Button(
                            onClick = {

                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            contentPadding = PaddingValues(vertical = 14.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 2.dp
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Xác nhận",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }


                    }

                }


            }
        }
    }
}