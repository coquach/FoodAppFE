package com.se114.foodapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.compose.rememberNavController

import com.example.foodapp.BaseFoodAppActivity
import com.example.foodapp.HomeViewModel
import com.example.foodapp.SplashViewModel
import com.example.foodapp.data.remote.FoodApi

import com.example.foodapp.data.model.ResetPasswordArgs

import com.example.foodapp.ui.navigation.BottomNavItem
import com.example.foodapp.ui.navigation.BottomNavigationBar

import com.example.foodapp.ui.navigation.OrderDetails

import com.example.foodapp.ui.navigation.ResetPassword

import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.example.foodapp.ui.theme.FoodAppTheme

import com.se114.foodapp.app_nav.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseFoodAppActivity() {
    @Inject
    lateinit var foodApi: FoodApi
    @Inject
    lateinit var splashViewModel: SplashViewModel


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
            val darkMode = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(darkMode) }

            val screen by splashViewModel.startDestination

            FoodAppTheme(darkTheme = isDarkMode) {

                val navItems = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Favorite,
                    BottomNavItem.Reservation,
                    BottomNavItem.Order,
                    BottomNavItem.Setting
                )
                val notificationViewModel: NotificationViewModel = hiltViewModel()
                val shouldShowBottomNav = remember {
                    mutableStateOf(true)
                }
                val navController = rememberNavController()
                val deepLinkUri by viewModel.deepLinkUri.collectAsState()
                LaunchedEffect(deepLinkUri) {
                    deepLinkUri?.let { uri ->
                        Log.d("reset pass", "check")
                        val oobCode = uri.getQueryParameter("oobCode")
                        val mode = uri.getQueryParameter("mode")

                        if (!oobCode.isNullOrBlank() && !mode.isNullOrBlank()) {
                            val route = ResetPassword(ResetPasswordArgs(oobCode, mode))
                            navController.navigate(route)
                        }
                        viewModel.clearDeepLink()
                    }
                }
                LaunchedEffect(key1 = true) {
                    viewModel.event.collectLatest {
                        when (it) {
                            is HomeViewModel.HomeEvent.NavigateToOrderDetail -> {
                                navController.navigate(
                                    OrderDetails(
                                        it.order
                                    )
                                )
                            }
                        }
                    }
                }
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        AnimatedVisibility(visible = shouldShowBottomNav.value) {
                            BottomNavigationBar(navController, navItems)
                        }


                    }

                ) { innerPadding ->

                    SharedTransitionLayout {
                       AppNavGraph(
                           navController = navController,
                           innerPadding = innerPadding,
                           shouldShowBottomNav = shouldShowBottomNav,
                           notificationViewModel = notificationViewModel,
                           startDestination = screen,
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



