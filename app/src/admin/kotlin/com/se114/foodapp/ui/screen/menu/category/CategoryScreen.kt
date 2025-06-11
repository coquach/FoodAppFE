package com.se114.foodapp.ui.screen.menu.category

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.data.model.Menu
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.Retry
import com.example.foodapp.ui.theme.confirm
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavController,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showErrorSheet by remember { mutableStateOf(false) }

    var isCreating by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var showSetStatusDialog by remember { mutableStateOf(false) }


    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                is CategoryState.Event.ShowError -> {
                    showErrorSheet = true
                }

                CategoryState.Event.OnBack -> {
                    navController.popBackStack()
                }

                is CategoryState.Event.ShowMessage -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HeaderDefaultView(
                text = "Danh mục thực đơn",
                onBack = {
                    viewModel.onAction(CategoryState.Action.OnBack)
                },
            )
        }

        when (uiState.menuState) {
            is CategoryState.MenuState.Error -> {
                val error = (uiState.menuState as CategoryState.MenuState.Error).message
                Retry(
                    message = error,
                    onClicked = {
                        viewModel.onAction(CategoryState.Action.Retry)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            CategoryState.MenuState.Loading -> {
                Loading(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            CategoryState.MenuState.Success -> {
                if (uiState.menuList.isEmpty() && !isCreating) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(
                                onClick = {
                                    viewModel.onAction(CategoryState.Action.OnSelectMenu(Menu()))
                                    isCreating = true

                                },
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(color = MaterialTheme.colorScheme.outline)
                                    .padding(0.dp)

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Category",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Text(
                                text = "Thêm danh mục",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                    }

                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uiState.menuList.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp),

                                ) {
                                items(uiState.menuList) { category ->
                                    val isEditing =
                                        isEditMode && uiState.menuSelected.id == category.id

                                    AnimatedContent(
                                        targetState = isEditing,
                                        transitionSpec = {
                                            (slideInVertically { it } + fadeIn()) togetherWith
                                                    (slideOutVertically { -it } + fadeOut())
                                        },
                                        label = "Edit Switch"
                                    ) { editing ->
                                        if (editing) {
                                            EditMenuCard(
                                                onModifyClick = {
                                                    viewModel.onAction(CategoryState.Action.UpdateMenu)
                                                    isEditMode = false
                                                },
                                                value = uiState.menuSelected.name,
                                                onValueChange = { newName ->
                                                    viewModel.onAction(
                                                        CategoryState.Action.OnChangeName(
                                                            newName
                                                        )
                                                    )
                                                },
                                                onCloseClick = {
                                                    viewModel.onAction(
                                                        CategoryState.Action.OnSelectMenu(
                                                            Menu()
                                                        )
                                                    )
                                                    isEditMode = false
                                                },
                                                isHideIcon = uiState.isLoading
                                            )
                                        } else {
                                            SwipeableActionsBox(
                                                startActions = listOf(
                                                    SwipeAction(
                                                        icon = rememberVectorPainter(Icons.Default.Edit),
                                                        background = MaterialTheme.colorScheme.primary,
                                                        onSwipe = {
                                                            viewModel.onAction(
                                                                CategoryState.Action.OnSelectMenu(
                                                                    category
                                                                )
                                                            )
                                                            isEditMode = true
                                                        }
                                                    )
                                                ),
                                                endActions = listOf(
                                                    SwipeAction(
                                                        icon = rememberVectorPainter(if (category.active) Icons.Default.VisibilityOff else Icons.Default.Visibility),
                                                        background = if (category.active) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.confirm,
                                                        onSwipe = {
                                                            isEditMode = false
                                                            viewModel.onAction(
                                                                CategoryState.Action.OnSelectMenu(
                                                                    category
                                                                )
                                                            )
                                                            showSetStatusDialog = true
                                                        }
                                                    )
                                                )
                                            ) {
                                                MenuCard(
                                                    menu = category
                                                )
                                            }
                                        }
                                    }
                                }
                                item {
                                    if (isCreating) {
                                        EditMenuCard(
                                            onModifyClick = {
                                                viewModel.onAction(CategoryState.Action.AddMenu)
                                                isCreating = false
                                            },
                                            value = uiState.menuSelected.name,
                                            onValueChange = { newName ->
                                                viewModel.onAction(
                                                    CategoryState.Action.OnChangeName(
                                                        newName
                                                    )
                                                )
                                            },
                                            onCloseClick = {
                                                isCreating = false
                                            },
                                            isHideIcon = uiState.isLoading,
                                        )

                                    } else if (!uiState.isLoading && !isEditMode) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    viewModel.onAction(CategoryState.Action.OnSelectMenu(Menu()))
                                                    isCreating = true
                                                          },
                                                modifier = Modifier

                                                    .size(56.dp)
                                                    .clip(CircleShape)
                                                    .background(color = MaterialTheme.colorScheme.outline)
                                                    .padding(0.dp)

                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add Category",
                                                    modifier = Modifier.size(30.dp),
                                                    tint = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                        }

                                    }
                                }


                            }

                        }


                    }


                }
            }
        }

    }
    if (showSetStatusDialog) {
        FoodAppDialog(
            title = if (uiState.menuSelected.active) "Ẩn nhà menu" else "Hiện menu",
            message = if (uiState.menuSelected.active) "Bạn có chắc chắn muốn ẩn menu này không?" else "Bạn có chắc chắn muốn hiện menu này không?",
            onDismiss = {
                showSetStatusDialog = false
            },
            onConfirm = {

                viewModel.onAction(CategoryState.Action.UpdateStatus)
                showSetStatusDialog = false

            },
            confirmText = if (uiState.menuSelected.active) "Ẩn" else "Hiện",
            dismissText = "Đóng",
            showConfirmButton = true
        )

    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.errorMessage.toString(),
            onDismiss = {
                showErrorSheet = false
            },
        )
    }

}

@Composable
fun MenuCard(
    menu: Menu,
) {
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (menu.active) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.outline.copy(
                    alpha = 0.5f
                )
            )
            .padding(vertical = 16.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = Icons.Default.Fastfood,
                contentDescription = null,
                tint = if (menu.active) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = menu.name,
                style = MaterialTheme.typography.titleMedium,
                color = if (menu.active) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    }


}


@Composable
fun EditMenuCard(
    modifier: Modifier = Modifier,
    onModifyClick: () -> Unit,
    onCloseClick: () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    isHideIcon: Boolean = false,

    ) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        FoodAppTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Nhập tên danh mục...") }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            if (!isHideIcon) {
                IconButton(
                    onClick = {
                        onModifyClick.invoke()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.primary)
                        .padding(0.dp),


                    ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirm",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(28.dp)
                    )

                }
            }
            IconButton(
                onClick = {
                    onCloseClick.invoke()
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.outline)
                    .padding(0.dp),


                ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Confirm",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(28.dp)
                )

            }

        }


    }
}




