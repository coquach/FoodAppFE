package com.example.foodapp.data.model.enums

enum class Gender(val display: String) {
    MALE("Nam"),
    FEMALE("Nữ");


    override fun toString(): String = display

    companion object {
        fun fromDisplay(display: String): Gender? {
            return entries.firstOrNull { it.display == display }
        }
        fun fromName(name: String): Gender? {
            return entries.firstOrNull { it.name == name }
        }
    }
}