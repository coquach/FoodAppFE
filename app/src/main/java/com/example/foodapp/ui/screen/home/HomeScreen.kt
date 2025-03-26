package com.example.foodapp.ui.screen.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.ui.ItemCount
import com.example.foodapp.ui.MyFloatingActionButton
import com.example.foodapp.ui.navigation.Cart
import com.example.foodapp.ui.screen.home.categories.CategoriesList


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel(),

) {
    val cartSize by viewModel.cartSize.collectAsState()
    Scaffold(
        floatingActionButton =
        {
            MyFloatingActionButton(
                onClick = {
                    navController.navigate(Cart)
                },
                bgColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Box(modifier = Modifier.size(56.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_cart),
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        contentDescription = null,
                        modifier = Modifier.align(Center)
                    )

                    if (cartSize > 0) {
                        ItemCount(cartSize)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding)
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
    }


}




//Fake data

