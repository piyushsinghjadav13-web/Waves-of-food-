package com.example.wavesoffood2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood2.databinding.BuyAgainItemBinding

class BuyAgainAdapter(
    private val foodNames: MutableList<String>,
    private val foodPrices: MutableList<String>,
    private val foodImages: MutableList<String>,
    private val context: Context,
    private val onBuyAgainClick: (Int) -> Unit
) : RecyclerView.Adapter<BuyAgainAdapter.ViewHolder>() {

    inner class ViewHolder(val bind: BuyAgainItemBinding) :
        RecyclerView.ViewHolder(bind.root) {

        fun setData(pos: Int) {

            bind.buyagainfoodname.text = foodNames[pos]
            bind.buyagainfoodprice.text = "₹${foodPrices[pos]}"

            Glide.with(context).load(foodImages[pos]).centerCrop().into(bind.buyagainfoodimage)

            bind.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    onBuyAgainClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.setData(position)

    override fun getItemCount() = foodNames.size
}
