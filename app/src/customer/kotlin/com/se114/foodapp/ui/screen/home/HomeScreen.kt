package com.se114.foodapp.ui.screen.home


import android.Manifest
import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.navigation.Cart
import com.example.foodapp.navigation.FoodDetails
import com.example.foodapp.ui.screen.common.FoodView
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.ItemCount
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.MyFloatingActionButton
import com.example.foodapp.ui.screen.components.Retry
import com.example.foodapp.ui.screen.components.gridItems
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.se114.foodapp.ui.screen.chat_box.ChatBoxScreen
import com.se114.foodapp.ui.screen.home.banner.Banners


@OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun SharedTransitionScope.HomeScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel(),

    ) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isOpenChatBox by remember { mutableStateOf(false) }
    var showErrorSheet by remember { mutableStateOf(false) }

    Log.d("isOpenChatBox", isOpenChatBox.toString())

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { event ->
                when (event) {
                    Home.Event.GoToCart -> {
                        navController.navigate(Cart)
                    }

                    is Home.Event.GoToDetails -> {
                        navController.navigate(FoodDetails(event.food))
                    }

                    Home.Event.ShowChatBox -> {
                        isOpenChatBox = true
                    }

                    Home.Event.ShowError -> {
                        showErrorSheet = true
                    }
                }
            }
    }
    val notificationPermissionState =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        ) else null

    if (notificationPermissionState != null) {
        LaunchedEffect(Unit) {

            if (!notificationPermissionState.status.isGranted) {
                notificationPermissionState.launchPermissionRequest()
            }

        }
    }

    Scaffold(
        floatingActionButton =
            {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MyFloatingActionButton(
                        onClick = {
                            isOpenChatBox = true
                        },
                        bgColor = MaterialTheme.colorScheme.onPrimary,
                    ) {
                        Box(modifier = Modifier.size(56.dp), contentAlignment = Center) {
                            Image(
                                painter = painterResource(R.drawable.chatbot_ic),
                                contentDescription = "Chat box",
                                modifier = Modifier.size(24.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                            )
                        }

                    }
                    MyFloatingActionButton(
                        onClick = {
                            viewModel.onAction(Home.Action.OnCartClicked)
                        },
                        bgColor = MaterialTheme.colorScheme.onPrimary,
                    ) {
                        Box(modifier = Modifier.size(56.dp)) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Center)
                                    .size(24.dp)
                            )

                            if (uiState.cartSize > 0) {
                                ItemCount(uiState.cartSize)
                            }
                        }
                    }
                }

            }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        start = padding.calculateStartPadding(LayoutDirection.Ltr),
                        end = padding.calculateEndPadding(LayoutDirection.Ltr)
                    )
                )
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = viewModel.getGreetingTitle(uiState.profile.displayName),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.5f)

                )
                Box(
                    modifier = Modifier
                        .size(50.dp),
                    contentAlignment = Alignment.Center
                ) {

                    AsyncImage(
                        model = uiState.profile.avatar,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(8.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.avatar_placeholder),
                        error = painterResource(id = R.drawable.avatar_placeholder)
                    )


                }
            }
            Banners(
                onClick = {
                },
                modifier = Modifier.fillMaxWidth()
            )



            Text(
                text = "🍽️ Hôm nay ăn gì?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),

            )

            when (uiState.foodSuggestionsState) {
                is Home.FoodSuggestions.Error -> {
                    val error = (uiState.foodSuggestionsState as Home.FoodSuggestions.Error).message
                    Retry(
                        message = error,
                        onClicked = {
                            viewModel.onAction(Home.Action.Retry)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)

                    )
                }

                Home.FoodSuggestions.Loading -> {
                    Loading(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)

                    )
                }

                Home.FoodSuggestions.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                      ,
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        gridItems(
                            data = uiState.foodSuggestions,
                            nColumns = 2,
                            key = {
                                it.id
                            }
                        ) {
                            FoodView(
                                food = it,
                                animatedVisibilityScope = animatedVisibilityScope,
                                onClick = {
                                    viewModel.onAction(Home.Action.OnFoodClicked(it))
                                },
                                isCustomer = true,
                                isAnimated = false
                            )
                        }
                    }
                }
            }


        }


    }
    if (isOpenChatBox) {
        ChatBoxScreen(
            onDismiss = {
                isOpenChatBox = false
            },
            modifier = Modifier.height(700.dp)
        )
    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            onDismiss = {
                showErrorSheet = false
            },
            description = uiState.error.toString()
        )
    }
}


