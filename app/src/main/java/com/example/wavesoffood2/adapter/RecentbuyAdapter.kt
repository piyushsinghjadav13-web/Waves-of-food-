package com.example.wavesoffood2.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood2.R
import com.example.wavesoffood2.RecentOrderItems
import com.example.wavesoffood2.databinding.RecentOrderItemBinding

class RecentbuyAdapter(
    private val context: RecentOrderItems,
    private val foodNameList: ArrayList<String>,
    private val foodPriceList: ArrayList<String>,
    private val foodImageList: ArrayList<String>,
    private val foodQuantityList: ArrayList<Int>
) : RecyclerView.Adapter<RecentbuyAdapter.RecentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecentViewHolder(
            RecentOrderItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = foodNameList.size   //🔥 ab ZERO nahi hoga

    inner class RecentViewHolder(private val binding: RecentOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pos: Int) {

            binding.foodname.text = foodNameList[pos]
            binding.foodprice.text = "₹" + foodPriceList[pos]
            binding.quantity.text = foodQuantityList[pos].toString()

            // 🔥 If no image → default placeholder
            if (foodImageList[pos].isEmpty()) {
                binding.foodimage.setImageResource(R.drawable.burger3)
            } else {
                Glide.with(context)
                    .load(Uri.parse(foodImageList[pos]))
                    .placeholder(R.drawable.burger3)
                    .into(binding.foodimage)
            }
        }
    }
}
