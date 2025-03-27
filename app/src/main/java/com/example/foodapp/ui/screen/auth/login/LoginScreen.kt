package com.example.foodapp.ui.screen.auth.login


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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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

import com.example.foodapp.ui.navigation.SignUp

import com.example.foodapp.ui.theme.FoodAppTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    isCustomer: Boolean = true,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val username = viewModel.username.collectAsStateWithLifecycle()
    val password = viewModel.password.collectAsStateWithLifecycle()
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
                is LoginViewModel.LoginNavigationEvent.NavigateHome -> {
                    navController.navigate(Home) {
                        popUpTo(Auth) {
                            inclusive = true
                        }
                    }
                }
                is LoginViewModel.LoginNavigationEvent.NavigateSignUp -> {
                    navController.navigate(SignUp)
                }
                else -> {
                    // Handle other navigation events
                }
            }

        }
    }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center ) {


        val uiState = viewModel.uiState.collectAsStateWithLifecycle()
        when (uiState.value) {
            is LoginViewModel.LoginEvent.Error -> {
                // show error
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
            FoodAppTextField(
                value = username.value,
                onValueChange = { viewModel.onUsernameChanged(it) },
                label = {
                    Text(text = stringResource(id = R.string.username))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )

            FoodAppTextField(
                value = password.value,
                onValueChange = { viewModel.onPasswordChanged(it) },
                label = {
                    Text(text = stringResource(id = R.string.password))
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {

                    Image(
                        painter = if (showPassword) painterResource(id = R.drawable.ic_eye) else painterResource(id = R.drawable.ic_slash_eye),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showPassword = !showPassword}

                    )
                },
                singleLine = true,
                maxLines = 1

            )
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                onClick = viewModel::onLoginClick,
                modifier = Modifier
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
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
                                text = stringResource(id = R.string.log_in),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }

                    }
                }

            }
            Spacer(modifier = Modifier.size(16.dp))
            if(isCustomer) {
                Text(
                    text = stringResource(id = R.string.dont_have_account),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            viewModel.onSignUpClick()
                        }
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center

                )
                GroupSocialButtons(color = Color.Black)

            }
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
fun LoginScreenPreview() {
    FoodAppTheme {
        LoginScreen(rememberNavController())
    }
}