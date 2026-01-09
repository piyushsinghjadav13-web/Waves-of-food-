package com.example.wavesoffood2

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Splash_Screen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        // Delay using Coroutine (BEST, no warning)
        lifecycleScope.launch {
            delay(3000)   // 3 seconds
            startActivity(Intent(this@Splash_Screen, StartActivity::class.java))
            finish()
        }
    }
}

