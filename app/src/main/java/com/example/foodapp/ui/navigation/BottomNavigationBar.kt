package com.example.foodapp.ui.navigation

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf

import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

import com.example.foodapp.BuildConfig
import com.example.foodapp.R
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Straight
import com.exyte.animatednavbar.animation.balltrajectory.Teleport
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable

sealed class BottomNavItem(val route: NavRoute, val icon: Int) {
    data object Home : BottomNavItem(com.example.foodapp.ui.navigation.Home, R.drawable.ic_home)
    data object Favorite :
        BottomNavItem(com.example.foodapp.ui.navigation.Favorite, R.drawable.ic_favorite)

    data object Reservation :
        BottomNavItem(com.example.foodapp.ui.navigation.Reservation, R.drawable.ic_book)

    data object Order : BottomNavItem(OrderList, R.drawable.ic_order)
    data object Setting :
        BottomNavItem(
            com.example.foodapp.ui.navigation.Setting,
            if (BuildConfig.FLAVOR == "restaurant") R.drawable.ic_setting else R.drawable.ic_user_circle
        )

    data object Statistics :
        BottomNavItem(com.example.foodapp.ui.navigation.Statistics, R.drawable.ic_chart)

    data object Warehouse :
        BottomNavItem(com.example.foodapp.ui.navigation.Warehouse, R.drawable.ic_warehouse)

    data object Employee :
        BottomNavItem(com.example.foodapp.ui.navigation.Employee, R.drawable.ic_employee)

    data object Menu :
        BottomNavItem(com.example.foodapp.ui.navigation.Menu, R.drawable.ic_meal)


}

@Composable
fun BottomNavigationBar(navController: NavHostController, navItems: List<BottomNavItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val selectedIndex = navItems.indexOfFirst { it.route::class.qualifiedName == currentRoute }
        .takeIf { it >= 0 } ?: 0


    AnimatedNavigationBar(
        modifier = Modifier
            .height(80.dp)
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
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(item.icon),
                    contentDescription = "Bottom Bar Icon",
                    tint = if (selectedIndex == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.inversePrimary
                )
            }
        }
    }

}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }

}

