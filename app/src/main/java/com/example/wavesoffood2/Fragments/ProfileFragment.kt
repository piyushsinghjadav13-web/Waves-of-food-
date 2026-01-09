package com.example.wavesoffood2.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wavesoffood2.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

data class UserModle(
    var name: String? = "",
    var email: String? = "",
    var phone: String? = "",
    var address: String? = ""
)

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setUserData()
            binding.apply {
            name1.isEnabled = false
            Email.isEnabled = false
            Address.isEnabled = false
            Phone.isEnabled = false
        binding.editbtn.setOnClickListener {

                name1.isEnabled = !name1.isEnabled
                Phone.isEnabled = !Phone.isEnabled
                Address.isEnabled = !Address.isEnabled
                Email.isEnabled = !Email.isEnabled
            }
        }


        binding.payoutbtn.setOnClickListener {

            val name = binding.name1.text.toString()
            val email = binding.Email.text.toString()
            val phone = binding.Phone.text.toString()
            val address = binding.Address.text.toString()

            updateUserData(name, email, phone, address)
        }

        return binding.root
    }

    private fun updateUserData(name: String, email: String, phone: String, address: String) {

        val userId = auth.currentUser?.uid

        if (userId != null) {

            val userReference = database.getReference("user").child(userId)

            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "address" to address
            )

            userReference.setValue(userData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Profile Update Failed!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {

            val userReference = database.getReference("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {

                        val userProfile = snapshot.getValue(UserModle::class.java)

                        userProfile?.let {

                            binding.name1.setText(it.name)
                            binding.Email.setText(it.email)
                            binding.Phone.setText(it.phone)
                            binding.Address.setText(it.address)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}
