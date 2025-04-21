package com.example.foodapp.data.model.enums

enum class PaymentMethod(val display: String) {
    CASH("Tiền mặt"),
    BANK("Online");
    override fun toString(): String = display

    companion object {
        fun fromDisplay(display: String): PaymentMethod? {
            return PaymentMethod.entries.firstOrNull { it.display == display }
        }
        fun fromName(name: String): PaymentMethod? {
            return PaymentMethod.entries.firstOrNull { it.name == name }
        }
    }
}