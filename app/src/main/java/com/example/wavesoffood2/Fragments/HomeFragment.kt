package com.example.wavesoffood2.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.wavesoffood2.FragmentMenuBottomSheet
import com.example.wavesoffood2.R
import com.example.wavesoffood2.adapter.menuadapter
import com.example.wavesoffood2.databinding.FragmentHomeBinding
import com.example.wavesoffood2.modle.menuitem
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private val menuItems = mutableListOf<menuitem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewmenu.setOnClickListener {
            FragmentMenuBottomSheet().show(parentFragmentManager, "Menu")
        }

        loadPopularMenuItems()

        return binding.root
    }

    private fun loadPopularMenuItems() {
        database = FirebaseDatabase.getInstance()

        database.reference.child("menu")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    menuItems.clear()

                    for (i in snapshot.children) {
                        val d = i.getValue(menuitem::class.java)
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

                    showPopularItems()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showPopularItems() {

        if (menuItems.isEmpty()) return

        val list = menuItems.shuffled().take(6)

        val nameList = list.map { it.foodName ?: "" }.toMutableList()
        val priceList = list.map { it.foodPrice ?: "" }.toMutableList()
        val imageList = list.map { it.foodImage ?: "" }.toMutableList()
        val descriptionList = list.map { it.foodDescription ?: "" }.toMutableList()   // ⭐ Added
        val ingredientsList = list.map { it.foodIngredient ?: "" }.toMutableList()    // ⭐ Added

        binding.popularrecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.popularrecyclerView.adapter = menuadapter(
            nameList,
            priceList,
            imageList,
            descriptionList,      // ⭐ Added
            ingredientsList,       // ⭐ Added
            requireContext()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.imageSlider.setImageList(
            listOf(
                SlideModel(R.drawable.baner1, ScaleTypes.FIT),
                SlideModel(R.drawable.baner2, ScaleTypes.FIT),
                SlideModel(R.drawable.baner3, ScaleTypes.FIT)
            ),
            ScaleTypes.FIT
        )

        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {}
            override fun onItemSelected(position: Int) {
                Toast.makeText(requireContext(), "Banner $position Selected", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
