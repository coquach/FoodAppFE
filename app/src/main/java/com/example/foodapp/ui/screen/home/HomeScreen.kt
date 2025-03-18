package com.example.foodapp.ui.screen.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.ui.screen.home.categories.CategoriesList


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel(),

) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        val uiSate = viewModel.uiState.collectAsStateWithLifecycle()
        when(uiSate.value) {
            is HomeViewModel.HomeState.Loading -> {
                    CircularProgressIndicator()
            }
            is HomeViewModel.HomeState.Empty -> {
                // Show empty state message
            }
            is HomeViewModel.HomeState.Success -> {
                val categories = viewModel.categories
                CategoriesList(categories = categories, onCategorySelected = {

                })
            }
        }
    }

}

//Fake data

