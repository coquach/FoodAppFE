package com.se114.foodapp.ui.screen.menu.category

import android.graphics.Color
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.twotone.Delete
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.data.model.Menu
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.theme.FoodAppTheme
import com.se114.foodapp.ui.screen.employee.EmployeeViewModel
import kotlinx.coroutines.flow.collectLatest
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun CategoryScreen(
    navController: NavController,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categoryList by viewModel.categoryList.collectAsStateWithLifecycle()
    val categoryName by viewModel.categoryName.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var isCreating by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val editingItemId = remember { mutableStateOf<Long?>(null) }
    var isEditMode by remember { mutableStateOf(false) }



    when (uiState) {
        CategoryViewModel.CategoryState.Error -> {
            isLoading = false
        }
        CategoryViewModel.CategoryState.Loading -> {
            isLoading = true
        }

        CategoryViewModel.CategoryState.Nothing -> {
            isLoading = false
        }
        CategoryViewModel.CategoryState.Success -> {
            isLoading = false
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is CategoryViewModel.CategoryEvents.ShowErrorMessage -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
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
                text = "Danh mục món ăn",
                onBack = {
                    navController.popBackStack()
                },

                )
        }


        if (!isLoading && categoryList.isEmpty() && !isCreating) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { isCreating = true },
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
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (categoryList.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),

                        ) {
                        items(categoryList) { category ->
                            val isEditing = editingItemId.value == category.id
                            LaunchedEffect(isEditing) {
                                if (isEditing) {
                                    viewModel.onChangeCategoryName(category.name)
                                }
                            }

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
                                        onClick = {
                                            viewModel.updateCategory(category.id)
                                            editingItemId.value = null
                                            isEditMode = false
                                        },
                                        value = categoryName,
                                        onValueChange = { newName -> viewModel.onChangeCategoryName(newName) }
                                    )
                                } else {
                                    SwipeableActionsBox(
                                        startActions = listOf(
                                            SwipeAction(
                                                icon = rememberVectorPainter(Icons.Default.Edit),
                                                background = MaterialTheme.colorScheme.primary,
                                                onSwipe = {
                                                    editingItemId.value = category.id
                                                    isEditMode = true
                                                }
                                            )
                                        ),
                                        endActions = listOf(
                                            SwipeAction(
                                                icon = rememberVectorPainter(Icons.Default.Delete),
                                                background = MaterialTheme.colorScheme.error,
                                                onSwipe = {
                                                    // handle delete
                                                }
                                            )
                                        )
                                    ) {
                                        MenuCard(
                                            text = category.name
                                        )
                                    }
                                }
                            }

                        }
                    }
                }
                if (isCreating) {
                    EditMenuCard(
                        onClick = {
                            viewModel.addCategory()
                            isCreating = false
                                  },
                        value = categoryName,
                        onValueChange = {newName -> viewModel.onChangeCategoryName(newName)}
                    )

                } else if(!isLoading && !isEditMode) {
                    IconButton(
                        onClick = { isCreating = true },
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .size(42.dp)
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

@Composable
fun MenuCard(
    text: String,
) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }


}


@Composable
fun EditMenuCard(
    onClick: () -> Unit,
    value: String,
    onValueChange:(String) -> Unit

){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        FoodAppTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.width(320.dp),
            placeholder = { Text("Nhập tên danh mục...") }
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                onClick.invoke()
            },
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.outline)
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
}



