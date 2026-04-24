package com.example.vinilos_grupo11

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vinilos_grupo11.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVisitante.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_ROLE, MainActivity.ROLE_VISITOR)
            })
        }

        binding.btnColeccionista.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_ROLE, MainActivity.ROLE_COLLECTOR)
            })
        }
    }
}
