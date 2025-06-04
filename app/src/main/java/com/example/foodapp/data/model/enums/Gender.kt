package com.example.foodapp.data.model.enums

enum class Gender(val display: String) {
    MALE("Nam"),
    FEMALE("Nữ");


    fun getDisplayName(): String = display

    override fun toString(): String = name

    companion object {
        fun fromDisplay(display: String): Gender {
            return entries.first { it.display == display }
        }
        fun fromName(name: String): Gender {
            return entries.first { it.name == name }
        }
    }
}