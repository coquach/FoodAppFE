package com.se114.foodapp.ui.screen.warehouse.material

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.data.model.Ingredient
import com.example.foodapp.ui.screen.components.ChipsGroupWrap
import com.example.foodapp.ui.screen.components.ComboBoxSample
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppDialog
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Nothing
import com.se114.foodapp.ui.screen.supplier.SupplierState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialScreen(
    navController: NavController,
    viewModel: MaterialViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isEditUnitMode by remember { mutableStateOf(false) }
    var isEditActiveUnit by remember { mutableStateOf(false) }
    var isEditIngredientMode by remember { mutableStateOf(false) }
    var isEditActiveIngredient by remember { mutableStateOf(false) }
    var showErrorSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                MaterialState.Event.ShowError -> {
                    showErrorSheet = true
                }

                is MaterialState.Event.ShowSuccessToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

                MaterialState.Event.OnBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderDefaultView(
            onBack = {
                viewModel.onAction(MaterialState.Action.OnBack)
            },
            text = "Nguyên liệu"
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đơn vị tính",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                if (!isEditIngredientMode) {
                    AnimatedContent(
                        targetState = isEditUnitMode,
                        transitionSpec = {
                            (slideInVertically { it } + fadeIn()) togetherWith
                                    (slideOutVertically { -it } + fadeOut())
                        },
                        label = "Edit Unit Switch"
                    ) { isEdit ->
                        if (isEdit) {
                            EditUnitCard(
                                onModify = {
                                    if (uiState.editState.isUpdating)
                                        viewModel.onAction(MaterialState.Action.OnUpdateUnit)
                                    else viewModel.onAction(MaterialState.Action.OnAddUnit)
                                    isEditUnitMode = false
                                },
                                value = uiState.unitSelected.name,
                                onValueChange = { newName ->
                                    viewModel.onAction(MaterialState.Action.OnChangeUnitName(newName))
                                },
                                onHide = {
                                    showStatusDialog = true
                                },
                                onDelete = {
                                    showDeleteDialog = true
                                },
                                isActive = uiState.editState.isActive,
                                isUpdating = uiState.editState.isUpdating,
                            )
                        } else {
                            IconButton(
                                onClick = {
                                    viewModel.onAction(
                                        MaterialState.Action.OnEditState(
                                            isUpdating = false,
                                            isActive = true,
                                            isUnit = true
                                        )
                                    )
                                    viewModel.onAction(MaterialState.Action.OnUnitSelected(com.example.foodapp.data.model.Unit()))
                                    isEditUnitMode = true

                                },
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(color = MaterialTheme.colorScheme.outline)
                                    .padding(0.dp)

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Unit",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                }


            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Đang hiển thị",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium

                    )
                    if (uiState.activeUnits.isEmpty()) {
                        Nothing(
                            icon = Icons.Default.Info,
                            iconSize = 24.dp,
                            text = "Không có đơn vị tính nào"
                        )
                    } else {
                        ChipsGroupWrap(
                            options = uiState.activeUnits.map { it.name },
                            selectedOption = uiState.unitSelected.name,
                            onOptionSelected = { selectedName ->
                                val selectedUnit =
                                    uiState.activeUnits.find { it.name == selectedName }
                                if (!isEditIngredientMode&& isEditActiveIngredient && selectedUnit != null) {
                                    viewModel.onAction(
                                        MaterialState.Action.OnUnitSelected(
                                            selectedUnit
                                        )
                                    )
                                    viewModel.onAction(
                                        MaterialState.Action.OnEditState(
                                            true,
                                            true,
                                            true
                                        )
                                    )

                                    isEditUnitMode = true
                                    isEditActiveUnit = true
                                }


                            },

                            thresholdExpend = 10,
                            containerColor = MaterialTheme.colorScheme.inversePrimary,
                            shouldSelectDefaultOption = false
                        )
                    }

                }

                VerticalDivider(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.outline
                )

                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Đã ẩn",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium

                    )
                    if (uiState.hiddenUnits.isEmpty()) {
                        Nothing(
                            icon = Icons.Default.Info,
                            iconSize = 24.dp,
                            text = "Không có đơn vị tính nào"
                        )
                    } else {
                        ChipsGroupWrap(
                            options = uiState.hiddenUnits.map { it.name },
                            selectedOption = uiState.unitSelected.name,
                            onOptionSelected = { selectedName ->
                                val selectedUnit =
                                    uiState.hiddenUnits.find { it.name == selectedName }
                                if (!isEditIngredientMode && !isEditActiveUnit && selectedUnit != null) {
                                    viewModel.onAction(
                                        MaterialState.Action.OnUnitSelected(
                                            selectedUnit
                                        )
                                    )
                                    viewModel.onAction(
                                        MaterialState.Action.OnEditState(
                                            true,
                                            false,
                                            true
                                        )
                                    )
                                    isEditUnitMode = true
                                    isEditActiveUnit = false

                                }

                            },
                            thresholdExpend = 10,
                            containerColor = MaterialTheme.colorScheme.secondary,
                            shouldSelectDefaultOption = false
                        )
                    }

                }
            }
        }
        Spacer(modifier = Modifier.size(100.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nguyên liệu",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                if (!isEditUnitMode) {
                    AnimatedContent(
                        targetState = isEditIngredientMode,
                        transitionSpec = {
                            (slideInVertically { it } + fadeIn()) togetherWith
                                    (slideOutVertically { -it } + fadeOut())
                        },
                        label = "Edit Ingredient Switch"
                    ) { isEdit ->
                        if (isEdit) {
                            EditIngredient(
                                onModify = {
                                    if (uiState.editState.isUpdating)
                                        viewModel.onAction(MaterialState.Action.OnUpdateIngredient)
                                    else viewModel.onAction(MaterialState.Action.OnAddIngredient)
                                    isEditIngredientMode = false
                                },
                                ingredient = uiState.ingredientSelected,
                                onValueChange = {
                                    viewModel.onAction(
                                        MaterialState.Action.OnChangeIngredientName(
                                            it
                                        )
                                    )
                                },

                                onPositionSelected = { selectedName ->
                                    val selectedUnit =
                                        uiState.activeUnits.find { it.name == selectedName }
                                    selectedUnit?.id?.let {
                                        viewModel.onAction(
                                            MaterialState.Action.OnChangeIngredientUnitId(
                                                it
                                            )
                                        )
                                    }
                                },
                                activeUnits = uiState.activeUnits,
                                onHide = {

                                    showStatusDialog = true
                                },
                                onDelete = {
                                    showDeleteDialog = true
                                },
                                isActive = uiState.editState.isActive,
                                isUpdating = uiState.editState.isUpdating,
                            )
                        } else {
                            IconButton(
                                onClick = {
                                    viewModel.onAction(
                                        MaterialState.Action.OnEditState(
                                            false,
                                            true,
                                            false
                                        )
                                    )
                                    viewModel.onAction(
                                        MaterialState.Action.OnIngredientSelected(
                                            Ingredient()
                                        )
                                    )

                                    isEditIngredientMode = true
                                },
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(color = MaterialTheme.colorScheme.outline)
                                    .padding(0.dp)

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Unit",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }


            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Đang hiển thị",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium

                    )
                    if (uiState.activeIngredients.isEmpty()) {
                        Nothing(
                            icon = Icons.Default.Info,
                            iconSize = 24.dp,
                            text = "Không có nguyên liệu nào"
                        )
                    } else {
                        ChipsGroupWrap(
                            options = uiState.activeIngredients.map { it.name },
                            selectedOption = uiState.ingredientSelected.name,
                            onOptionSelected = { selectedName ->
                                val selectedIngredient =
                                    uiState.activeIngredients.find { it.name == selectedName }
                                if (!isEditUnitMode && isEditActiveIngredient && selectedIngredient != null) {
                                    viewModel.onAction(
                                        MaterialState.Action.OnIngredientSelected(
                                            selectedIngredient
                                        )
                                    )
                                    viewModel.onAction(
                                        MaterialState.Action.OnEditState(
                                            true,
                                            true,
                                            false
                                        )
                                    )
                                    isEditIngredientMode = true
                                    isEditActiveIngredient = true

                                }


                            },

                            thresholdExpend = 10,
                            containerColor = MaterialTheme.colorScheme.inversePrimary,
                            shouldSelectDefaultOption = false
                        )
                    }

                }

                VerticalDivider(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.outline
                )

                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Đã ẩn",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium

                    )
                    if (uiState.hiddenIngredients.isEmpty()) {
                        Nothing(
                            icon = Icons.Default.Info,
                            iconSize = 24.dp,
                            text = "Không có nguyên liệu nào"
                        )
                    } else {
                        ChipsGroupWrap(
                            options = uiState.hiddenIngredients.map { it.name },
                            selectedOption = uiState.ingredientSelected.name,
                            onOptionSelected = { selectedName ->
                                val selectedIngredient =
                                    uiState.hiddenIngredients.find { it.name == selectedName }
                                if (!isEditUnitMode && !isEditActiveIngredient && selectedIngredient != null) {
                                    viewModel.onAction(
                                        MaterialState.Action.OnIngredientSelected(
                                            selectedIngredient
                                        )
                                    )
                                    viewModel.onAction(
                                        MaterialState.Action.OnEditState(
                                            true,
                                            false,
                                            false
                                        )
                                    )
                                    isEditIngredientMode = true
                                    isEditActiveIngredient = false

                                }


                            },

                            thresholdExpend = 10,
                            containerColor = MaterialTheme.colorScheme.inversePrimary,
                            shouldSelectDefaultOption = false
                        )
                    }

                }
            }
        }
    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error.toString(),
            onDismiss = {
                showErrorSheet = false
            },
        )
    }
    if (showDeleteDialog) {
        FoodAppDialog(
            title = "Xóa ${if (uiState.editState.isUnit == true) "đơn vị" else "nguyên liệu"}",
            message = "Bạn có chắc chắn muốn xóa ${if (uiState.editState.isUnit == true) "đơn vị" else "nguyên liệu"} này không?",
            onDismiss = {

                showDeleteDialog = false
            },
            onConfirm = {

                if(uiState.editState.isUnit) {
                    viewModel.onAction(MaterialState.Action.DeleteUnit)
                } else {
                    viewModel.onAction(MaterialState.Action.DeleteIngredient)
                }
                isEditIngredientMode = false
                isEditUnitMode = false
                showDeleteDialog = false

            },
            confirmText = "Xóa",
            dismissText = "Đóng",
            showConfirmButton = true
        )
    }
    if (showStatusDialog) {
        FoodAppDialog(
            title = if (uiState.editState.isActive) "Hiện ${if (uiState.editState.isUnit == true) "đơn vị" else "nguyên liệu"}" else "Ẩn ${if (uiState.editState.isUnit == true) "đơn vị" else "nguyên liệu"}",
            message = if (uiState.editState.isActive) "Bạn có chắc chắn muốn hiện ${if (uiState.editState.isUnit == true) "đơn vị" else "nguyên liệu"} này không?" else "Bạn có chắc chắn muốn ẩn ${if (uiState.editState.isUnit == true) "đơn vị" else "nguyên liệu"} này không?",
            onDismiss = {

                showStatusDialog = false
            },
            onConfirm = {

                if(uiState.editState.isUnit) {
                    if (uiState.editState.isActive) {
                        viewModel.onAction(MaterialState.Action.OnRecoverUnit(false))
                    } else {
                        viewModel.onAction(MaterialState.Action.OnRecoverUnit(true))
                    }
                } else {
                    if (uiState.editState.isActive) {
                        viewModel.onAction(MaterialState.Action.SetActiveIngredient(false))
                    } else {
                        viewModel.onAction(MaterialState.Action.SetActiveIngredient(true))
                    }
                }
                isEditIngredientMode = false
                isEditUnitMode = false
                showStatusDialog = false

            },
            confirmText = if (uiState.editState.isActive) "Ẩn" else "Hiện",
            dismissText = "Đóng",
            showConfirmButton = true
        )
    }
}

@Composable
fun EditUnitCard(
    modifier: Modifier = Modifier,
    onModify: () -> Unit,
    onHide: () -> Unit,
    onDelete: () -> Unit,
    isActive: Boolean = true,
    isUpdating: Boolean = false,
    value: String,
    onValueChange: (String) -> Unit,


    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,

        ) {
        FoodAppTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .width(120.dp)
                .height(70.dp),
            fieldHeight = 70.dp
        )
        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = {
                onModify.invoke()
            },
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(0.dp),


            ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Confirm",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(24.dp)
            )
        }
        if (isUpdating) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    onHide.invoke()
                },
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.outline)
                    .padding(0.dp),


                ) {
                Icon(
                    imageVector = if (isActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle Status",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    onDelete.invoke()
                },
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.error)
                    .padding(0.dp),


                ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }

    }
}

@Composable
fun EditIngredient(

    modifier: Modifier = Modifier,
    onModify: () -> Unit,
    onHide: () -> Unit,
    onDelete: () -> Unit,
    isActive: Boolean = true,
    isUpdating: Boolean = false,
    ingredient: Ingredient,
    onValueChange: (String) -> Unit,

    onPositionSelected: (String?) -> Unit,
    activeUnits: List<com.example.foodapp.data.model.Unit>,


    ) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FoodAppTextField(
                value = ingredient.name,
                onValueChange = onValueChange,
                modifier = Modifier
                    .width(120.dp)
                    .height(70.dp),
                fieldHeight = 70.dp
            )

            ComboBoxSample(
                modifier = Modifier
                    .width(90.dp)
                    .height(70.dp),
                textPlaceholder = "...",
                selected = activeUnits.find { it.id == ingredient.unitId }?.name,
                onPositionSelected = onPositionSelected,
                options = activeUnits.map { it.name },
                fieldHeight = 70.dp
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        ) {
            IconButton(
                onClick = {
                    onModify.invoke()
                },
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.primary)
                    .padding(0.dp),


                ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
            if (isUpdating) {
                IconButton(
                    onClick = {
                        onHide.invoke()
                    },
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.outline)
                        .padding(0.dp),


                    ) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Status",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
                IconButton(
                    onClick = {
                        onDelete.invoke()
                    },
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.error)
                        .padding(0.dp),


                    ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onError,
                        modifier = Modifier
                            .size(24.dp)
                    )
                }
            }

        }
    }


}

