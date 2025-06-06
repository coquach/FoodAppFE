package com.se114.foodapp.ui.screen.setting


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.navigation.MyAddressList
import com.example.foodapp.navigation.Profile
import com.example.foodapp.navigation.VoucherPublic
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.SettingGroup
import com.example.foodapp.ui.screen.components.SettingItem
import com.example.foodapp.ui.screen.components.ThemeSwitcher


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val isNotificationMode = rememberSaveable { mutableStateOf(false) }

    var showErrorSheet by remember { mutableStateOf(false) }
    var showDialogLogout by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val handle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(handle) {
        if (handle?.get<Boolean>("shouldRefresh") == true) {
            handle["shouldRefresh"] = false
            viewModel.onAction(Setting.Action.OnLoadProfile)
        }
    }


    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                Setting.Event.NavigateToProfile -> {
                    navController.navigate(Profile(isUpdating = true))
                }

                Setting.Event.ShowLogoutDialog -> {
                    showDialogLogout = true
                }

                Setting.Event.NavigateToAddress -> {
                    navController.navigate(MyAddressList(isCheckout = false))
                }
                Setting.Event.NavigateToContact -> {}
                Setting.Event.NavigateToHelp -> {}
                Setting.Event.NavigateToPrivacy -> {}
                Setting.Event.NavigateToVoucher -> {
                    navController.navigate(VoucherPublic)
                }

                Setting.Event.ShowError -> {
                    showErrorSheet = true
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

                AsyncImage(
                    model = uiState.profile.avatar,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(8.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.avatar_placeholder),
                    error = painterResource(id = R.drawable.avatar_placeholder)
                )


            }
            Spacer(modifier = Modifier.height(10.dp))


            Text(
                text = uiState.profile.displayName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            TextButton(onClick = {
                viewModel.onAction(Setting.Action.OnProfileClicked)
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
                        SettingItem(Icons.Default.LocationOn, "Địa chỉ", onClick = {viewModel.onAction(Setting.Action.OnAddressClicked)})
                        SettingItem(Icons.Default.CardGiftcard, "Voucher của tôi", onClick = {viewModel.onAction(Setting.Action.OnVoucherClicked)})
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
                    viewModel.onAction(Setting.Action.OnLogOutClicked)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp)
            ) {
                Text(text = "Đăng xuất", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
    if (showDialogLogout) {

        FoodAppDialog(
            title = "Đăng xuất",
            titleColor = MaterialTheme.colorScheme.error,
            message = "Bạn có chắc chắn muốn đăng xuất khỏi tài khoản của mình không?",
            onDismiss = {

                showDialogLogout = false
            },
            onConfirm = {
                viewModel.onAction(Setting.Action.OnLogout)
                showDialogLogout = false

            },
            confirmText = "Đăng xuất",
            dismissText = "Đóng",
            showConfirmButton = true
        )

    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            onDismiss = {
                showErrorSheet = false
            },
            description = uiState.error.toString(),
        )
    }
}



