package com.example.wavesoffood2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.wavesoffood2.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val navController = findNavController(R.id.fragmentContainerView)
        val bottomnav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomnav.setOnItemSelectedListener { item ->
            when(item.itemId){

                R.id.homeFragment -> {
                    if(navController.currentDestination?.id != R.id.homeFragment)
                        navController.navigate(R.id.homeFragment)
                }

                R.id.searchFragment -> {
                    if(navController.currentDestination?.id != R.id.searchFragment)
                        navController.navigate(R.id.searchFragment)
                }

                R.id.cartFragment -> {
                    if(navController.currentDestination?.id != R.id.cartFragment)
                        navController.navigate(R.id.cartFragment)
                }

                R.id.historyFragment -> {
                    if(navController.currentDestination?.id != R.id.historyFragment)
                        navController.navigate(R.id.historyFragment)
                }

                R.id.profileFragment -> {
                    if(navController.currentDestination?.id != R.id.profileFragment)
                        navController.navigate(R.id.profileFragment)
                }
            }
            true
        }

        // Notification Image click
        binding.imageView.setOnClickListener {
            val bottomSheetDialog = Notification_Bottom_Fragment()
            bottomSheetDialog.show(supportFragmentManager,"Test")
        }
    }
}
//    }
//}
