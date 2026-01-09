package com.example.wavesoffood2.modle

data class menuitem(
    var foodName: String? = "",
    var foodDescription: String? = "",
    var foodImage: String? = "",
    var foodPrice: String? = "",  // ❗ String only
    var foodQuantity: String? = "1",   // ← INT NAHI STRING ✔
    var foodIngredient: String? = ""
)
