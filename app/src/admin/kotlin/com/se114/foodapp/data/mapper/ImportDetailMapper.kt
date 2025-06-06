package com.se114.foodapp.data.mapper

import com.example.foodapp.data.model.ImportDetail
import com.se114.foodapp.ui.screen.warehouse.imports.ImportDetailUIModel


fun ImportDetail.toImportDetailUiModel() = ImportDetailUIModel(
    id = this.id,
    ingredient = this.ingredient,
    expiryDate = this.expiryDate,
    productionDate = this.productionDate,
    quantity = this.quantity,
    cost = this.cost,

    )