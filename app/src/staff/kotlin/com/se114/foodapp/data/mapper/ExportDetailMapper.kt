package com.se114.foodapp.data.mapper

import com.example.foodapp.data.model.ExportDetail
import com.se114.foodapp.ui.screen.export.export_detail.ExportDetailUIModel

fun ExportDetail.toExportDetailUiModel() = ExportDetailUIModel(
    id = this.id,
    inventoryId = this.inventoryId,
    ingredientName = this.ingredientName,
    expiryDate = this.expiryDate,
    cost = this.cost,
    quantity = this.quantity
)