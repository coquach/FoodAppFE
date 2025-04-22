package com.example.foodapp.utils.buil_sql

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.foodapp.data.dto.filter.MenuItemFilter
import com.example.foodapp.data.dto.filter.OrderFilter

object BuildMenuItemQuery {

    fun buildMenuItemQuery(filter: MenuItemFilter, isDelete: Boolean = false): SupportSQLiteQuery {
        val queryBuilder = StringBuilder()


        if (isDelete) {
            queryBuilder.append("DELETE FROM menu_item_table WHERE 1=1")
        } else {
            queryBuilder.append("SELECT * FROM menu_item_table WHERE 1=1")
        }

        val args = mutableListOf<Any?>()


        if (filter.id != null) {
            queryBuilder.append(" AND id = ?")
            args.add(filter.id)
        }
        if (filter.isAvailable != null) {
            queryBuilder.append(" AND isAvailable = ?")
            args.add(if (filter.isAvailable) 1 else 0)
        }

        if (!isDelete) {
            queryBuilder.append(" ORDER BY name DESC")
        }

        return SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
    }
}
