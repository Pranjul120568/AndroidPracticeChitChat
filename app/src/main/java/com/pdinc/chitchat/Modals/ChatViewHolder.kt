package com.pdinc.chitchat.Modals

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.pdinc.chitchat.R
import com.pdinc.chitchat.Utils.formatAsListItem
import com.squareup.picasso.Picasso

class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: Inbox, onClick: (name: String, imageUrl: String, id: String) -> Unit) =
            with(itemView) {
                findViewById<TextView>(R.id.countTv).isVisible = item.count >0
                findViewById<TextView>(R.id.countTv).text=item.count.toString()
                findViewById<TextView>(R.id.timeTv).text = item.time.formatAsListItem(context)
                findViewById<TextView>(R.id.titleTv).text = item.name
                findViewById<TextView>(R.id.subTitleTv).text = item.msg
                Picasso.get().load(item.image).placeholder(R.drawable.common_google_signin_btn_icon_dark_focused)
                        .error(R.drawable.common_google_signin_btn_icon_dark_focused)
                        .into(findViewById<ImageView>(R.id.userImgView))
                setOnClickListener {
                    onClick.invoke(item.name, item.image, item.from)
                }
            }
}