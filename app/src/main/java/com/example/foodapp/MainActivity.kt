package com.example.foodapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.painterResource

import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.foodapp.data.FoodApi
import com.example.foodapp.data.FoodAppSession
import com.example.foodapp.data.model.FoodItem
import com.example.foodapp.ui.navigation.AddAddress
import com.example.foodapp.ui.navigation.AddressList

import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Cart
import com.example.foodapp.ui.navigation.Favorite
import com.example.foodapp.ui.navigation.FoodDetails
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.Login
import com.example.foodapp.ui.navigation.NavRoute
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.navigation.Order
import com.example.foodapp.ui.navigation.Reservation

import com.example.foodapp.ui.navigation.SignUp
import com.example.foodapp.ui.navigation.foodItemNavType
import com.example.foodapp.ui.screen.address.AddressListScreen
import com.example.foodapp.ui.screen.address.addAddress.AddAddressScreen
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
    private var showSplashScreen = true

    @Inject
    lateinit var foodApi: FoodApi

    @Inject
    lateinit var session: FoodAppSession

    sealed class BottomNavItem(val route: NavRoute, val icon: Int) {
        data object Home : BottomNavItem(com.example.foodapp.ui.navigation.Home, R.drawable.ic_home)
        data object Favorite :
            BottomNavItem(com.example.foodapp.ui.navigation.Favorite, R.drawable.ic_home)

        data object Reservation :
            BottomNavItem(com.example.foodapp.ui.navigation.Reservation, R.drawable.ic_home)

        data object Order : BottomNavItem(com.example.foodapp.ui.navigation.Order, R.drawable.ic_home)

    }

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
            FoodAppTheme {

                val navItems = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Favorite,
                    BottomNavItem.Reservation,
                    BottomNavItem.Order
                )

                val shouldShowButton = remember {
                    mutableStateOf(true)
                }

                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    bottomBar = {

                        val currentRoute =
                            navController.currentBackStackEntryAsState().value?.destination
                        AnimatedVisibility(visible = shouldShowButton.value) {

                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                navItems.forEach { item ->
                                    val selected =
                                        currentRoute?.hierarchy?.any { it.route == item.route::class.qualifiedName } == true
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(item.route)
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(id = item.icon),
                                                contentDescription = null,
                                                tint = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    )
                                }
                            }
                        }


                    }

                ) { innerPadding ->

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
                                shouldShowButton.value = false
                                AuthScreen(navController)
                            }
                            composable<SignUp> {
                                shouldShowButton.value = false
                                SignUpScreen(navController)
                            }
                            composable<Login> {
                                shouldShowButton.value = false
                                LoginScreen(navController)
                            }
                            composable<Home> {
                                shouldShowButton.value = true
                                HomeScreen(navController, this)
                            }
                            composable<FoodDetails>(
                                typeMap = mapOf(typeOf<FoodItem>() to foodItemNavType)
                            ) {
                                shouldShowButton.value = false
                                val route = it.toRoute<FoodDetails>()
                                FoodDetailsScreen(
                                    navController,
                                    foodItem = route.foodItem,
                                    this,
                                )
                            }
                            composable<Cart> {
                                shouldShowButton.value = false
                                CartScreen(navController)
                            }
                            composable<Notification> {
                                shouldShowButton.value = true
                            }
                            composable<Favorite> {
                                shouldShowButton.value = true
                            }
                            composable<Reservation> {
                                shouldShowButton.value = true
                            }
                            composable<Order> {
                                shouldShowButton.value = true
                            }
                            composable<AddressList> {
                                shouldShowButton.value = false
                                AddressListScreen(navController)
                            }
                            composable<AddAddress> {
                                shouldShowButton.value = false
                                AddAddressScreen(navController)
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



