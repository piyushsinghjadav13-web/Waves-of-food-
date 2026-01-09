package com.example.wavesoffood2.utils

object CartManager {
    val cartItems = mutableListOf<String>()
    val cartPrices = mutableListOf<String>()
    val cartImages = mutableListOf<Int>()

    // ✅ New: History Items
    val historyItems = mutableListOf<String>()
    val historyPrices = mutableListOf<String>()
    val historyImages = mutableListOf<Int>()

    fun addItem(item: String, price: String, image: Int) {
        cartItems.add(item)
        cartPrices.add(price)
        cartImages.add(image)

        // ✅ Also save in history
        historyItems.add(item)
        historyPrices.add(price)
        historyImages.add(image)
    }

    fun removeItem(position: Int) {
        cartItems.removeAt(position)
        cartPrices.removeAt(position)
        cartImages.removeAt(position)
    }
}
