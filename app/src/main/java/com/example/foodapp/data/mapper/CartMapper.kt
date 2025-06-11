package com.example.foodapp.data.mapper

import com.example.foodapp.data.local.entities.CartItemEntity
import com.example.foodapp.data.model.CartItem

object CartMapper {
    fun CartItemEntity.toCartItem() = CartItem(
        id = id,
        name = name,
        quantity = quantity,
        imageUrl = imageUrl,
        price = price,
        remainingQuantity = remainingQuantity
    )

    fun CartItem.toEntity() = CartItemEntity(
        id = this.id,
        quantity = this.quantity,
        name = this.name,
        price = this.price,
        imageUrl = this.imageUrl,
        remainingQuantity = this.remainingQuantity
    )
}