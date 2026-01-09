package com.example.wavesoffood2.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood2.details_activity
import com.example.wavesoffood2.databinding.MenuItemBinding

class menuadapter(
    private val foodNameList: List<String>,
    private val foodPriceList: List<String>,
    private val foodImageList: List<String>,
    private val foodDescriptionList: List<String>,       // ⭐ NEW
    private val foodIngredientsList: List<String>,       // ⭐ NEW
    private val context: Context
) : RecyclerView.Adapter<menuadapter.MenuViewHolder>() {

    inner class MenuViewHolder(val binding: MenuItemBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuViewHolder(binding)
    }

    override fun getItemCount(): Int =
        minOf(foodNameList.size, foodPriceList.size, foodImageList.size)

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {

        holder.binding.menuname.text = foodNameList[position]
        holder.binding.menuprice.text = foodPriceList[position]

        val img = foodImageList[position]
        val resId = img.toIntOrNull()

        if (resId != null) {
            Glide.with(context).load(resId).into(holder.binding.menuimage)
        } else {
            Glide.with(context).load(Uri.parse(img)).into(holder.binding.menuimage)
        }

        // ⭐ NOW CLICK → OPEN DETAILS PAGE with REAL DATA
        holder.itemView.setOnClickListener {

            val intent = Intent(context, details_activity::class.java)

            intent.putExtra("MenuItemName", foodNameList[position])
            intent.putExtra("MenuItemPrice", foodPriceList[position])
            intent.putExtra("MenuItemImage", foodImageList[position])
            intent.putExtra("MenuItemDescription", foodDescriptionList[position]) // ⭐ REAL DATA
            intent.putExtra("MenuItemIngredients", foodIngredientsList[position]) // ⭐ REAL DATA

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
