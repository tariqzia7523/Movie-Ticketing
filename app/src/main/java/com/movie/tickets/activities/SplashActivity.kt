package com.movie.tickets.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.movie.tickets.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        FirebaseAuth.getInstance().signOut()
        Handler(Looper.getMainLooper()).postDelayed({
            decideFlow()
        }, 3000)
    }

    private fun decideFlow() {
        startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
//        val currentUser  = FirebaseAuth.getInstance().currentUser
//        if(currentUser == null)
//            startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
//        else {
//            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
////            if(currentUser.uid.equals(getString(R.string.admin_id)))
////                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
////            else
////                startActivity(Intent(this@SplashActivity, ViewCarParkingActivity::class.java))
//        }
        finish()


    }
}