package com.example.foodapp.data.model.enums

enum class ServingType(val display: String) {
    TAKEAWAY("Mang về"), ONLINE("Trực tuyến"), INSTORE("Tại chỗ");


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