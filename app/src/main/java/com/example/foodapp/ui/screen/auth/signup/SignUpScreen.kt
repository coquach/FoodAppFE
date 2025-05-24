package com.example.foodapp.ui.screen.auth.signup


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.R
import com.example.foodapp.navigation.CreateProfile
import com.example.foodapp.navigation.Login
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet

import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.PasswordTextField
import com.example.foodapp.ui.screen.components.ValidateTextField
import com.example.foodapp.ui.theme.FoodAppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel(),
) {


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showErrorSheet by remember { mutableStateOf(false) }


    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { event ->
                when (event) {
                    SignUp.Event.NavigateToLogin -> {
                        navController.navigate(Login)
                    }

                    SignUp.Event.NavigateToProfile -> {
                        navController.navigate(CreateProfile)
                    }

                    SignUp.Event.ShowError -> {
                        showErrorSheet = true
                    }
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)


        ) {

            Text(
                text = stringResource(id = R.string.sign_up),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary

            )
            ValidateTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.email,
                onValueChange = {
                    viewModel.onAction(SignUp.Action.EmailChanged(it))
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
                    viewModel.onAction(SignUp.Action.PasswordChanged(it))
                },
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
            PasswordTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.confirmPassword,
                onValueChange = {
                    viewModel.onAction(SignUp.Action.PasswordChanged(it))
                },
                errorMessage = uiState.confirmPasswordError,
                validate = {
                    viewModel.validate("password")
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                label = stringResource(R.string.confirm_password)
            )
        }

        Spacer(modifier = Modifier.size(30.dp))
        LoadingButton(
            onClick = {
                viewModel.onAction(SignUp.Action.SignUpClicked)
            },
            text = stringResource(R.string.sign_up),
            modifier = Modifier.fillMaxWidth(),
            loading = uiState.loading,
            enabled = uiState.isValid
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = stringResource(id = R.string.already_have_account),
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    viewModel.onAction(SignUp.Action.LoginClicked)
                }
                .fillMaxWidth(),
            textAlign = TextAlign.Center

        )

    }

    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error.toString(),
            onDismiss = { showErrorSheet = false },

            )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    FoodAppTheme {
        SignUpScreen(rememberNavController())
    }
}