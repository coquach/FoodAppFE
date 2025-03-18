package com.example.foodapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.data.FoodApi
import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.Login
import com.example.foodapp.ui.navigation.SignUp
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.example.foodapp.ui.screen.auth.login.LoginScreen
import com.example.foodapp.ui.screen.auth.signup.SignUpScreen

import com.example.foodapp.ui.theme.FoodAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var showSplashScreen = true
    @Inject
    lateinit var foodApi: FoodApi
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                showSplashScreen
            }
            setOnExitAnimationListener{screen ->
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
            FoodAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Auth,
                        modifier = Modifier
                            .padding(innerPadding)
                    ) {
                        composable<Auth>() {
                            AuthScreen(navController)
                        }
                        composable<SignUp>() {
                            SignUpScreen(navController)
                        }
                        composable<Login>() {
                            LoginScreen(navController)
                        }
                        composable<Home>() {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Red)
                            ){

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
fun ComponentActivity.enableEdgeToEdge(
    statusBarStyle : SystemBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
    navigationBarStyle : SystemBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
    ) {

}


