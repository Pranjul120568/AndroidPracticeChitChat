package com.pdinc.chitchat.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pdinc.chitchat.Adapter.ScreenSlideAdapter
import com.pdinc.chitchat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.viewPager.adapter= ScreenSlideAdapter(this)
    }


}