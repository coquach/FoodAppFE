package com.se114.foodapp.ui.screen.setting.security

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.ui.screen.components.AppButton
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import androidx.core.net.toUri
import com.example.foodapp.R
import com.example.foodapp.navigation.ResetPassword
import com.example.foodapp.ui.screen.auth.signup.SignUp
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.PasswordTextField
import com.example.foodapp.ui.theme.confirm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    navController: NavController,
    viewModel: SecurityViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showErrorSheet by remember { mutableStateOf(false) }
    var showDialogPassword by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect { event ->
            when (event) {
                SecurityState.Event.OnBack -> {
                    navController.popBackStack()
                }

                is SecurityState.Event.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                SecurityState.Event.ShowError -> {
                    showErrorSheet = true
                }
                SecurityState.Event.NavigateToChangePassword -> {
                    navController.navigate(ResetPassword)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkGoogleVerify()
        viewModel.checkVerifyEmail()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderDefaultView(
            text = "Bảo mật tài khoản",
            onBack = {
                viewModel.onAction(SecurityState.Action.OnBack)
            }
        )
        if (uiState.isGoogleVerified) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Tài khoản của bạn được đăng nhập bằng Google.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Nếu muốn cài đặt bảo mật, hãy mở trang quản lý Google.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    AppButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = "https://myaccount.google.com/security".toUri()
                            }
                            context.startActivity(intent)
                        },
                        text = "Cài đặt bảo mật"
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = Color.Black.copy(alpha = 0.4f),
                        spotColor = Color.Black.copy(alpha = 0.4f)
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(18.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Verify",
                    modifier = Modifier.size(30.dp),
                    tint = if (uiState.isVerifyEmail) MaterialTheme.colorScheme.confirm else MaterialTheme.colorScheme.error
                )

                Text(
                    text = if (uiState.isVerifyEmail) "Email đã được xác thực" else "Email chưa được xác thực",
                    color = if (uiState.isVerifyEmail) MaterialTheme.colorScheme.confirm else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                AppButton(
                    text = "Xác thực",
                    onClick = {
                        viewModel.onAction(SecurityState.Action.OnVerifyEmail)
                    },
                    shape = RoundedCornerShape(20.dp),
                    enable = !uiState.isVerifyEmail
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = Color.Black.copy(alpha = 0.4f),
                        spotColor = Color.Black.copy(alpha = 0.4f)
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(18.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Password,
                    contentDescription = "Password Change",
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.onBackground

                )
                Text(
                    text = "Đổi mật khẩu",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                AppButton(
                    text = "Đổi mật khẩu",
                    onClick = {
                        viewModel.onAction(SecurityState.Action.NavigateToChangePassword)
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }


        if (showErrorSheet) {
            ErrorModalBottomSheet(
                description = uiState.error.toString(),
                onDismiss = {
                    showErrorSheet = false
                }
            )
        }

}
