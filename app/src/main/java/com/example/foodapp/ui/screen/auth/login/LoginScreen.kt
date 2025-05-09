package com.example.foodapp.ui.screen.auth.login


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.Credential
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.BuildConfig

import com.example.foodapp.R
import com.example.foodapp.ui.screen.components.BasicDialog
import com.example.foodapp.ui.screen.components.FoodAppTextField

import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.OrderList
import com.example.foodapp.ui.navigation.SendEmail

import com.example.foodapp.ui.navigation.SignUp
import com.example.foodapp.ui.navigation.Statistics
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.GoogleLoginButton
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.LoadingButton

import com.example.foodapp.ui.theme.FoodAppTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    isCustomer: Boolean = false,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val email = viewModel.email.collectAsStateWithLifecycle()
    val password = viewModel.password.collectAsStateWithLifecycle()
    var showPassword by remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    var rememberMe by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null)
            scope.launch {
                showDialog = true
            }
    }


    LaunchedEffect(true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {


                is LoginViewModel.LoginNavigationEvent.NavigateSignUp -> {
                    navController.navigate(SignUp)
                }

                is LoginViewModel.LoginNavigationEvent.NavigateForgot -> {
                    navController.navigate(SendEmail)
                }

                LoginViewModel.LoginNavigationEvent.NavigateToAdmin -> {
                    navController.navigate(Statistics) {
                        popUpTo(navController.graph.startDestinationId)
                    }
                }
                LoginViewModel.LoginNavigationEvent.NavigateToCustomer -> {
                    navController.navigate(Home) {
                        popUpTo(navController.graph.startDestinationId)
                    }

                }
                LoginViewModel.LoginNavigationEvent.NavigateToStaff -> {
                    navController.navigate(Home) {
                        
                        popUpTo(navController.graph.startDestinationId)
                    }
                }
            }

        }
    }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {


        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        when (uiState.value) {
            is LoginViewModel.LoginEvent.Error -> {
                loading.value = false
                errorMessage.value = "Failed"
            }

            is LoginViewModel.LoginEvent.Loading -> {
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
                .padding(vertical = 26.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center


        ) {

            Text(
                text = stringResource(id = R.string.log_in),
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,

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
                        onValueChange = { viewModel.onEmailChanged(it) },
                        labelText = stringResource(R.string.email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        maxLines = 1
                    )

                    FoodAppTextField(
                        value = password.value,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        labelText = stringResource(R.string.password),
                        modifier = Modifier.fillMaxWidth(),
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
                        singleLine = true,
                        maxLines = 1

                    )

                    Spacer(modifier = Modifier.size(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { }
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = !it }
                            )
                            Text(
                                text = "Nhớ mật khẩu",
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }


                        TextButton(onClick = {
                            viewModel.onForgotPasswordClick()
                        }) {
                            Text(
                                text = stringResource(R.string.forgot_password),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    LoadingButton(
                        onClick = viewModel::onLoginClick,
                        text = stringResource(R.string.log_in),
                        loading = loading.value
                    )
                }
            }

            Spacer(modifier = Modifier.size(16.dp))
            if (isCustomer) {
                Text(
                    text = stringResource(id = R.string.dont_have_account),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            viewModel.onSignUpClick()
                        }
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold

                )
                GoogleLoginButton(
                    onGetCredentialResponse = { credential ->
                        viewModel.onLoginWithGoogleClick(credential)
                    }
                )

            }
        }
    }
    if (showDialog) {
        ErrorModalBottomSheet(
            title = viewModel.error,
            description = viewModel.errorDescription,
            onDismiss = { showDialog = false },

        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FoodAppTheme {
        LoginScreen(rememberNavController())
    }
}