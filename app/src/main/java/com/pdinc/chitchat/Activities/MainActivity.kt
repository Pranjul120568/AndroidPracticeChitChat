package com.pdinc.chitchat.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
        TabLayoutMediator.TabConfigurationStrategy{tab:TabLayout.Tab,pos:Int ->
            when(pos){
                0 -> tab.text="CHATS"
                1 -> tab.text="PEOPLE"
            }
        }
    }
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}