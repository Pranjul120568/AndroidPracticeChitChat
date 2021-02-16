package com.pdinc.chitchat.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.currentUser==null){
            startActivity(Intent(this, Login::class.java))
        }
        else{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}