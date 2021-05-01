package com.pdinc.chitchat.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.pdinc.chitchat.Modals.DateHeader
import com.pdinc.chitchat.Modals.Message
import com.pdinc.chitchat.Modals.chatEvent
import com.pdinc.chitchat.R
import com.pdinc.chitchat.Utils.formatAsTime

class ChatAdapter(private val list:MutableList<chatEvent>, private val mCurrentUid:String):
         RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var highFiveClick: ((id: String, status: Boolean) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

         val inflate ={layout:Int ->
            LayoutInflater.from(parent.context).inflate(layout,parent,false)
         }


         return when(viewType){
             TEXT_MESSAGE_RECIEVED->{
                 messageViewHolder(inflate(R.layout.list_item_chat_message_rcv))
             }
             TEXT_MESSAGE_SENT->{
                 messageViewHolder(inflate(R.layout.list_item_chat_message_sent))
             }DATE_HEADER->{
                 dateViewHolder(inflate(R.layout.list_item_date_header))
             }else ->{
                 messageViewHolder(inflate(R.layout.list_item_chat_message_rcv))
             }
         }
     }

     override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
         when(val item=list[position]){
             is DateHeader->{
                 holder.itemView.findViewById<TextView>(R.id.dateTv).text=item.date
             }
             is Message->{
                 holder.itemView.apply {
                     findViewById<TextView>(R.id.content).text=item.msg
                     findViewById<TextView>(R.id.time).text=item.sentAt.formatAsTime()
                     when (getItemViewType(position)) {
                         TEXT_MESSAGE_RECIEVED -> {
                             holder.itemView.findViewById<MaterialCardView>(R.id.messageCardView).setOnClickListener(
                                     object : DoubleClickListener() {
                                 override fun onDoubleClick(v: View?) {
                                     highFiveClick!!.invoke(item.msgId, !item.liked)
                                 }
                             })
                             holder.itemView.findViewById<ImageView>(R.id.highFiveImg).apply {
                                 isVisible = position == itemCount - 1 || item.liked
                                 isSelected = item.liked
                                 setOnClickListener {
                                     highFiveClick?.invoke(item.msgId, !isSelected)
                                 }
                             }
                         }

                         TEXT_MESSAGE_SENT -> {
                             holder.itemView.findViewById<ImageView>(R.id.highFiveImg).apply {
                                 isVisible = item.liked
                             }
                         }
                     }
                 }
             }
         }
     }
     override fun getItemCount(): Int =list.size

     override fun getItemViewType(position: Int): Int {
         return when(val event=list[position]){
             is Message ->{
                 if(event.senderId==mCurrentUid){
                     TEXT_MESSAGE_SENT
                 }else{
                     TEXT_MESSAGE_RECIEVED
                 }
             }
             is DateHeader-> DATE_HEADER

             else -> UNSUPPORTED
         }
     }

     class dateViewHolder(view: View):RecyclerView.ViewHolder(view)

     class messageViewHolder(view: View):RecyclerView.ViewHolder(view)


     //they are basically static variables
     companion object{
         private const val UNSUPPORTED=-1
         private const val TEXT_MESSAGE_RECIEVED=0
         private const val TEXT_MESSAGE_SENT=1
         private const val DATE_HEADER=2
     }
 }
abstract class DoubleClickListener : View.OnClickListener {
    var lastClickTime: Long = 0
    override fun onClick(v: View?) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
            lastClickTime = 0
        }
//        else {
//            onSingleClick(v)
//        }
        lastClickTime = clickTime
    }

    //    abstract fun onSingleClick(v: View?)
    abstract fun onDoubleClick(v: View?)

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
    }
}