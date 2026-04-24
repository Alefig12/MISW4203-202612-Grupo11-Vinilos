package com.example.vinilos_grupo11.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vinilos_grupo11.databinding.ActivityHomeBinding
import com.example.vinilos_grupo11.ui.MainActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVisitante.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnColeccionista.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
