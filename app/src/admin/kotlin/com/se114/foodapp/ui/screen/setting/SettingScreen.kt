package com.se114.foodapp.ui.screen.setting

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.example.foodapp.navigation.FoodTableAdmin

import com.example.foodapp.navigation.Supplier
import com.example.foodapp.navigation.Voucher
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.HeaderDefaultView
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
    remember { mutableStateOf(false) }
    var showErrorSheet by remember { mutableStateOf(false) }
    val isNotificationMode = rememberSaveable { mutableStateOf(false) }

    var showDialogLogout by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    BackHandler {
        activity?.moveTaskToBack(true)
    }
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                is SettingState.Event.ShowError -> {
                    showErrorSheet = true
                }

                is SettingState.Event.NavigateToSupplier -> {
                    navController.navigate(Supplier)
                }

                is SettingState.Event.NavigateToVoucher -> {
                    navController.navigate(Voucher)
                }

                is SettingState.Event.NavigateToFoodTable -> {
                    navController.navigate(FoodTableAdmin)
                }

                SettingState.Event.NavigateToContact -> {

                }

                SettingState.Event.NavigateToHelp -> {

                }

                SettingState.Event.NavigateToPrivacy -> {

                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        HeaderDefaultView(
            text = "Cài đặt"
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
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
            SettingGroup(
                items = listOf(
                    {
                        SettingItem(
                            Icons.Default.LocalShipping,
                            "Nhà cung cấp",
                            onClick = { viewModel.onAction(SettingState.Action.OnSupplierClicked) })
                        SettingItem(
                            Icons.Default.CardGiftcard,
                            "Voucher & Khuyến mãi",
                            onClick = { viewModel.onAction(SettingState.Action.OnVoucherClicked) })
                        SettingItem(Icons.Default.TableRestaurant, "Bàn tại quán", onClick = {
                            viewModel.onAction(SettingState.Action.OnFoodTableClicked)
                        })
                    }
                )
            )
            SettingGroup(
                items = listOf(
                    {
                        SettingItem(Icons.AutoMirrored.Filled.Help, "Hỏi đáp & trợ giúp")
                        SettingItem(Icons.AutoMirrored.Filled.Message, "Liên hệ")
                        SettingItem(Icons.Default.PrivacyTip, "Chính sách bảo mật")
                    }
                )
            )
            Button(
                onClick = {
                    showDialogLogout = true
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
                viewModel.onAction(SettingState.Action.OnLogout)
                showDialogLogout = false

            },
            confirmText = "Đăng xuất",
            dismissText = "Đóng",
            showConfirmButton = true
        )


    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error.toString(),
            onDismiss = {
                showErrorSheet = false
            },

            )
    }
}

