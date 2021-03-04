package com.pdinc.chitchat.Modals

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pdinc.chitchat.R
import com.pdinc.chitchat.databinding.ListItemBinding
import com.squareup.picasso.Picasso

class UserVIewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var binding:ListItemBinding

    fun bind(user: User,onClick:(name:String,photo:String,id:String)->Unit)= with(itemView){
        findViewById<TextView>(R.id.countTv).isVisible=false
        findViewById<TextView>(R.id.timeTv).isVisible=false
        findViewById<TextView>(R.id.titleTv).text=user.name
        findViewById<TextView>(R.id.titleTv).text=user.status
        Picasso.get().load(user.thumbImage).placeholder(R.drawable.common_google_signin_btn_icon_dark).error(R.drawable.common_google_signin_btn_icon_dark)
            .into(binding.userImgView)

        setOnClickListener {
onClick.invoke(user.name,user.thumbImage,user.uid)
        }
    }
}