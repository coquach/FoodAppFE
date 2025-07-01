package com.se114.foodapp.ui.screen.setting

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.navigation.Contact
import com.example.foodapp.navigation.Help
import com.example.foodapp.navigation.Privacy
import com.example.foodapp.ui.screen.components.AppButton
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


                SettingState.Event.NavigateToContact -> {
                    navController.navigate(Contact)
                }

                SettingState.Event.NavigateToHelp -> {
                    navController.navigate(Help)
                }

                SettingState.Event.NavigateToPrivacy -> {
                    navController.navigate(Privacy)
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


                    },
                )
            )

            SettingGroup(
                items = listOf(
                    {
                        SettingItem(
                            Icons.AutoMirrored.Filled.Help,
                            "Hướng dẫn sử dụng",
                            onClick = { viewModel.onAction(SettingState.Action.OnHelpClicked) })
                        SettingItem(
                            Icons.AutoMirrored.Filled.Message,
                            "Liên hệ",
                            onClick = { viewModel.onAction(SettingState.Action.OnContactClicked) })
                        SettingItem(
                            Icons.Default.PrivacyTip,
                            "Chính sách bảo mật",
                            onClick = { viewModel.onAction(SettingState.Action.OnPrivacyClicked) })
                    }
                )
            )
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    showDialogLogout = true
                },
                text = "Đăng xuất",
            )
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

