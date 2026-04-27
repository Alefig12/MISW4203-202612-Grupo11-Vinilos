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
            saveUserRole("Visitante")
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnColeccionista.setOnClickListener {
            saveUserRole("Coleccionista")
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun saveUserRole(role: String) {
        val sharedPref = getSharedPreferences("VinilosPrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_role", role)
            apply()
        }
    }
}
