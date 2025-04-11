package com.se114.foodapp.ui.app_nav.menu

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.FoodItem
import com.example.foodapp.ui.navigation.AddMenuItem
import com.example.foodapp.ui.navigation.FoodDetails
import com.example.foodapp.ui.navigation.Menu
import com.example.foodapp.ui.navigation.UpdateMenuItem
import com.example.foodapp.ui.navigation.foodItemNavType
import com.se114.foodapp.ui.screen.menu.MenuScreen
import com.se114.foodapp.ui.screen.menu.add_menu_item.AddMenuItemScreen
import kotlin.reflect.typeOf

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.menuGraph(
    navController: NavHostController,
    shouldShowBottomNav: MutableState<Boolean>,
    sharedTransitionScope: SharedTransitionScope
) {
    with(sharedTransitionScope) {
        composable<Menu> {
            shouldShowBottomNav.value = true
            MenuScreen(navController, this)
        }
        composable<AddMenuItem> {
            shouldShowBottomNav.value = false
            AddMenuItemScreen(navController)
        }

        composable<UpdateMenuItem>(
            typeMap = mapOf(typeOf<FoodItem>() to foodItemNavType)
        ) {
            shouldShowBottomNav.value = false
            val route = it.toRoute<FoodDetails>()
            AddMenuItemScreen(navController, isUpdating = true, foodItem = route.foodItem)
        }
    }


}
