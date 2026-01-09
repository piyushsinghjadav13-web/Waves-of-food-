package com.example.wavesoffood2.modle

import java.io.Serializable

data class OrderDetails(

    var userUid: String? = null,
    var userName: String? = null,
    var phoneNumber: String? = null,
    var address: String? = null,

    // FOOD DETAIL LISTS
    var foodNames: MutableList<String> = mutableListOf(),
    var foodPrices: MutableList<String> = mutableListOf(),
    var foodImages: MutableList<String> = mutableListOf(),
    var foodQuantities: MutableList<String> = mutableListOf(),     // Quantity stored as string now ✔

    // PAYMENT / ORDER INFO
    var totalPrice: String? = null,
    var itemPushKey: String? = null,
    var paymentReceived: Boolean = false,
    var orderAccepted: Boolean = false,

    // TIME (Long = BEST for sorting recent)
    var currentTime: Long = 0L

) : Serializable       // REQUIRED ✔ FOR SENDING VIA INTENT
