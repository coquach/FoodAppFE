package com.se114.foodapp.navigation.menu

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.foodapp.data.model.Food
import com.example.foodapp.navigation.AddFood
import com.example.foodapp.navigation.Category
import com.example.foodapp.navigation.Menu
import com.example.foodapp.navigation.UpdateFood
import com.example.foodapp.navigation.FoodNavType
import com.example.foodapp.utils.ScreenContainer
import com.se114.foodapp.ui.screen.menu.MenuScreen
import com.se114.foodapp.ui.screen.menu.add_menu_item.AddFoodScreen
import com.se114.foodapp.ui.screen.menu.category.CategoryScreen
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
            ScreenContainer {
                MenuScreen(navController, this)
            }

        }
        composable<AddFood> {
            shouldShowBottomNav.value = false
            ScreenContainer{
                AddFoodScreen(navController)
            }

        }

        composable<UpdateFood>(
            typeMap = mapOf(typeOf<Food>() to FoodNavType)
        ) {
            shouldShowBottomNav.value = false
            val route = it.toRoute<UpdateFood>()
            ScreenContainer {
                AddFoodScreen(navController, isUpdating = true, Food = route.Food)
            }

        }
        composable<Category> {
            shouldShowBottomNav.value = false
            ScreenContainer { CategoryScreen(navController) }

        }
    }


}
