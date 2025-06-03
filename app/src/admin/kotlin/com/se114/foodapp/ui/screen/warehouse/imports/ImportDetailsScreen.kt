package com.se114.foodapp.ui.screen.warehouse.imports

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.Ingredient
import com.example.foodapp.ui.screen.common.CheckoutRowItem
import com.example.foodapp.ui.screen.components.ComboBoxSample
import com.example.foodapp.ui.screen.components.ComboBoxSampleLazyPaging
import com.example.foodapp.ui.screen.components.DateRangePickerSample
import com.example.foodapp.ui.screen.components.DetailsTextRow
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.FoodItemCounter
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.theme.confirm
import com.example.foodapp.ui.theme.onConfirm
import com.example.foodapp.utils.StringUtils
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportDetailsScreen(
    navController: NavController,
    viewModel: ImportDetailsViewModel = hiltViewModel(),
) {


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val suppliers = viewModel.suppliers.collectAsLazyPagingItems()
    val staffs = viewModel.staffs.collectAsLazyPagingItems()


    val totalCost by rememberSaveable {
        derivedStateOf {
            uiState.importDetails.sumOf { it.quantity * it.cost }
        }
    }

    var isCreating by rememberSaveable { mutableStateOf(false) }
    var isEditMode by rememberSaveable { mutableStateOf(false) }

    
    var showErrorSheet by rememberSaveable { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                is ImportDetailsState.Event.BackToAfterModify -> {
                    Log.d("Import goback", "Done")
                    if (uiState.isUpdating) {
                        Toast.makeText(
                            navController.context,
                            "Cập nhật phiếu nhập thành công",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            navController.context,
                            "Tạo món ăn thành công",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    navController.previousBackStackEntry?.savedStateHandle?.set("updated", true)
                    navController.popBackStack()

                }

                is ImportDetailsState.Event.OnBack -> {
                    navController.popBackStack()
                }

                ImportDetailsState.Event.ShowError -> {
                    showErrorSheet = true
                }
                is ImportDetailsState.Event.NotifyCantEdit -> {
                    Toast.makeText(
                        navController.context,
                        "Không thể sửa phiếu nhập này vì đã quá hạn 3 ngày",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderDefaultView(
            onBack = {
                viewModel.onAction(ImportDetailsState.Action.OnBack)
            },
            text = "Thông tin đơn nhập"
        )
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)

            ) {


                ComboBoxSampleLazyPaging(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = "Nhà cung cấp",
                    textPlaceholder = "Chọn nhà cung cấp",
                    selected = uiState.import.supplierName,
                    onPositionSelected = { name ->
                        val selectedSupplier = (0 until suppliers.itemCount)
                            .mapNotNull { index -> suppliers[index] }
                            .find { it.name == name }
                        val supplierId = selectedSupplier?.id
                        supplierId?.let {
                            viewModel.onAction(ImportDetailsState.Action.OnChangeSupplierId(it))
                        }
                    },
                    options = suppliers,
                    enabled = uiState.isEditable,
                    labelExtractor = { supplier -> supplier.name }
                )
                ComboBoxSampleLazyPaging(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = "Nhân viên",
                    textPlaceholder = "Chọn nhân viên",
                    selected = uiState.import.staffName,
                    onPositionSelected = { name ->
                        val selectedSupplier = (0 until suppliers.itemCount)
                            .mapNotNull { index -> staffs[index] }
                            .find { it.fullName == name }
                        val supplierId = selectedSupplier?.id
                        supplierId?.let {
                            viewModel.onAction(ImportDetailsState.Action.OnChangeStaffId(it))
                        }
                    },
                    options = suppliers,
                    fieldHeight = 80.dp,
                    labelExtractor = { supplier -> supplier.name },
                    enabled = uiState.isEditable,
                )

            }
        }

        if (uiState.importDetails.isEmpty() && !isCreating) {
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
                        text = "Thêm chi tiết phiếu",
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    state = listState

                ) {
                    item {
                        if (isCreating) {
                            ImportDetailsEditCard(
                                importDetail = uiState.importDetailsSelected,
                                ingredients = uiState.ingredients,
                                onIngredientChange = { name ->
                                    val selectedIngredient =
                                        uiState.ingredients.find { it.name == name }
                                    selectedIngredient?.let {
                                        viewModel.onAction(
                                            ImportDetailsState.Action.OnChangeIngredient(
                                                it
                                            )
                                        )
                                    }
                                },
                                onCostChange = {
                                    viewModel.onAction(ImportDetailsState.Action.OnChangeCost(it.toBigDecimalOrNull()))
                                },
                                onChangeProductionDate = {
                                    viewModel.onAction(
                                        ImportDetailsState.Action.OnChangeProductionDate(
                                            it
                                        )
                                    )
                                },
                                onChangeExpiryDate = {
                                    viewModel.onAction(
                                        ImportDetailsState.Action.OnChangeExpiryDate(
                                            it
                                        )
                                    )
                                },
                                onIncrement = {
                                    viewModel.onAction(
                                        ImportDetailsState.Action.OnChangeQuantity(
                                            uiState.importDetailsSelected.quantity + BigDecimal.ONE
                                        )
                                    )
                                },
                                onDecrement = {
                                    viewModel.onAction(
                                        ImportDetailsState.Action.OnChangeQuantity(
                                            uiState.importDetailsSelected.quantity - BigDecimal.ONE
                                        )
                                    )
                                },
                                onUpdate = {
                                    viewModel.onAction(ImportDetailsState.Action.AddImportDetails)
                                    isCreating = false
                                },
                                onClose = { isCreating = false },
                            )
                        }
                    }
                    items(uiState.importDetails) { importDetails ->
                        val isEditing =
                            uiState.importDetailsSelected.localId == importDetails.localId

                        AnimatedContent(
                            targetState = isEditing,
                            transitionSpec = {
                                (slideInVertically { it } + fadeIn()) togetherWith
                                        (slideOutVertically { -it } + fadeOut())
                            },
                            label = "Edit Switch"
                        ) { editing ->
                            if (editing) {

                                ImportDetailsEditCard(
                                    importDetail = uiState.importDetailsSelected,
                                    ingredients = uiState.ingredients,
                                    onIngredientChange = { name ->
                                        val selectedIngredient =
                                            uiState.ingredients.find { it.name == name }
                                        selectedIngredient?.let {
                                            viewModel.onAction(
                                                ImportDetailsState.Action.OnChangeIngredient(
                                                    it
                                                )
                                            )
                                        }
                                    },
                                    onCostChange = {
                                        viewModel.onAction(ImportDetailsState.Action.OnChangeCost(it.toBigDecimalOrNull()))
                                    },
                                    onChangeProductionDate = {
                                        viewModel.onAction(
                                            ImportDetailsState.Action.OnChangeProductionDate(
                                                it
                                            )
                                        )
                                    },
                                    onChangeExpiryDate = {
                                        viewModel.onAction(
                                            ImportDetailsState.Action.OnChangeExpiryDate(
                                                it
                                            )
                                        )
                                    },
                                    onIncrement = {
                                        viewModel.onAction(
                                            ImportDetailsState.Action.OnChangeQuantity(
                                                uiState.importDetailsSelected.quantity + BigDecimal.ONE
                                            )
                                        )
                                    },
                                    onDecrement = {
                                        viewModel.onAction(
                                            ImportDetailsState.Action.OnChangeQuantity(
                                                uiState.importDetailsSelected.quantity - BigDecimal.ONE
                                            )
                                        )
                                    },
                                    onUpdate = {
                                        viewModel.onAction(ImportDetailsState.Action.UpdateImportDetails)
                                        viewModel.onAction(
                                            ImportDetailsState.Action.OnImportDetailsSelected(
                                                ImportDetailUIModel()
                                            )
                                        )
                                        isEditMode = false
                                    },
                                    onClose = {
                                        viewModel.onAction(
                                            ImportDetailsState.Action.OnImportDetailsSelected(
                                                ImportDetailUIModel()
                                            )
                                        )
                                        isEditMode = false
                                    },
                                )
                            } else {
                                SwipeableActionsBox(
                                    modifier = Modifier
                                        .padding(
                                            vertical = 8.dp,
                                        )
                                        .clip(RoundedCornerShape(16.dp)),
                                    startActions = listOf(
                                        SwipeAction(
                                            icon = rememberVectorPainter(Icons.Default.Edit),
                                            background = MaterialTheme.colorScheme.primary,
                                            onSwipe = {
                                                if (uiState.isEditable) {
                                                    viewModel.onAction(
                                                        ImportDetailsState.Action.OnImportDetailsSelected(
                                                            importDetails
                                                        )
                                                    )

                                                    isEditMode = true
                                                }

                                            }
                                        )
                                    ),
                                    endActions = listOf(
                                        SwipeAction(
                                            icon = rememberVectorPainter(Icons.Default.Delete),
                                            background = MaterialTheme.colorScheme.error,
                                            onSwipe = {
                                                if (uiState.isEditable) {
                                                    viewModel.onAction(
                                                        ImportDetailsState.Action.DeleteImportDetails(
                                                            importDetails.localId
                                                        )
                                                    )
                                                }

                                            }
                                        )
                                    )
                                ) {
                                    ImportDetailsCard(
                                        importDetail = importDetails
                                    )
                                }
                            }
                        }

                    }
                    item {
                        if (uiState.isEditable && !isCreating && !isEditMode) {
                            IconButton(
                                onClick = {
                                    isCreating = true
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                },
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outline)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add ImportDetails",
                                    modifier = Modifier.size(30.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }


            }
        }

        AnimatedVisibility(
            visible = uiState.isEditable && !isCreating && !isEditMode,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Column {
                CheckoutRowItem(
                    title = "Tổng cộng",
                    value = totalCost,
                    fontWeight = FontWeight.Bold
                )
                LoadingButton(
                    onClick = {
                        if (uiState.isUpdating) viewModel.onAction(ImportDetailsState.Action.UpdateImport)
                        else viewModel.onAction(ImportDetailsState.Action.CreateImport)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    text = if (uiState.isUpdating) "Cập nhật" else "Tạo",
                    loading = false,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
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

}

@Composable
fun ImportDetailsCard(importDetail: ImportDetailUIModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inversePrimary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)

        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "ImportDetails",
                tint = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier.size(50.dp)
            )
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {


                DetailsTextRow(
                    text = "Nguyên liệu: ${importDetail.ingredient?.name}",
                    icon = Icons.Default.Inventory,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
                DetailsTextRow(
                    text = "NSX: ${StringUtils.formatDateTime(importDetail.productionDate)}",
                    icon = Icons.Default.DateRange,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )

                DetailsTextRow(
                    text = "HSD: ${StringUtils.formatDateTime(importDetail.expiryDate)}",
                    icon = Icons.Default.DateRange,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
                DetailsTextRow(
                    text = "Số lượng: ${importDetail.quantity}",
                    icon = Icons.Default.Tag,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
                DetailsTextRow(
                    text = "Giá: ${StringUtils.formatCurrency(importDetail.cost)}",
                    icon = Icons.Default.MonetizationOn,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )


            }
        }

    }
}


@Composable
fun ImportDetailsEditCard(
    importDetail: ImportDetailUIModel,
    ingredients: List<Ingredient>,
    onIngredientChange: (String?) -> Unit,
    onCostChange: (String) -> Unit,
    onChangeProductionDate: (LocalDateTime) -> Unit,
    onChangeExpiryDate: (LocalDateTime) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onUpdate: () -> Unit,
    onClose: () -> Unit,

    ) {

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                ComboBoxSample(
                    modifier = Modifier
                        .width(120.dp),
                    title = "Nguyên liệu",
                    textPlaceholder = "Chọn nguyên liệu",
                    selected = importDetail.ingredient?.name ?: "",
                    onPositionSelected = {
                        onIngredientChange(it)
                    },
                    options = ingredients.map { it.name },

                    )

                DateRangePickerSample(
                    startDateText = "NSX",
                    endDateText = "HSD",
                    startDate = importDetail.productionDate.toLocalDate(),
                    endDate = importDetail.expiryDate.toLocalDate(),
                    modifier = Modifier
                        .width(160.dp),
                    onDateRangeSelected = { selectedStartDate, selectedEndDate ->
                        val currentTime = LocalTime.now()

                        val productionDateTime = selectedStartDate?.atTime(currentTime)
                        val expiryDateTime = selectedEndDate?.atTime(currentTime)
                        onChangeProductionDate(productionDateTime!!)
                        onChangeExpiryDate(expiryDateTime!!)
                    }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FoodAppTextField(
                        value = importDetail.cost.toPlainString(),
                        onValueChange = {
                            onCostChange(it)
                        },
                        labelText = "Giá",
                        modifier = Modifier
                            .width(180.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    FoodItemCounter(
                        onCounterIncrement = onIncrement,
                        onCounterDecrement = onDecrement,
                        count = importDetail.quantity.toInt()
                    )
                }


            }
        }
        Spacer(modifier = Modifier.size(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = onClose,
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Confirm",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(32.dp)
                )
            }
            Button(
                onClick = onUpdate,
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.confirm),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirm",
                    tint = MaterialTheme.colorScheme.onConfirm,
                    modifier = Modifier
                        .size(32.dp)
                )
            }
        }

    }

}



