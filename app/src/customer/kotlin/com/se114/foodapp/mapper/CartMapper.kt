package com.se114.foodapp.mapper

import com.example.foodapp.data.model.CartItem

import com.se114.foodapp.data.entities.CartItemEntity

object CartMapper {
    fun CartItemEntity.toCartItem() = CartItem(
        id = id,
        name = name,
        quantity = quantity,
        menuId = menuId,
        menuName = menuName,
        imageUrl = imageUrl,
        price = price
    )

    fun CartItem.toEntity() = CartItemEntity(
        id = this.id,
        quantity = this.quantity,
        menuId = this.menuId?: 1,
        name = this.name,
        menuName = this.menuName,
        price = this.price,
        imageUrl = this.imageUrl
    )
}
