package com.demo.foodorderanddeliveryappkotlin.models

data class Order(
    val phoneNumber: String,
    val itemName: String,
    val price: Double,
    val restaurant: String
)
