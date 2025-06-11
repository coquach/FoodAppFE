package com.example.foodapp.data.dto.request

data class ImportRequest (
    val supplierId: Long?=null,
    val importDate: String,
    val importDetails: List<ImportDetailRequest>
)