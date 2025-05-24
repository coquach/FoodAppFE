package com.example.foodapp.ui.screen.auth.login


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController


import com.example.foodapp.R

import com.example.foodapp.ui.screen.components.FoodAppTextField

import com.example.foodapp.navigation.Home

import com.example.foodapp.navigation.SendEmail

import com.example.foodapp.navigation.SignUp
import com.example.foodapp.navigation.Statistics
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.GoogleLoginButton

import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.PasswordTextField
import com.example.foodapp.ui.screen.components.ValidateTextField

import com.example.foodapp.ui.theme.FoodAppTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    isCustomer: Boolean = false,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showErrorSheet by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { event -> // Renamed 'it' for clarity
                when (event) {
                    Login.Event.NavigateForgot -> {
                        navController.navigate(SendEmail)
                    }
                    Login.Event.NavigateSignUp -> {
                        navController.navigate(SignUp)
                    }
                    Login.Event.NavigateToAdmin,
                    Login.Event.NavigateToCustomer,
                    Login.Event.NavigateToStaff -> {

                        val destination = when (event) {
                            Login.Event.NavigateToAdmin -> Statistics
                            else -> Home
                        }
                        navController.navigate(destination) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }

                    Login.Event.ShowError -> {
                        showErrorSheet = true
                    }
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 26.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)


        ) {

            Text(
                text = stringResource(id = R.string.log_in),
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                )
            Spacer(modifier = Modifier.size(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ValidateTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.email,
                    onValueChange = {
                        viewModel.onAction(Login.Action.EmailChanged(it))
                    },
                    labelText = stringResource(R.string.email),
                    errorMessage = uiState.emailError,
                    validate = {
                        viewModel.validate("email")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                )
                PasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.password,
                    onValueChange = {
                        viewModel.onAction(Login.Action.PasswordChanged(it))
                    } ,
                    errorMessage = uiState.passwordError,
                    validate = {
                        viewModel.validate("password")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    label = stringResource(R.string.password)
                )

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
                        viewModel.onAction(Login.Action.ForgotPasswordClicked)
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
                    onClick = {
                        viewModel.onAction(Login.Action.LoginClicked)
                    },
                    text = stringResource(R.string.log_in),
                    loading = uiState.loading,
                    enabled = uiState.isValid
                )
            }


            Spacer(modifier = Modifier.size(16.dp))
            if (isCustomer) {
                Text(
                    text = stringResource(id = R.string.dont_have_account),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            viewModel.onAction(Login.Action.SignUpClicked)
                        }
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold

                )
                GoogleLoginButton(
                    onGetCredentialResponse = { credential ->
                        viewModel.onAction(Login.Action.LoginWithGoogleClicked(credential))
                    }
                )

            }
        }
    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error.toString(),
            onDismiss = { showErrorSheet = false },

            )
    }
}

