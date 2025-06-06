package com.se114.foodapp.data.dto.request

data class ExportRequest (
    val staffId: Long?=null,
    val exportDate: String,
    val exportDetails: List<ExportDetailRequest>
)