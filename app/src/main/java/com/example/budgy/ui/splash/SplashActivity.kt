package com.example.budgy.ui.splash

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.budgy.MainActivity
import com.example.budgy.R
import com.example.budgy.ui.auth.LoginActivity
import com.example.budgy.ui.auth.RegisterActivity
import com.example.budgy.ui.helper.PreferenceHelper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val animationView: LottieAnimationView = findViewById(R.id.lottieAnimationView)

        // Tambahkan listener untuk memeriksa status animasi
        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.d("SplashActivity", "Animation started")
            }

            override fun onAnimationEnd(animation: Animator) {
                Log.d("SplashActivity", "Animation ended")
            }

            override fun onAnimationCancel(animation: Animator) {
                Log.d("SplashActivity", "Animation cancelled")
            }

            override fun onAnimationRepeat(animation: Animator) {
                Log.d("SplashActivity", "Animation repeated")
            }
        })

        // Memulai animasi secara programatik
        animationView.playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            val isLoggedIn = PreferenceHelper.getIsLoggedIn(this)
            if (isLoggedIn) {
                // Jika sudah login, arahkan ke MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // Jika belum login, arahkan ke LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 3000) // 3 detik untuk menunggu animasi selesai     // 3 detik untuk menunggu animasi selesai
    }
}
