package com.pdinc.chitchat.Modals

import android.content.Context
import com.pdinc.chitchat.Utils.formatAsHeader
import java.util.*

interface chatEvent{
        val sentAt:Date
}
data class Message(
        val msg:String,
        val senderId:String,
        val msgId:String,
        val type:String="TEXT",
        val status:Int=1,
        val liked:Boolean=false,
override val sentAt:Date
):chatEvent{
    constructor(): this("","","","",1,false,Date())
    constructor(msg: String,senderId: String,msgId: String):this()
}
data class DateHeader(
        override val sentAt: Date= Date(), val context:Context
):chatEvent{
    val date:String=sentAt.formatAsHeader(context)
}