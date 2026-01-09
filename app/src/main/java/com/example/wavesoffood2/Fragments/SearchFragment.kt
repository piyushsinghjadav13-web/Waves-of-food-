package com.example.wavesoffood2.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood2.adapter.menuadapter
import com.example.wavesoffood2.databinding.FragmentSearchBinding
import com.example.wavesoffood2.modle.menuitem
import com.google.firebase.database.*

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var database: FirebaseDatabase

    private val originalMenu = mutableListOf<menuitem>()  // All items from Firebase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchBinding.inflate(inflater, container, false)

        retrieveMenuItems()
        setupSearch()

        return binding.root
    }

    private fun retrieveMenuItems() {

        database = FirebaseDatabase.getInstance()
        val ref = database.reference.child("menu")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                originalMenu.clear()

                for (snap in snapshot.children) {
                    val item = snap.getValue(menuitem::class.java)
                    if (item != null) {
                        originalMenu.add(item)
                    }
                }

                // Show full list initially
                showList(originalMenu)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupSearch() {

        // ⭐ Make entire search bar clickable — always expanded
        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.isIconified = false
        binding.searchView.clearFocus()

        // ⭐ Tap anywhere on search bar → focus + keyboard
        binding.searchCard.setOnClickListener {
            binding.searchView.isIconified = false
            binding.searchView.requestFocusFromTouch()
        }

        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
            binding.searchView.requestFocusFromTouch()
        }

        // ⭐ Real-time search listener
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = true
            override fun onQueryTextChange(text: String?): Boolean {
                filterMenu(text ?: "")
                return true
            }
        })
    }

    private fun filterMenu(query: String) {

        val filtered = originalMenu.filter {
            it.foodName?.contains(query, ignoreCase = true) == true
        }

        showList(filtered)
    }

    // ⭐ Sends all updated lists to adapter
    private fun showList(list: List<menuitem>) {

        val fName = list.map { it.foodName ?: "" }.toMutableList()
        val fPrice = list.map { it.foodPrice ?: "" }.toMutableList()
        val fImage = list.map { it.foodImage ?: "" }.toMutableList()
        val fDescription = list.map { it.foodDescription ?: "" }.toMutableList()
        val fIngredients = list.map { it.foodIngredient ?: "" }.toMutableList()

        binding.cartrecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.cartrecyclerview.adapter = menuadapter(
            fName,
            fPrice,
            fImage,
            fDescription,
            fIngredients,
            requireContext()
        )
    }
}
