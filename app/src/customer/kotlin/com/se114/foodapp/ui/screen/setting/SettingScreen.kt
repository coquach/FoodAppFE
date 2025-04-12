package com.se114.foodapp.ui.screen.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.ThemeSwitcher
import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Profile
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val isDarkMode = rememberSaveable { mutableStateOf(false) }
    val isNotificationMode = rememberSaveable { mutableStateOf(false) }

    val showDialogLogout = remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                is SettingViewModel.SettingEvents.OnLogout -> {
                    showDialogLogout.value = true
                }

                is SettingViewModel.SettingEvents.NavigateToAuth -> {
                    navController.navigate(Auth) {
                        popUpTo(navController.graph.startDestinationId)

                    }
                }

                is SettingViewModel.SettingEvents.NavigateToProfile -> {
                    navController.navigate(Profile)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                // Avatar tròn
                Image(
                    painter = painterResource(id = R.drawable.avatar_placeholder),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(8.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                )

            }
            Spacer(modifier = Modifier.height(10.dp))


            Text(
                "Quách Vĩnh Cơ",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            TextButton(onClick = {
                viewModel.onProfileClicked()
            }) {
                Text("Sửa thông tin", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
            }
            Spacer(modifier = Modifier.height(20.dp))

            SettingGroup(
                items = listOf(
                    {
                        SettingItem(
                            Icons.Default.BrightnessMedium,
                            "Chủ đề ",
                            customView = {
                                ThemeSwitcher(
                                    darkTheme = darkTheme,
                                    size = 35.dp,
                                    padding = 6.dp,
                                    onClick = onThemeUpdated
                                )
                            }
                        )
                        SettingItem(
                            Icons.Default.Notifications,
                            "Thông báo",
                            toggleState = isNotificationMode
                        )
                        SettingItem(Icons.Default.Language, "Ngôn ngữ", customView = {})

                    },
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            SettingGroup(
                items = listOf(
                    {
                        SettingItem(Icons.Default.LocationOn, "Địa chỉ")
                        SettingItem(Icons.Default.CardGiftcard, "Voucher")
                    }
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            SettingGroup(
                items = listOf(
                    {
                        SettingItem(Icons.AutoMirrored.Filled.Help, "Hỏi đáp & trợ giúp")
                        SettingItem(Icons.AutoMirrored.Filled.Message, "Liên hệ")
                        SettingItem(Icons.Default.PrivacyTip, "Chính sách bảo mật")
                    }
                )
            )

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.onShowLogoutDialog()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp)
            ) {
                Text(text = "Đăng xuất", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
    if (showDialogLogout.value) {

        FoodAppDialog(
            title = "Đăng xuất",
            titleColor = MaterialTheme.colorScheme.error,
            message = "Bạn có chắc chắn muốn đăng xuất khỏi tài khoản của mình không?",
            onDismiss = {

                showDialogLogout.value = false
            },
            onConfirm = {
                viewModel.onLogoutClicked()
                showDialogLogout.value = false

            },
            confirmText = "Đăng xuất",
            dismissText = "Đóng",
            showConfirmButton = true
        )


    }
}

@Composable
fun SettingGroup(items: List<@Composable () -> Unit>) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            items.forEachIndexed { index, item ->
                item()
                if (index < items.size - 1) {
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                }
            }
        }
    }
}


@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    value: String? = null,
    toggleState: MutableState<Boolean>? = null,
    customView: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        when {
            toggleState != null -> { // Nếu là Toggle
                Switch(checked = toggleState.value, onCheckedChange = { toggleState.value = it })
            }

            customView != null -> { // Nếu là ComboBox (Dropdown)
                customView()
            }

        }
    }
}


