package com.se114.foodapp.mapper

import com.example.foodapp.data.model.CartItem
import com.example.foodapp.mapper.MenuMapper.toEntity
import com.example.foodapp.mapper.MenuMapper.toMenuItem
import com.se114.foodapp.data.entities.CartItemEntity

object CartMapper {
    fun CartItemEntity.toCartItem() = CartItem(
        id = id,
        name = name,
        quantity = quantity,
        menuName = menuName,
        imageUrl = imageUrl,
        price = price
    )

    fun CartItem.toEntity() = CartItemEntity(
        id = this.id,
        quantity = this.quantity,
        name = this.name,
        menuName = this.menuName,
        price = this.price,
        imageUrl = this.imageUrl
    )
}
