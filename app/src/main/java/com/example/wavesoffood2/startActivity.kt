package com.example.wavesoffood2

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wavesoffood2.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private val binding:ActivityStartBinding by lazy {
        ActivityStartBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {0
        super.onCreate(savedInstanceState)

        setContentView(binding.root)


        binding.nextbutton.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent);
        }


    }
}