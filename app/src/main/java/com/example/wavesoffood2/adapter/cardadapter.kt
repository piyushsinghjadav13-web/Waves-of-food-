package com.example.wavesoffood2.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood2.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CardAdapter(
    private val context: Context,
    private val foodNames: MutableList<String>,
    private val foodPrices: MutableList<String>,
    private val foodDescriptions: MutableList<String>,
    private val foodImages: MutableList<String>,
    private val foodQuantities: MutableList<String>,
    private val cartKeys: MutableList<String>   // ⭐ REQUIRED KEYS
) : RecyclerView.Adapter<CardAdapter.CartViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid ?: ""
    private val cartRef = FirebaseDatabase.getInstance()
        .reference.child("user").child(userId).child("CartItems")

    private val updatedQuantities =
        foodQuantities.map { it.toIntOrNull() ?: 1 }.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CartViewHolder(CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = foodNames.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) =
        holder.bind(position)

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            // Set values
            binding.Cardfoodname.text = foodNames[position]
            binding.cartitemprice.text = foodPrices[position]
            binding.CardItemquantity.text = updatedQuantities[position].toString()

            Glide.with(context)
                .load(Uri.parse(foodImages[position]))
                .into(binding.CardImage)

            // PLUS
            binding.plusbtn.setOnClickListener {
                if (updatedQuantities[position] < 10) {
                    updatedQuantities[position]++
                    binding.CardItemquantity.text = updatedQuantities[position].toString()
                }
            }

            // MINUS
            binding.minusbtn.setOnClickListener {
                if (updatedQuantities[position] > 1) {
                    updatedQuantities[position]--
                    binding.CardItemquantity.text = updatedQuantities[position].toString()
                }
            }

            // DELETE
            binding.deletbtn.setOnClickListener {
                deleteItem(position)
            }
        }
    }

    // FULLY SAFE DELETE
    private fun deleteItem(pos: Int) {

        // 🚨 Avoid invalid crashes
        if (pos < 0 || pos >= foodNames.size) return
        if (pos >= cartKeys.size) return

        val key = cartKeys[pos]

        cartRef.child(key).removeValue().addOnSuccessListener {

            // Firebase reload may clear lists → extra safety
            if (pos >= foodNames.size) return@addOnSuccessListener

            // Remove item safely
            foodNames.removeAt(pos)
            foodPrices.removeAt(pos)
            foodDescriptions.removeAt(pos)
            foodImages.removeAt(pos)
            updatedQuantities.removeAt(pos)
            cartKeys.removeAt(pos)

            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos, itemCount)

            Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show()
        }
    }

    fun getUpdatedQuantities(): MutableList<String> =
        updatedQuantities.map { it.toString() }.toMutableList()
}
