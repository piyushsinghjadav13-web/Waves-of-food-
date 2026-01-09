package com.example.wavesoffood2.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wavesoffood2.RecentOrderItems
import com.example.wavesoffood2.adapter.BuyAgainAdapter
import com.example.wavesoffood2.databinding.FragmentHistoryBinding
import com.example.wavesoffood2.modle.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.graphics.Color

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: BuyAgainAdapter
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()

    private var orderList = mutableListOf<OrderDetails>()

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        loadHistory()

        binding.btnClearAll.setOnClickListener { clearAllHistory() }

        // ⭐ FINAL RECEIVE BUTTON CODE
        binding.reciveitem.setOnClickListener {

            if (orderList.isEmpty()) return@setOnClickListener

            val itemPushKey = orderList[0].itemPushKey ?: return@setOnClickListener
            val uid = auth.currentUser?.uid ?: return@setOnClickListener

            // 1️⃣ Mark payment received
            db.reference.child("dispatchOrders")
                .child(itemPushKey)
                .child("paymentReceived")
                .setValue(true)
                .addOnSuccessListener {

                    // 2️⃣ Make button gray + disable
                    binding.reciveitem.apply {
                        isEnabled = false
                        setBackgroundColor(Color.GRAY)
                        text = "Received"
                    }

                    // 3️⃣ Remove from BuyHistory
                    db.reference.child("user")
                        .child(uid)
                        .child("BuyHistory")
                        .child(itemPushKey)
                        .removeValue()

                    Toast.makeText(requireContext(), "Order Received & Removed", Toast.LENGTH_SHORT).show()

                    // 4️⃣ HIDE UI instantly (very important)
                    binding.reciveitem.visibility = View.GONE
                    binding.recentbuyItem.visibility = View.GONE
                    binding.recyclerViewHistory.visibility = View.GONE

                    // 5️⃣ Reload clean list
                    loadHistory()
                }
        }

        return binding.root
    }

    private fun loadHistory() {
        val uid = auth.currentUser?.uid ?: return

        db.reference.child("user").child(uid).child("BuyHistory")
            .orderByChild("currentTime")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    orderList.clear()

                    for (snap in snapshot.children) {
                        val item = snap.getValue(OrderDetails::class.java)
                        if (item != null) orderList.add(item)
                    }

                    if (orderList.isEmpty()) {
                        hideUI()
                        return
                    }

                    // Latest first
                    orderList.sortByDescending { it.currentTime }

                    // ⭐ Keep only latest 15
                    if (orderList.size > 15) {

                        val itemsToRemove = orderList.subList(15, orderList.size)
                        itemsToRemove.forEach { oldItem ->
                            oldItem.itemPushKey?.let { key ->
                                db.reference.child("user")
                                    .child(uid)
                                    .child("BuyHistory")
                                    .child(key)
                                    .removeValue()
                            }
                        }

                        orderList = orderList.subList(0, 15).toMutableList()
                    }

                    setLatestOrderUI()
                    setOldOrdersList()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun hideUI() {
        binding.recentbuyItem.visibility = View.GONE
        binding.recyclerViewHistory.visibility = View.GONE
        binding.reciveitem.visibility = View.GONE
    }

    private fun setLatestOrderUI() {

        val item = orderList[0]

        binding.recentbuyItem.visibility = View.VISIBLE

        binding.buyagainfoodname.text = item.foodNames.firstOrNull() ?: "No Name"
        binding.buyagainfoodprice.text = item.foodPrices.firstOrNull() ?: "0"

        Glide.with(requireContext())
            .load(item.foodImages.firstOrNull())
            .into(binding.buyagainfoodimage)

        binding.recentbuyItem.setOnClickListener { openLatestOrder() }

        // ⭐ If accepted, show Receive button
        if (item.orderAccepted) {
            binding.orderstatus.background.setTint(Color.GREEN)
            binding.reciveitem.visibility = View.VISIBLE
        }
    }

    private fun setOldOrdersList() {
        if (orderList.size <= 1) {
            binding.recyclerViewHistory.visibility = View.GONE
            return
        }

        val oldOrders = orderList.subList(1, orderList.size)

        val names = oldOrders.map { it.foodNames.firstOrNull() ?: "" }.toMutableList()
        val prices = oldOrders.map { it.foodPrices.firstOrNull() ?: "" }.toMutableList()
        val images = oldOrders.map { it.foodImages.firstOrNull() ?: "" }.toMutableList()

        adapter = BuyAgainAdapter(names, prices, images, requireContext()) {}

        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistory.adapter = adapter
    }

    private fun openLatestOrder() {
        if (orderList.isEmpty()) {
            Toast.makeText(requireContext(), "No Recent Order Found!", Toast.LENGTH_SHORT).show()
            return
        }

        val latestOrderList = arrayListOf(orderList[0])

        val intent = Intent(requireContext(), RecentOrderItems::class.java)
        intent.putExtra("RecentBuyOrderItem", latestOrderList)
        startActivity(intent)
    }

    private fun clearAllHistory() {
        val uid = auth.currentUser?.uid ?: return

        db.reference.child("user").child(uid).child("BuyHistory").removeValue()
        hideUI()

        Toast.makeText(requireContext(), "History Cleared", Toast.LENGTH_SHORT).show()
    }
}
