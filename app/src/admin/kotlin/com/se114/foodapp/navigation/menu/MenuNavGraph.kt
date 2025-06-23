package com.se114.foodapp.navigation.menu

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.foodapp.data.model.Food
import com.example.foodapp.navigation.Category
import com.example.foodapp.navigation.FoodDetailsAdmin
import com.example.foodapp.navigation.Menu

import com.example.foodapp.navigation.FoodNavType
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.menu.MenuScreen
import com.se114.foodapp.ui.screen.menu.category.CategoryScreen
import com.se114.foodapp.ui.screen.menu.food_details.FoodDetailsAdminScreen
import kotlin.reflect.typeOf

@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.menuGraph(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope
) {
    with(sharedTransitionScope) {

        composable<Menu> {
            ScreenContainer(isBottomBarVisible = true) {
                MenuScreen(navController, this)
            }

        }


        composable<FoodDetailsAdmin>(
            typeMap = mapOf(typeOf<Food>() to FoodNavType)
        ) {
            ScreenContainer {
                FoodDetailsAdminScreen(navController)
            }

        }
        composable<Category> {

            ScreenContainer { CategoryScreen(navController) }

        }
    }


}
