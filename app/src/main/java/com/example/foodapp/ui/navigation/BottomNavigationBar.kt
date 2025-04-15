package com.example.foodapp.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.foodapp.BuildConfig
import com.example.foodapp.R

sealed class BottomNavItem(val route: NavRoute, val icon: Int) {
    data object Home : BottomNavItem(com.example.foodapp.ui.navigation.Home, R.drawable.ic_home)
    data object Favorite :
        BottomNavItem(com.example.foodapp.ui.navigation.Favorite, R.drawable.ic_favorite)

    data object Reservation :
        BottomNavItem(com.example.foodapp.ui.navigation.Reservation, R.drawable.ic_book)

    data object Order : BottomNavItem(OrderList, R.drawable.ic_order)
    data object Setting :
        BottomNavItem(com.example.foodapp.ui.navigation.Setting, if(BuildConfig.FLAVOR == "restaurant") R.drawable.ic_setting else R.drawable.ic_user_circle)
    data object  Statistics :
        BottomNavItem(com.example.foodapp.ui.navigation.Statistics, R.drawable.ic_chart)
    data object  Warehouse :
        BottomNavItem(com.example.foodapp.ui.navigation.Warehouse, R.drawable.ic_warehouse)
    data object  Employee :
        BottomNavItem(com.example.foodapp.ui.navigation.Employee, R.drawable.ic_employee)
    data object  Menu :
        BottomNavItem(com.example.foodapp.ui.navigation.Menu, R.drawable.ic_meal)


}

@Composable
fun BottomNavigationBar(navController: NavHostController, navItems: List<BottomNavItem>) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        navItems.forEach { item ->
            val selected =
                currentRoute?.hierarchy?.any { it.route == item.route::class.qualifiedName } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) { // Chỉ navigate nếu chưa ở màn hiện tại
                        navController.navigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = null,
//
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colorScheme.outline,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                )
            )
        }
    }
}


