package com.example.foodapp.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.data.FoodApi
import com.example.foodapp.data.models.response.Category
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.example.foodapp.ui.screen.home.categories.CategoriesList
import com.example.foodapp.ui.theme.FoodAppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mockk.mockk

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val uiSate = viewModel.uiState.collectAsStateWithLifecycle()
        when(uiSate.value) {
            is HomeViewModel.HomeState.Loading -> {

            }
            is HomeViewModel.HomeState.Empty -> {

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

class FakeHomeViewModel(foodApi: FoodApi) : HomeViewModel(foodApi) {
    init {

            // Fake data
            categories = listOf(
                Category("2025-03-18T12:00:00Z", "1", "https://source.unsplash.com/200x200/?coffee", "Coffee"),
                Category("2025-03-18T12:05:00Z", "2", "https://source.unsplash.com/200x200/?tea", "Tea"),
                Category("2025-03-18T12:10:00Z", "3", "https://source.unsplash.com/200x200/?juice", "Juice"),
                Category("2025-03-18T12:10:00Z", "3", "https://via.placeholder.com/150", "Juice"),
                Category("2025-03-18T12:10:00Z", "3", "https://via.placeholder.com/150", "Juice"),
                Category("2025-03-18T12:10:00Z", "3", "https://via.placeholder.com/150", "Juice"),
                Category("2025-03-18T12:10:00Z", "3", "https://via.placeholder.com/150", "Juice")




            )

            // Cập nhật trạng thái
            _uiState.value = HomeState.Success

    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FoodAppTheme {
        val fakeApi = mockk<FoodApi>(relaxed = true)
        val fakeViewModel = FakeHomeViewModel(foodApi = fakeApi)
        HomeScreen(navController = rememberNavController(), viewModel = fakeViewModel)
    }
}