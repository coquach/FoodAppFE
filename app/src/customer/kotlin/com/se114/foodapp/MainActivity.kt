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
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem

import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.foodapp.R
import com.example.foodapp.data.FoodApi
import com.example.foodapp.data.FoodAppSession
import com.example.foodapp.data.model.FoodItem
import com.example.foodapp.ui.FoodAppDialog
import com.example.foodapp.ui.navigation.AddAddress
import com.example.foodapp.ui.navigation.AddressList

import com.example.foodapp.ui.navigation.Auth
import com.example.foodapp.ui.navigation.Cart
import com.example.foodapp.ui.navigation.Checkout
import com.example.foodapp.ui.navigation.Favorite
import com.example.foodapp.ui.navigation.FoodAppNavHost
import com.example.foodapp.ui.navigation.FoodDetails
import com.example.foodapp.ui.navigation.Home
import com.example.foodapp.ui.navigation.Login
import com.example.foodapp.ui.navigation.NavRoute
import com.example.foodapp.ui.navigation.Notification
import com.example.foodapp.ui.navigation.OrderDetails
import com.example.foodapp.ui.navigation.OrderList
import com.example.foodapp.ui.navigation.OrderSuccess
import com.example.foodapp.ui.navigation.Reservation
import com.example.foodapp.ui.navigation.Setting

import com.example.foodapp.ui.navigation.SignUp
import com.example.foodapp.ui.navigation.foodItemNavType
import com.se114.foodapp.ui.screen.address.AddressListScreen
import com.se114.foodapp.ui.screen.address.addAddress.AddAddressScreen
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.example.foodapp.ui.screen.auth.login.LoginScreen
import com.example.foodapp.ui.screen.auth.signup.SignUpScreen
import com.se114.foodapp.ui.screen.cart.CartScreen
import com.se114.foodapp.ui.screen.checkout.CheckoutScreen
import com.se114.foodapp.ui.screen.food_item_details.FoodDetailsScreen
import com.se114.foodapp.ui.screen.home.HomeScreen
import com.example.foodapp.ui.screen.notification.NotificationListScreen
import com.example.foodapp.ui.screen.notification.NotificationViewModel
import com.example.foodapp.ui.screen.order.OrderListScreen
import com.example.foodapp.ui.screen.order.order_detail.OrderDetailScreen
import com.example.foodapp.ui.screen.order.order_success.OrderSuccess

import com.example.foodapp.ui.theme.FoodAppTheme
import com.se114.foodapp.ui.screen.profile.SettingScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
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

        data object Order : BottomNavItem(OrderList, R.drawable.ic_order)
        data object Setting : BottomNavItem(com.example.foodapp.ui.navigation.Setting, R.drawable.ic_user_circle)

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
                    BottomNavItem.Order,
                    BottomNavItem.Setting
                )
                val notificationViewModel: NotificationViewModel = hiltViewModel()

                val shouldShowBottomNav = remember {
                    mutableStateOf(true)
                }

                var isLoggedIn by remember { mutableStateOf(!session.getRefreshToken().isNullOrEmpty()) }
                var showSessionExpiredDialog by remember { mutableStateOf(false) }
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    session.sessionExpiredFlow.collectLatest {
                        isLoggedIn = false
                        if (!session.isManualLogout) {
                            showSessionExpiredDialog = true
                        }
                    }
                }
                if (showSessionExpiredDialog) {
                    FoodAppDialog(
                        title = "Phiên đăng nhập hết hạn",
                        message = "Bạn cần đăng nhập lại để tiếp tục sử dụng ứng dụng.",
                        onDismiss = {

                            showSessionExpiredDialog = false
                        },
                        onConfirm = {
                            showSessionExpiredDialog = false
                            isLoggedIn = false
                            navController.navigate(Auth) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        },
                        confirmText = "Đăng nhập lại",
                        dismissText = "Đóng",
                        showConfirmButton = true
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    bottomBar = {

                        val currentRoute =
                            navController.currentBackStackEntryAsState().value?.destination
                        AnimatedVisibility(visible = shouldShowBottomNav.value) {

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
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent
                                        )
                                    )
                                }
                            }
                        }


                    }

                ) { innerPadding ->

                    SharedTransitionLayout {
                        FoodAppNavHost(
                            navController = navController,
                            startDestination = if(isLoggedIn) Home else Auth,
                            modifier = Modifier
                                .padding(innerPadding),

                        ) {
                            composable<Auth> {
                                shouldShowBottomNav.value = false
                                AuthScreen(navController)
                            }
                            composable<SignUp> {
                                shouldShowBottomNav.value = false
                                SignUpScreen(navController)
                            }
                            composable<Login> {
                                shouldShowBottomNav.value = false
                                LoginScreen(navController)
                            }
                            composable<Home> {
                                shouldShowBottomNav.value = true
                                HomeScreen(navController, this, notificationViewModel = notificationViewModel)
                            }
                            composable<FoodDetails>(
                                typeMap = mapOf(typeOf<FoodItem>() to foodItemNavType)
                            ) {
                                shouldShowBottomNav.value = false
                                val route = it.toRoute<FoodDetails>()
                                FoodDetailsScreen(
                                    navController,
                                    foodItem = route.foodItem,
                                    this,
                                )
                            }
                            composable<Cart> {
                                shouldShowBottomNav.value = false
                                CartScreen(navController)
                            }
                            composable<Notification> {
                                shouldShowBottomNav.value = false
                                NotificationListScreen(navController)
                            }
                            composable<Favorite> {
                                shouldShowBottomNav.value = true
                            }
                            composable<Reservation> {
                                shouldShowBottomNav.value = true
                            }
                            composable<OrderList> {
                                shouldShowBottomNav.value = true
                                OrderListScreen(navController)
                            }
                            composable<AddressList> {
                                shouldShowBottomNav.value = false
                                AddressListScreen(navController)
                            }
                            composable<AddAddress> {
                                shouldShowBottomNav.value = false
                                AddAddressScreen(navController)
                            }
                            composable<Checkout> {
                                shouldShowBottomNav.value = false
                                CheckoutScreen(navController)
                            }
                            composable<OrderSuccess> {
                                shouldShowBottomNav.value = false
                                val orderID = it.toRoute<OrderSuccess>().orderId
                                OrderSuccess(orderID, navController)
                            }

                            composable<OrderDetails> {
                                shouldShowBottomNav.value = false
                                val orderID = it.toRoute<OrderDetails>().orderId
                                OrderDetailScreen(
                                    navController, orderID
                                )
                            }
                            composable<Setting> {
                                shouldShowBottomNav.value = true
                                SettingScreen(navController)
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



