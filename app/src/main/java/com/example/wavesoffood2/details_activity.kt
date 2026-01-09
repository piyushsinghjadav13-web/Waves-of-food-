package com.example.wavesoffood2

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.wavesoffood2.databinding.ActivityDetailsBinding
import com.example.wavesoffood2.modle.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class details_activity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var auth: FirebaseAuth

    // Class level variables
    private var foodName: String? = null
    private var foodDescription: String? = null
    private var foodIngredients: String? = null
    private var foodPrice: String? = null
    private var foodImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Receive data
        foodName = intent.getStringExtra("MenuItemName")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodIngredients = intent.getStringExtra("MenuItemIngredients")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        foodImage = intent.getStringExtra("MenuItemImage")

        // Set UI
        with(binding) {
            detailfoodname.text = foodName
            descriptiontion.text = foodDescription
            ingrediant.text = foodIngredients

            foodImage?.let {
                Glide.with(this@details_activity)
                    .load(Uri.parse(it))
                    .into(deetailfoodimage)
            }
        }

        // Back button
        binding.imageButton3.setOnClickListener {
            finish()
        }

        // Add to cart
        binding.buttoncart.setOnClickListener {
            addItemToCart()
        }
    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid ?: ""

        // ✔ Correct object format according to your CartItems model
        val cartItem = CartItems(
            foodImage = foodImage,
            foodDescription = foodDescription,
            foodName = foodName,
            foodPrice = foodPrice,
            foodQuantity = "1"
        )

        database.child("user").child(userId).child("CartItems")
            .push().setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Item added to cart successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
            }
    }
}
