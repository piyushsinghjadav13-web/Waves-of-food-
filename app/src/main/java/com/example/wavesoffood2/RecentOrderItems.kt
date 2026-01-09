package com.example.wavesoffood2

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood2.adapter.RecentbuyAdapter
import com.example.wavesoffood2.databinding.ActivityRecentOrderItemsBinding
import com.example.wavesoffood2.modle.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RecentOrderItems : AppCompatActivity() {

    private lateinit var binding: ActivityRecentOrderItemsBinding
    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentOrderItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener { finish() }
        loadData()
    }

    private fun loadData() {

        val uid = auth.currentUser?.uid ?: return

        db.reference.child("user").child(uid).child("BuyHistory")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (!snapshot.exists()) {
                        Toast.makeText(this@RecentOrderItems, "❌ No Recent Orders!", Toast.LENGTH_LONG).show()
                        return
                    }

                    // 🔥 MERGE ALL ORDER ITEMS (NOT ONLY LAST ONE)
                    val fullNames = ArrayList<String>()
                    val fullPrices = ArrayList<String>()
                    val fullImages = ArrayList<String>()
                    val fullQty = ArrayList<Int>()

                    for (data in snapshot.children) {
                        val order = data.getValue(OrderDetails::class.java)

                        order?.foodNames?.forEach { fullNames.add(it) }
                        order?.foodPrices?.forEach { fullPrices.add(it) }
                        order?.foodQuantities?.forEach { fullQty.add(it.toIntOrNull() ?: 1) }

                        if (order?.foodImages.isNullOrEmpty()) {
                            repeat(order?.foodNames?.size ?: 1) { fullImages.add("") } // placeholder if no url
                        } else {
                            order?.foodImages?.forEach { fullImages.add(it) }
                        }
                    }

                    Log.d("FINAL_RESULT", "TOTAL ITEMS → ${fullNames.size}")

                    binding.recentRecyclerView.layoutManager = LinearLayoutManager(this@RecentOrderItems)
                    binding.recentRecyclerView.adapter =
                        RecentbuyAdapter(this@RecentOrderItems, fullNames, fullPrices, fullImages, fullQty)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
