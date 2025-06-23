package com.se114.foodapp


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.BaseFoodAppActivity
import com.example.foodapp.MainViewModel
import com.example.foodapp.navigation.Auth
import com.example.foodapp.navigation.BottomNavItem
import com.example.foodapp.navigation.BottomNavigationBar
import com.example.foodapp.navigation.Notification
import com.example.foodapp.navigation.bottomBarVisibility
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.example.foodapp.ui.theme.FoodAppTheme
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.se114.foodapp.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MainActivity : BaseFoodAppActivity() {




    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {



        enableEdgeToEdge()
        super.onCreate(savedInstanceState)


        setContent {
            val systemUiController: SystemUiController = rememberSystemUiController()
            LaunchedEffect(Unit) {
                systemUiController.isNavigationBarVisible = false
                systemUiController.isStatusBarVisible = false
            }
            val darkMode = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(darkMode) }

            val screen = viewModel.startDestination.value



            FoodAppTheme(darkTheme = isDarkMode) {

                val navItems = listOf(
                    BottomNavItem.Order,
                    BottomNavItem.Notification,
                    BottomNavItem.Setting
                )
                val notificationViewModel: NotificationViewModel = hiltViewModel()
                val unreadCount by notificationViewModel.unreadCount.collectAsStateWithLifecycle()

                val navController = rememberNavController()

                LaunchedEffect(key1 = true) {
                    viewModel.event.collectLatest {
                        when (it) {


                            is MainViewModel.UiEvent.NavigateToNotification -> {
                                navController.navigate(Notification)
                            }

                            is MainViewModel.UiEvent.NavigateToResetPassword -> {

                            }

                            MainViewModel.UiEvent.NavigateToAuth -> {
                                navController.navigate(Auth) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                }

                if (screen == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Loading()
                    }
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {

                            BottomNavigationBar(
                                navController, navItems = navItems, state = bottomBarVisibility(navController)
                            )

                        }

                    ) { innerPadding ->

                        SharedTransitionLayout {
                            AppNavGraph(
                                navController = navController,
                                innerPadding = PaddingValues(
                                    bottom = 75.dp
                                ),
                                startDestination = screen,
                                isDarkMode = isDarkMode,
                                onThemeUpdated = {
                                    isDarkMode = !isDarkMode
                                },
                                notificationViewModel = notificationViewModel
                            )
                        }


                    }
                }
            }
        }

    }


}



