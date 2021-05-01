package com.pdinc.chitchat.Modals

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pdinc.chitchat.R
import com.pdinc.chitchat.databinding.ListItemBinding
import com.squareup.picasso.Picasso


class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    //lateinit var binding: ListItemBinding= ListItemBinding.inflate(inf)
    fun bind(user: User, onClick: (name: String, imageUrl: String, id: String) -> Unit) =
            with(itemView) {
                findViewById<TextView>(R.id.countTv).isVisible = false
                findViewById<TextView>(R.id.timeTv).isVisible = false
                findViewById<TextView>(R.id.titleTv).text = user.name
                findViewById<TextView>(R.id.subTitleTv).text = user.status
                Picasso.get().load(user.imageUrl)
                        .placeholder(R.drawable.common_google_signin_btn_icon_dark_focused)
                        .error(R.drawable.common_google_signin_btn_icon_dark_focused)
                        .into(findViewById<ImageView>(R.id.userImgView))
                setOnClickListener {
                    onClick.invoke(user.name, user.imageUrl, user.uid)
                }
            }
}