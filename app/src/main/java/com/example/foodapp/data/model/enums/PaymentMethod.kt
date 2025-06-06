package com.example.foodapp.data.model.enums

enum class PaymentMethod(val display: String) {
    CASH("Tiền mặt"),
    BANK("Chuyển khoản");
    fun getDisplayName(): String = display

    override fun toString(): String = name

    companion object {
        fun fromDisplay(display: String): PaymentMethod? {
            return PaymentMethod.entries.firstOrNull { it.display == display }
        }
        fun fromName(name: String): PaymentMethod? {
            return PaymentMethod.entries.firstOrNull { it.name == name }
        }
    }
}