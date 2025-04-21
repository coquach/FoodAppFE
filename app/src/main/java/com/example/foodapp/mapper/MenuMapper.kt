package com.example.foodapp.mapper

import com.example.foodapp.data.entities.MenuEntity
import com.example.foodapp.data.entities.MenuItemEntity
import com.example.foodapp.data.model.Menu
import com.example.foodapp.data.model.MenuItem

object MenuMapper {
    fun MenuEntity.toMenu(): Menu = Menu(
        id = id,
        createdAt = createdAt,
        name = name,
        isDeleted = isDeleted
    )

    fun Menu.toEntity(): MenuEntity = MenuEntity(
        id = id,
        createdAt = createdAt,
        name = name,
        isDeleted = isDeleted
    )
    fun MenuItemEntity.toMenuItem(): MenuItem = MenuItem(
        id = id,
        createdAt = createdAt,
        description = description,
        menuName = menuName,
        imageUrl = imageUrl,
        name = name,
        price = price
    )

    fun MenuItem.toEntity(): MenuItemEntity = MenuItemEntity(
        id = id,
        createdAt = createdAt,
        description = description,
        menuName = menuName,
        imageUrl = imageUrl,
        name = name,
        price = price
    )
}