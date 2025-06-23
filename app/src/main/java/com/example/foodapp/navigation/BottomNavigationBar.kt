package com.example.foodapp.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition.Center
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

import com.example.foodapp.BuildConfig
import com.example.foodapp.R
import com.example.foodapp.ui.screen.components.ItemCount
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Straight
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class BottomNavItem(val route: NavRoute, val icon: Int) {
    //Customer
    data object Home : BottomNavItem(com.example.foodapp.navigation.Home, R.drawable.ic_home)
    data object Favorite :
        BottomNavItem(com.example.foodapp.navigation.Favorite, R.drawable.ic_favorite)

    data object Reservation :
        BottomNavItem(com.example.foodapp.navigation.Reservation, R.drawable.ic_book)

    data object Notification :
        BottomNavItem(com.example.foodapp.navigation.Notification, R.drawable.ic_nofication)

    data object Order : BottomNavItem(OrderList, R.drawable.ic_order)
    data object Setting :
        BottomNavItem(
            com.example.foodapp.navigation.Setting,
            if (BuildConfig.FLAVOR == "admin" || BuildConfig.FLAVOR == "staff" || BuildConfig.FLAVOR == "shipper") R.drawable.ic_setting else R.drawable.ic_user_circle
        )

    //Admin


    data object Warehouse :
        BottomNavItem(com.example.foodapp.navigation.Warehouse, R.drawable.ic_warehouse)

    data object Employee :
        BottomNavItem(com.example.foodapp.navigation.Employee, R.drawable.ic_employee)

    data object Menu :
        BottomNavItem(com.example.foodapp.navigation.Menu, R.drawable.ic_meal)

    //Staff
    data object Export:
            BottomNavItem(com.example.foodapp.navigation.Export,R.drawable.ic_export )


}

@Composable
fun BottomNavigationBar(navController: NavHostController, navItems: List<BottomNavItem>, unreadCount: Int?=null, state: MutableState<Boolean>,) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    // Tìm index nếu có
    val matchedIndex = navItems.indexOfFirst { it.route::class.qualifiedName == currentRoute }
    if (matchedIndex >= 0 && matchedIndex != selectedIndex) {
        selectedIndex = matchedIndex
    }


    AnimatedVisibility(
        visible = state.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        AnimatedNavigationBar(
            modifier = Modifier
                .height(70.dp)
                .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            selectedIndex = selectedIndex,
            barColor = MaterialTheme.colorScheme.primary,
            ballColor = MaterialTheme.colorScheme.primary,
            cornerRadius = shapeCornerRadius(cornerRadius = 34.dp),
            ballAnimation = Straight(tween(durationMillis = 350, easing = FastOutSlowInEasing)),
            indentAnimation = Height(tween(durationMillis = 200, easing = LinearOutSlowInEasing))
        ) {
            navItems.forEachIndexed { index, item ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .noRippleClickable {


                            navController.navigate(item.route) {

                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }

                                launchSingleTop = true

                                restoreState = true
                            }


                        },
                ) {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = "Bottom Bar Icon",
                        tint = if (selectedIndex == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.inversePrimary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    unreadCount?.let {
                        if(item.route == Notification && unreadCount > 0) {
                            ItemCount(unreadCount)
                        }
                    }

                }
            }
        }
    }




}
@Composable
fun bottomBarVisibility(
    navController: NavController,
): MutableState<Boolean> {

    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    when (navBackStackEntry?.destination?.route) {
        Home::class.qualifiedName -> bottomBarState.value = true
        Notification::class.qualifiedName -> bottomBarState.value = true
        Setting::class.qualifiedName -> bottomBarState.value = true
        OrderList::class.qualifiedName -> bottomBarState.value = true
        Menu::class.qualifiedName -> bottomBarState.value = true
        Warehouse::class.qualifiedName -> bottomBarState.value = true
        Employee::class.qualifiedName -> bottomBarState.value = true
        Favorite::class.qualifiedName -> bottomBarState.value = true
        Export::class.qualifiedName -> bottomBarState.value = true

        else -> bottomBarState.value = false
    }

    return bottomBarState
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }

}

