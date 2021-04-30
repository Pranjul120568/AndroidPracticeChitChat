package com.pdinc.chitchat.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pdinc.chitchat.Fragments.InboxFragment
import com.pdinc.chitchat.Fragments.PeopleFragment
//This adapter is implemented in main activity in (feature of viewPager2) it is just
// used to make the feature of slide when we slide from chats to people fragment
class ScreenSlideAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment=when(position) {
        0 -> InboxFragment()
        else -> PeopleFragment()
    }
}