package com.example.foodapp.data.models.response


data class Category (
    val createdAt: String,
    val id: String,
    val imageUrl: String,
    val name: String
)

data class CategoriesResponse (
    val `data`: List<Category>
)
