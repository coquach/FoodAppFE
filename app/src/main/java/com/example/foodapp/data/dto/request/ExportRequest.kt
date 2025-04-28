package com.example.foodapp.data.dto.request

import java.time.LocalDateTime

data class ExportRequest (
    val staffId: Long,
    val exportDate: String,
    val exportDetails: List<ExportDetailRequest>
)