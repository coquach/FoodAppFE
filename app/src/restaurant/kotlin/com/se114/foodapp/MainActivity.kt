package com.se114.foodapp

import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.painterResource

import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy

import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.foodapp.R
import com.example.foodapp.data.FoodApi
import com.example.foodapp.data.FoodAppSession
import com.example.foodapp.data.remote.FoodApi


import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.BottomNavItem

import com.example.foodapp.ui.navigation.FoodAppNavHost

import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.Login
import com.example.foodapp.ui.navigation.NavRoute
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.navigation.OrderDetails
import com.example.foodapp.ui.navigation.OrderList
import com.example.foodapp.ui.navigation.OrderSuccess
import com.example.foodapp.ui.navigation.Reservation

import com.example.foodapp.ui.navigation.SignUp
import com.example.foodapp.ui.navigation.foodItemNavType
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.example.foodapp.ui.screen.auth.login.LoginScreen
import com.example.foodapp.ui.screen.auth.signup.SignUpScreen

import com.example.foodapp.ui.screen.notification.NotificationListScreen
import com.example.foodapp.ui.screen.notification.NotificationViewModel


import com.example.foodapp.ui.theme.FoodAppTheme
import com.se114.foodapp.ui.screen.home.HomeScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var showSplashScreen = true

    @Inject
    lateinit var foodApi: FoodApi



    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                showSplashScreen
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.5f,
                    0f
                )
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.5f,
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
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            val darkMode = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(darkMode) }

            FoodAppTheme(darkTheme = isDarkMode) {

                val navController = rememberNavController()
                val navItems = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Favorite,
                    BottomNavItem.Reservation,
                    BottomNavItem.Order,
                    BottomNavItem.Setting
                )
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    bottomBar = {




                    }

                ) { innerPadding ->

                    SharedTransitionLayout {
                        FoodAppNavHost(
                            navController = navController,
                            startDestination = Auth, //demo on fake api
                            modifier = Modifier
                                .padding(innerPadding)

                        ) {
                            composable<Auth> {
                                shouldShowBottomNav.value = false
                                AuthScreen(navController, false)
                            }
                            composable<Login> {
                                shouldShowBottomNav.value = false
                                LoginScreen(navController, false)
                            }
                            composable<Home> {
                                shouldShowBottomNav.value = true
                                HomeScreen(navController)
                            }
                            composable<Notification> {
                                shouldShowBottomNav.value = false
                                NotificationListScreen(navController)
                            }
                            composable<Reservation> {
                                shouldShowBottomNav.value = true
                            }


                        }
                    }
                }
            }

        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            showSplashScreen = false
        }

    }


}



