package com.example.wavesoffood2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood2.adapter.notificationadapter
import com.example.wavesoffood2.databinding.FragmentHistoryBinding
import com.example.wavesoffood2.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class Notification_Bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotificationBottomBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBottomBinding.inflate(inflater,container,false)

        val notifications = listOf("Your order has been Canceled","Order has been taken by the driver","Congrats Your order Placed")
        val notificationsImages = listOf(R.drawable.sademoji,R.drawable.truck,R.drawable.right)

        val adapter = notificationadapter(ArrayList(notifications), ArrayList(notificationsImages))

binding.notificationrecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationrecycler.adapter = adapter

        return binding.root
    }

    companion object {

            }
    }
