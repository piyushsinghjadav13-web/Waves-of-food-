package com.example.wavesoffood2.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood2.PayoutActivity
import com.example.wavesoffood2.adapter.CardAdapter
import com.example.wavesoffood2.databinding.FragmentCartBinding
import com.example.wavesoffood2.modle.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String

    private val foodNames = mutableListOf<String>()
    private val foodPrices = mutableListOf<String>()
    private val foodDescriptions = mutableListOf<String>()
    private val foodImages = mutableListOf<String>()
    private val quantity = mutableListOf<String>()

    private val cartKeys = mutableListOf<String>()   // ⭐ FIREBASE KEYS

    private lateinit var adapter: CardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""

        loadCart()

        binding.checkout.setOnClickListener {
            if (foodNames.isEmpty())
                Toast.makeText(requireContext(), "Cart Empty!", Toast.LENGTH_SHORT).show()
            else openPayout()
        }

        return binding.root
    }

    private fun loadCart() {

        database.reference.child("user").child(userId).child("CartItems")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    foodNames.clear()
                    foodPrices.clear()
                    foodDescriptions.clear()
                    foodImages.clear()
                    quantity.clear()
                    cartKeys.clear()

                    for (snap in snapshot.children) {

                        val item = snap.getValue(CartItems::class.java)

                        foodNames.add(item?.foodName ?: "")
                        foodPrices.add(item?.foodPrice ?: "")
                        foodDescriptions.add(item?.foodDescription ?: "")
                        foodImages.add(item?.foodImage ?: "")
                        quantity.add(item?.foodQuantity ?: "1")

                        cartKeys.add(snap.key!!)  // ⭐ REQUIRED FOR DELETE
                    }

                    adapter = CardAdapter(
                        requireContext(),
                        foodNames,
                        foodPrices,
                        foodDescriptions,
                        foodImages,
                        quantity,
                        cartKeys
                    )

                    binding.cartrecyclerview.layoutManager = LinearLayoutManager(requireContext())
                    binding.cartrecyclerview.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun openPayout() {

        val updatedQty = adapter.getUpdatedQuantities()

        val i = Intent(requireContext(), PayoutActivity::class.java)
        i.putStringArrayListExtra("FoodItemName", ArrayList(foodNames))
        i.putStringArrayListExtra("FoodItemPrice", ArrayList(foodPrices))
        i.putStringArrayListExtra("FoodItemQuantity", ArrayList(updatedQty))

        startActivity(i)
    }
}
