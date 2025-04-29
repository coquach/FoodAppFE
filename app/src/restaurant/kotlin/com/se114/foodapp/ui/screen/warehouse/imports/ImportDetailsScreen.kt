package com.se114.foodapp.ui.screen.warehouse.imports

import android.util.Log
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.ImportDetail
import com.example.foodapp.data.model.Ingredient
import com.example.foodapp.ui.screen.common.CheckoutRowItem
import com.example.foodapp.ui.screen.components.ComboBoxSample

import com.example.foodapp.ui.screen.components.ComboBoxSampleLazyPaging
import com.example.foodapp.ui.screen.components.DateRangePickerSample
import com.example.foodapp.ui.screen.components.FoodAppTextField
import com.example.foodapp.ui.screen.components.FoodItemCounter
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.theme.confirm
import com.example.foodapp.ui.theme.onConfirm
import com.example.foodapp.utils.StringUtils
import com.se114.foodapp.ui.screen.menu.category.EditMenuCard
import com.se114.foodapp.ui.screen.menu.category.MenuCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun ImportDetailsScreen(
    navController: NavController,
    viewModel: ImportDetailsViewModel = hiltViewModel(),
    isUpdating: Boolean = false,
    import: Import? = null,
) {
    LaunchedEffect(key1 = isUpdating) {
        delay(100)
        viewModel.setMode(isUpdating, import)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val suppliers = viewModel.suppliers.collectAsLazyPagingItems()
    val staffs = viewModel.staffs.collectAsLazyPagingItems()
    val ingredients by viewModel.ingredients.collectAsStateWithLifecycle()
    val importRequest by viewModel.importRequest.collectAsStateWithLifecycle()
    val importDetailsListRequest by viewModel.importDetailsListRequest.collectAsStateWithLifecycle()
    val importDetailsRequest by viewModel.importDetailsRequest.collectAsStateWithLifecycle()

    var isCreating by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var editingItemId by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                is ImportDetailsViewModel.ImportDetailsEvents.GoBack -> {
                    Log.d("Import goback", "Done")
                    if (isUpdating) {
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
                navController.navigateUp()
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
                    selected = import?.supplierName ?: "",
                    onPositionSelected = { name ->
                        val selectedSupplier = (0 until suppliers.itemCount)
                            .mapNotNull { index -> suppliers[index] }
                            .find { it.name == name }
                        val supplierId = selectedSupplier?.id
                        supplierId?.let {
                            viewModel.onChangeSupplerId(it)
                        }
                    },
                    options = suppliers,

                    labelExtractor = { supplier -> supplier.name }
                )
                ComboBoxSampleLazyPaging(
                    modifier = Modifier
                        .fillMaxWidth(),
                    title = "Nhân viên",
                    textPlaceholder = "Chọn nhân viên",
                    selected = import?.staffName ?: "",
                    onPositionSelected = { name ->
                        val selectedSupplier = (0 until suppliers.itemCount)
                            .mapNotNull { index -> suppliers[index] }
                            .find { it.name == name }
                        val supplierId = selectedSupplier?.id
                        supplierId?.let {
                            viewModel.onChangeSupplerId(it)
                        }
                    },
                    options = suppliers,
                    fieldHeight = 80.dp,
                    labelExtractor = { supplier -> supplier.name }
                )

            }
        }

        if (importDetailsListRequest.isEmpty() && !isCreating) {
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
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                           ,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        state = listState

                        ) {
                        item{
                            if (isCreating) {
                                ImportDetailsEditCard(
                                    importDetail = importDetailsRequest,
                                    ingredients = ingredients,
                                    onIngredientChange = { name ->
                                        val selectedIngredient = ingredients.find { it.name == name }
                                        selectedIngredient?.let {
                                            viewModel.onChangeIngredient(selectedIngredient)
                                        }
                                    },
                                    onCostChange = {
                                        viewModel.onChangeCost(it.toBigDecimal())
                                    },
                                    onChangeProductionDate = {
                                        viewModel.onChangeProductionDate(it)
                                    },
                                    onChangeExpiryDate = {
                                        viewModel.onChangeExpiryDate(it)
                                    },
                                    onIncrement = {
                                        viewModel.onChangeQuantity(
                                            importDetailsRequest.quantity + BigDecimal.ONE
                                        )
                                    },
                                    onDecrement = {
                                        viewModel.onChangeQuantity(
                                            if (importDetailsRequest.quantity > BigDecimal.ONE)
                                                importDetailsRequest.quantity - BigDecimal.ONE
                                            else importDetailsRequest.quantity
                                        )
                                    },
                                    onUpdate = {
                                        viewModel.addImportDetails()
                                        isCreating = false
                                    },
                                    onClose = { isCreating = false },
                                )
                            }
                        }
                        items(importDetailsListRequest) { importDetails ->
                            val isEditing = editingItemId == importDetails.localId

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
                                        importDetail = importDetailsRequest,
                                        ingredients = ingredients,
                                        onIngredientChange = { name ->
                                            val selectedIngredient =
                                                ingredients.find { it.name == name }
                                            selectedIngredient?.let {
                                                viewModel.onChangeIngredient(selectedIngredient)
                                            }
                                        },
                                        onCostChange = {
                                            viewModel.onChangeCost(it.toBigDecimal())
                                        },
                                        onChangeProductionDate = {
                                            viewModel.onChangeProductionDate(it)
                                        },
                                        onChangeExpiryDate = {
                                            viewModel.onChangeExpiryDate(it)
                                        },
                                        onIncrement = {
                                            viewModel.onChangeQuantity(
                                                importDetailsRequest.quantity.add(
                                                    BigDecimal(1)
                                                )
                                            )
                                        },
                                        onDecrement = {
                                            viewModel.onChangeQuantity(
                                                if (importDetailsRequest.quantity > BigDecimal.ONE)
                                                    importDetailsRequest.quantity.subtract(
                                                        BigDecimal(1)
                                                    ) else importDetailsRequest.quantity
                                            )
                                        },
                                        onUpdate = {
                                            viewModel.updateImportDetails(importDetails.localId)
                                            editingItemId = null
                                            isEditMode = false
                                        },
                                        onClose = {
                                            editingItemId = null
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
                                                    editingItemId = importDetails.localId
                                                    viewModel.loadImportDetails(importDetails)
                                                    isEditMode = true
                                                }
                                            )
                                        ),
                                        endActions = listOf(
                                            SwipeAction(
                                                icon = rememberVectorPainter(Icons.Default.Delete),
                                                background = MaterialTheme.colorScheme.error,
                                                onSwipe = {
                                                    viewModel.deleteImportDetails(importDetails.localId)
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
                             if (!isCreating &&!isEditMode) {
                                IconButton(
                                    onClick = { isCreating = true
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(0)
                                        }},
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

        Column(

        ) {
            CheckoutRowItem(
                title = "Tổng cộng",
                value = BigDecimal(0),
                fontWeight = FontWeight.Bold
            )
            LoadingButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                text = "Tạo",
                loading = false,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

}

@Composable
fun ImportDetailsCard(importDetail: ImportDetailUIModel) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
        ,
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


                ImportDetailsTextRow(
                    text = "Nguyên liệu: ${importDetail.ingredient?.name}",
                    icon = Icons.Default.Inventory,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
                ImportDetailsTextRow(
                    text = "NSX: ${StringUtils.formatDateTime(importDetail.productionDate)}",
                    icon = Icons.Default.DateRange,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )

                ImportDetailsTextRow(
                    text = "NSX: ${StringUtils.formatDateTime(importDetail.expiryDate)}",
                    icon = Icons.Default.DateRange,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
                ImportDetailsTextRow(
                    text = "Số lượng: ${importDetail.quantity}",
                    icon = Icons.Default.Tag,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
                ImportDetailsTextRow(
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


@Composable
fun ImportDetailsTextRow(
    text: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.outline,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

