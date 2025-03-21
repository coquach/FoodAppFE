package com.example.foodapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.foodapp.data.FoodApi
import com.example.foodapp.data.FoodAppSession
import com.example.foodapp.data.model.FoodItem

import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Cart
import com.example.foodapp.ui.navigation.FoodDetails
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.Login
import com.example.foodapp.ui.navigation.SignUp
import com.example.foodapp.ui.navigation.foodItemNavType
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.example.foodapp.ui.screen.auth.login.LoginScreen
import com.example.foodapp.ui.screen.auth.signup.SignUpScreen
import com.example.foodapp.ui.screen.cart.CartScreen
import com.example.foodapp.ui.screen.food_item_details.FoodDetailsScreen
import com.example.foodapp.ui.screen.home.HomeScreen

import com.example.foodapp.ui.theme.FoodAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var showSplashScreen = true

    @Inject
    lateinit var foodApi: FoodApi

    @Inject
    lateinit var session: FoodAppSession
    @OptIn(ExperimentalSharedTransitionApi::class)
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.onPrimary

                ) { innerPadding ->
                    val navController = rememberNavController()
                   SharedTransitionLayout {
                       NavHost(
                           navController = navController,
                           startDestination = FoodDetails(foodItem = FoodItem(
                               id = "3",
                               name = "Sushi Cá Hồi",
                               imageUrl = "https://www.themealdb.com/images/media/meals/g046bb1663960946.jpg",
                               description = "Sushi tươi ngon với cá hồi, cơm Nhật và wasabi.",
                               price = 12.99f
                           )), //demo on fake api
                           modifier = Modifier
                               .padding(innerPadding),
                           enterTransition = {
                               slideIntoContainer(
                                   towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                   animationSpec = tween(300)
                               ) + fadeIn(animationSpec = tween(300))
                           },
                           exitTransition = {
                               slideOutOfContainer(
                                   towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                   animationSpec = tween(300)
                               ) + fadeOut(animationSpec = tween(300))
                           },
                           popEnterTransition = {
                               slideIntoContainer(
                                   towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                   animationSpec = tween(300)
                               ) + fadeIn(animationSpec = tween(300))
                           },
                           popExitTransition = {
                               slideOutOfContainer(
                                   towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                   animationSpec = tween(300)
                               ) + fadeOut(animationSpec = tween(300))
                           }

                       ) {
                           composable<Auth> {
                               AuthScreen(navController)
                           }
                           composable<SignUp> {
                               SignUpScreen(navController)
                           }
                           composable<Login> {
                               LoginScreen(navController)
                           }
                           composable<Home> {
                               HomeScreen(navController, this)
                           }
                           composable<FoodDetails>(
                                typeMap = mapOf(typeOf<FoodItem>() to foodItemNavType)
                           ) {
                               val route = it.toRoute<FoodDetails>()
                               FoodDetailsScreen(
                                   navController,
                                   foodItem = route.foodItem,
                                    this,
                               )
                           }
                           composable<Cart> {
                               CartScreen(navController)
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



