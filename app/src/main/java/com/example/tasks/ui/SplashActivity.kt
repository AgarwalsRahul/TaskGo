package com.example.tasks.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.tasks.R
import com.example.tasks.ui.auth.LoginActivity
import com.example.tasks.ui.todo.MainActivity


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val i = Intent(this@SplashActivity, LoginActivity::class.java)
        Handler().postDelayed(Runnable {
            startActivity(i)
            finish()
        }, 1000)
    }
}