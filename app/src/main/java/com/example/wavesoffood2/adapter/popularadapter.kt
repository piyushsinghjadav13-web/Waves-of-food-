package com.example.wavesoffood2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.UiContext
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood2.databinding.PopularitemsBinding
import com.example.wavesoffood2.details_activity

class PopularAdapter(
    private val items: List<String>,
    private val prices: List<String>,
    private val images: List<Int>,
    private val requiredContext: Context,
    private val onAddToCart: (String, String, Int) -> Unit
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val binding = PopularitemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PopularViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.bind(items[position], prices[position], images[position])

        holder.itemView.setOnClickListener {
            val intent = Intent(requiredContext, details_activity::class.java)
            intent.putExtra("MenuItem", items[position])
            intent.putExtra("MenuImage", images[position])
            requiredContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class PopularViewHolder(private val binding: PopularitemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(itemName: String, itemPrice: String, imageRes: Int) {
            binding.foodname.text = itemName
            binding.foodprice.text = itemPrice
            binding.foodimage.setImageResource(imageRes)

            binding.root.setOnClickListener {
                onAddToCart(itemName, itemPrice, imageRes)
            }
        }
    }
}
