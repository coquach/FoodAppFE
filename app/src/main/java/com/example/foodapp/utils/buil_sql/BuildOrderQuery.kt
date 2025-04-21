package com.example.foodapp.utils.buil_sql

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.foodapp.data.dto.filter.OrderFilter

object BuildOrderQuery {

    fun buildOrderQuery(filter: OrderFilter, isDelete: Boolean = false): SupportSQLiteQuery {
        val queryBuilder = StringBuilder()


        if (isDelete) {
            queryBuilder.append("DELETE FROM order_table WHERE 1=1")
        } else {
            queryBuilder.append("SELECT * FROM order_table WHERE 1=1")
        }

        val args = mutableListOf<Any?>()


        if (filter.status != null) {
            queryBuilder.append(" AND status = '${filter.status}'")
        }
        if (filter.paymentMethod != null) {
            queryBuilder.append(" AND paymentMethod = '${filter.paymentMethod}'")
        }
        if (filter.startDate != null) {
            queryBuilder.append(" AND startDate >= '${filter.startDate}'")
        }
        if (filter.endDate != null) {
            queryBuilder.append(" AND endDate <= '${filter.endDate}'")
        }
        if (filter.staffId != null) {
            queryBuilder.append(" AND staffId = ${filter.staffId}")
        }
        // Thêm điều kiện ORDER BY cho SELECT
        if (!isDelete) {
            queryBuilder.append(" ORDER BY orderDate DESC")
        }

        return SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
    }
}
