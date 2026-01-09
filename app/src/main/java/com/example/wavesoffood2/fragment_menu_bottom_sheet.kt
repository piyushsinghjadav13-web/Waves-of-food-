package com.example.wavesoffood2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood2.adapter.menuadapter
import com.example.wavesoffood2.databinding.FragmentMenuBottomSheetBinding
import com.example.wavesoffood2.modle.menuitem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.*

class FragmentMenuBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMenuBottomSheetBinding
    private lateinit var database: FirebaseDatabase
    private val menuItems = mutableListOf<menuitem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false)

        binding.backbtn.setOnClickListener { dismiss() }

        retrieveMenuItems()

        return binding.root
    }

    private fun retrieveMenuItems() {

        database = FirebaseDatabase.getInstance()
        database.reference.child("menu")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    menuItems.clear()

                    for (foodSnapshot in snapshot.children) {
                        val d = foodSnapshot.getValue(menuitem::class.java)

                        menuItems.add(
                            menuitem(
                                d?.foodName ?: "",
                                d?.foodDescription ?: "",
                                d?.foodImage ?: "",
                                d?.foodPrice ?: "",
                                d?.foodQuantity ?: "1",
                                d?.foodIngredient ?: ""
                            )
                        )
                    }

                    Log.d("MenuBottomSheet", "Loaded: ${menuItems.size}")
                    setAdapter()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MenuBottomSheet", "Firebase Error: ${error.message}")
                }
            })
    }

    private fun setAdapter() {

        val nameList        = menuItems.map { it.foodName ?: "" }
        val priceList       = menuItems.map { it.foodPrice ?: "" }
        val imageList       = menuItems.map { it.foodImage ?: "" }
        val descriptionList = menuItems.map { it.foodDescription ?: "" }   // ⭐ Added
        val ingredientsList = menuItems.map { it.foodIngredient ?: "" }    // ⭐ Added

        binding.menurecyclerview.layoutManager = LinearLayoutManager(requireContext())

        binding.menurecyclerview.adapter = menuadapter(
            nameList,
            priceList,
            imageList,
            descriptionList,   // ⭐ Added
            ingredientsList,   // ⭐ Added
            requireContext()
        )

        Log.d("MenuBottomSheet", "Adapter Set Successfully 👍")
    }
}
