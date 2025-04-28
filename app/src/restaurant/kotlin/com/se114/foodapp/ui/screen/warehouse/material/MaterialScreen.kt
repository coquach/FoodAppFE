package com.se114.foodapp.ui.screen.warehouse.material

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.ui.screen.components.ChipsGroupWrap
import com.example.foodapp.ui.screen.components.ComboBoxSample
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Nothing
import com.example.foodapp.ui.screen.components.RadioGroupWrap
import com.se114.foodapp.ui.screen.menu.category.EditMenuCard

@Composable
fun MaterialScreen(
    navController: NavController,
    viewModel: MaterialViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unitName by viewModel.unitName.collectAsStateWithLifecycle()
    val ingredientRequest by viewModel.ingredientRequest.collectAsStateWithLifecycle()
    val activeUnits by viewModel.activeUnits.collectAsStateWithLifecycle()
    val hiddenUnits by viewModel.hiddenUnits.collectAsStateWithLifecycle()
    val activeIngredients by viewModel.activeIngredients.collectAsStateWithLifecycle()
    val hiddenIngredients by viewModel.hiddenIngredients.collectAsStateWithLifecycle()


    var isLoading by remember { mutableStateOf(false) }

    var editingUnitId by remember { mutableStateOf<Long?>(null) }
    var selectedUnitChip by remember { mutableStateOf<String?>(null) }
    var editingIngredientId by remember { mutableStateOf<Long?>(null) }
    var selectedIngredientChip by remember { mutableStateOf<String?>(null) }


    var isUpdating by remember { mutableStateOf(false) }


    var isEditUnitMode by remember { mutableStateOf(false) }
    var isEditIngredientMode by remember { mutableStateOf(false) }
    var showErrorSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderDefaultView(
            onBack = {
                navController.navigateUp()
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
                                onClick = {
                                    if (isUpdating)
                                        viewModel.updateUnit(editingUnitId!!)
                                    else viewModel.addUnit()
                                    isEditUnitMode = false
                                    selectedUnitChip = null
                                },
                                value = unitName,
                                onValueChange = { newName ->
                                    viewModel.onChangeUnitName(newName)
                                },

                                )
                        } else {
                            IconButton(
                                onClick = {
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
                    if (activeUnits.isEmpty()) {
                        Nothing(
                            icon = Icons.Default.Info,
                            iconSize = 24.dp,
                            text = "Không có đơn vị tính nào"
                        )
                    } else {
                        ChipsGroupWrap(
                            options = activeUnits.map { it.name },
                            selectedOption = selectedUnitChip,
                            onOptionSelected = { selectedName ->
                                val selectedUnit = activeUnits.find { it.name == selectedName }
                                if( !isEditIngredientMode && selectedUnit!=null) {
                                    viewModel.onChangeUnitName(selectedUnit.name)
                                    selectedUnitChip = selectedName
                                    editingUnitId = selectedUnit.id
                                    isEditUnitMode = true
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
                    if (hiddenUnits.isEmpty()) {
                        Nothing(
                            icon = Icons.Default.Info,
                            iconSize = 24.dp,
                            text = "Không có đơn vị tính nào"
                        )
                    } else {
                        ChipsGroupWrap(
                            options = hiddenUnits.map { it.name },
                            selectedOption = selectedUnitChip,
                            onOptionSelected = { selectedName ->
                                val selectedUnit = hiddenUnits.find { it.name == selectedName }
                                if( !isEditIngredientMode && selectedUnit!=null) {
                                    viewModel.onChangeUnitName(selectedUnit.name)
                                    selectedUnitChip = selectedName
                                    editingUnitId = selectedUnit.id
                                    isEditUnitMode = true
                                }

                            },
                            thresholdExpend = hiddenUnits.size,
                            containerColor = MaterialTheme.colorScheme.secondary
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
                                onClick = {
                                    if (isUpdating)
                                        viewModel.updateIngredient(editingIngredientId!!)
                                    else viewModel.addIngredient()
                                    isEditIngredientMode = false
                                    selectedIngredientChip = null
                                },
                                value = ingredientRequest.name,
                                onValueChange = {
                                    viewModel.onChangeIngredientName(it)
                                },
                                selected = activeUnits.find { it.id == ingredientRequest.unitId }?.name,
                                onPositionSelected = { selectedName ->
                                    val selectedUnit = activeUnits.find { it.name == selectedName }
                                    selectedUnit?.let {
                                        viewModel.onChangeIngredientId(it.id)
                                    }
                                },
                                options = activeUnits.map { it.name },

                            )
                        } else {
                            IconButton(
                                onClick = { isEditIngredientMode = true },
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
                    if (activeIngredients.isEmpty()) {
                        Nothing(
                            icon = Icons.Default.Info,
                            iconSize = 24.dp,
                            text = "Không có nguyên liệu nào"
                        )
                    } else {
                        ChipsGroupWrap(
                            options = activeIngredients.map { it.name },
                            selectedOption = selectedIngredientChip,
                            onOptionSelected = { selectedName ->
                                val selectedIngredient =
                                    activeIngredients.find { it.name == selectedName }
                                if( !isEditUnitMode && selectedIngredient!=null) {
                                    viewModel.loadIngredient(selectedIngredient)
                                    selectedIngredientChip = selectedName
                                    editingIngredientId = selectedIngredient.id
                                    isEditIngredientMode = true
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
                    if (hiddenIngredients.isEmpty()) {
                        Nothing(
                            icon = Icons.Default.Info,
                            iconSize = 24.dp,
                            text = "Không có nguyên liệu nào"
                        )
                    } else {
                        ChipsGroupWrap(
                            options = hiddenIngredients.map { it.name },
                            selectedOption = selectedIngredientChip,
                            onOptionSelected = { selectedName ->
                                val selectedIngredient =
                                    hiddenIngredients.find { it.name == selectedName }
                                if( !isEditUnitMode && selectedIngredient!=null) {
                                    viewModel.loadIngredient(selectedIngredient)
                                    selectedIngredientChip = selectedName
                                    editingIngredientId = selectedIngredient.id
                                    isEditIngredientMode = true
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
        ComboBoxSample(
            modifier = Modifier.width(80.dp),
            title = "",
            textPlaceholder = "test",
            selected = "",
            onPositionSelected = {  },
            options = activeUnits.map { it.name },
            fieldHeight = 56.dp
        )
    }
}

@Composable
fun EditUnitCard(
    onClick: () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,

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
                onClick.invoke()
            },
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.outline)
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
    }
}

@Composable
fun EditIngredient(

    onClick: () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    selected: String?,
    onPositionSelected: (String?) -> Unit,
    options: List<String>,


    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FoodAppTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .width(120.dp)
                .height(70.dp),
            fieldHeight = 70.dp
        )

        ComboBoxSample(
            modifier = Modifier.width(90.dp).height(70.dp),
            textPlaceholder = "...",
            selected = selected,
            onPositionSelected = onPositionSelected,
            options = options,
            fieldHeight = 70.dp
        )
        IconButton(
            onClick = {
                onClick.invoke()
            },
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.outline)
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
    }
}

