package com.pdinc.chitchat.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    //In this activity for making the splash which comes when we start to use any
    // app we use a theme which is originally started by manifest file we can check that out by going to manifest file
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.currentUser==null){
            startActivity(Intent(this, Login::class.java))
        }else{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}