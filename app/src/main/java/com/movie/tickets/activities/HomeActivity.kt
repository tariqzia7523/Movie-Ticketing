package com.movie.tickets.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.movie.tickets.databinding.ActivityHomeScreenBinding

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.adminLogin.setOnClickListener {
            startActivity(Intent(this@HomeActivity,LoginActivity::class.java))
        }
        binding.viewtickets.setOnClickListener {
            startActivity(Intent(this@HomeActivity,MivieListActivity::class.java))
        }

    }
}