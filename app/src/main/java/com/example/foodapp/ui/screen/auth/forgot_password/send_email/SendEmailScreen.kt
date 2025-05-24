package com.example.foodapp.ui.screen.auth.forgot_password.send_email

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.navigation.Auth
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.navigation.Login
import com.example.foodapp.ui.screen.components.BasicDialog
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.utils.ValidateField
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendEmailScreen(
    navController: NavController,
    viewModel: SendEmailViewModel = hiltViewModel()
) {
    val email = viewModel.email.collectAsStateWithLifecycle()
    val emailError = viewModel.emailError
    var isTouched by remember { mutableStateOf(false) }

    val loading = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showErrorSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current




    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is SendEmailViewModel.SendEmailEvents.NavigateToLogin -> {
                    navController.popBackStack(route = Login, inclusive = false)

                }

                is SendEmailViewModel.SendEmailEvents.ShowAlreadySent -> {
                    Toast.makeText(context, "Bạn đã gửi email rồi! 📩 Kiểm tra hộp thư trước khi gửi lại nha.", Toast.LENGTH_SHORT).show()
                }
                is SendEmailViewModel.SendEmailEvents.ShowError -> {
                    showErrorSheet = true
                }
                is SendEmailViewModel.SendEmailEvents.ShowSuccess -> {
                    Toast.makeText(
                        context,
                        "Email đã được gửi! Hãy kiểm tra hộp thư của bạn nhé 📬",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate(Auth) {
                        popUpTo(navController.graph.startDestinationId)

                    }
                }
                is SendEmailViewModel.SendEmailEvents.ShowTooFast -> {
                    Toast.makeText(context, "Đừng spam nha bro 😅 Chờ tí rồi gửi lại!", Toast.LENGTH_SHORT).show()
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


            is SendEmailViewModel.SendEmailState.Loading -> {
                loading.value = true
            }

           else -> {
               loading.value = false
           }
        }
        Text(
            text = stringResource(R.string.forgot_password),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            fontWeight = FontWeight.ExtraBold,
        )
        Image(
            painter = painterResource(id = R.drawable.ic_forgot_password_illustration),
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
                Text(
                    text = stringResource(R.string.enter_email_to_reset_password),
                    color = MaterialTheme.colorScheme.outline,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),

                    )
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
                        imeAction = ImeAction.Done
                    ),
                )
                Spacer(modifier = Modifier.size(20.dp))
                LoadingButton(
                    onClick = viewModel::onSendEmailClick,
                    text = "Gửi email khôi phục",
                    loading = loading.value
                )


            }

        }

        Text(
            text = stringResource(R.string.remember_password),
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable { viewModel.onLoginClick() }
                .padding(top = 20.dp),
            textAlign = TextAlign.Center
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
