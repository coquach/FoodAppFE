package com.se114.foodapp

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.OvershootInterpolator
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.BaseFoodAppActivity
import com.example.foodapp.MainViewModel
import com.example.foodapp.SplashViewModel
import com.example.foodapp.navigation.Auth
import com.example.foodapp.navigation.BottomNavItem
import com.example.foodapp.navigation.BottomNavigationBar
import com.example.foodapp.navigation.OrderDetails
import com.example.foodapp.navigation.ResetPassword
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.example.foodapp.ui.theme.FoodAppTheme
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.se114.foodapp.ui.app_nav.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MainActivity : BaseFoodAppActivity() {
    private val splashViewModel: SplashViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.R)
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !splashViewModel.isLoading.value
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.8f,
                    0f
                )
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.8f,
                    0f
                )
                zoomX.duration = 500
                zoomY.duration = 500
                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()
                zoomX.doOnEnd {
                    screen.remove()
                }
                zoomY.doOnEnd {
                    screen.remove()
                }
                zoomY.start()
                zoomX.start()
            }
        }

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

            val startDestination by splashViewModel.startDestination
            val isLoading by splashViewModel.isLoading.collectAsState()

            FoodAppTheme(darkTheme = isDarkMode) {

                val navItems = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Favorite,
                    BottomNavItem.Order,
                    BottomNavItem.Notification,
                    BottomNavItem.Setting
                )

                val shouldShowBottomNav = remember {
                    mutableStateOf(true)
                }
                val notificationViewModel: NotificationViewModel = hiltViewModel()
                val unreadCount = notificationViewModel.unreadCount.collectAsStateWithLifecycle()
                val navController = rememberNavController()

                LaunchedEffect(key1 = true) {
                    viewModel.event.collectLatest {
                        when (it) {
                            is MainViewModel.HomeEvent.NavigateToOrderDetail -> {
                                navController.navigate(
                                    OrderDetails(
                                        it.order
                                    )
                                )
                            }

                            is MainViewModel.HomeEvent.NavigateToResetPassword -> {
                                navController.navigate(
                                    ResetPassword(
                                        it.oobCode,
                                        it.mode
                                    )
                                )
                            }
                        }
                    }
                }
                LaunchedEffect(Unit) {
                    splashViewModel.navigateEventFlow.collectLatest { event ->
                        when (event) {
                            is SplashViewModel.UiEvent.NavigateToAuth -> {
                                navController.navigate(Auth) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                }
                if(!isLoading){
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            AnimatedVisibility(visible = shouldShowBottomNav.value) {
                                BottomNavigationBar(navController, navItems, unreadCount)
                            }

                        }

                    ) { innerPadding ->

                        SharedTransitionLayout {
                            AppNavGraph(
                                navController = navController,
                                innerPadding = innerPadding,
                                shouldShowBottomNav = shouldShowBottomNav,
                                notificationViewModel = notificationViewModel,
                                startDestination = startDestination,
                                isDarkMode = isDarkMode,
                                onThemeUpdated = {
                                    isDarkMode = !isDarkMode
                                },
                                sharedTransitionScope = this
                            )
                        }
                    }
                }

            }

        }


    }


}



