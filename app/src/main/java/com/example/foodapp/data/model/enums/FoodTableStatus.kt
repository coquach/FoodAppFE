package com.example.foodapp.data.model.enums

enum class FoodTableStatus(val display: String) {
    OCCUPIED("Đã đặt"),
    EMPTY("Trống");


    fun getDisplayName(): String = display

    override fun toString(): String = name

    companion object {
        fun fromDisplay(display: String): FoodTableStatus {
            return FoodTableStatus.entries.first { it.display == display }
        }
        fun fromName(name: String): FoodTableStatus {
            return FoodTableStatus.entries.first { it.name == name }
        }
    }
}