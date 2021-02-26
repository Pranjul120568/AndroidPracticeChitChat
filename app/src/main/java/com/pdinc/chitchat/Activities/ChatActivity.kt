package com.pdinc.chitchat.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.pdinc.chitchat.Modals.Inbox
import com.pdinc.chitchat.Modals.Message
import com.pdinc.chitchat.Modals.User
import com.pdinc.chitchat.R
import com.pdinc.chitchat.databinding.ActivityChatBinding
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager

const val UID="uid"
const val NAME="name"
const val IMAGE="photo"
class ChatActivity : AppCompatActivity() {
    private val friendsId by lazy { intent.getStringExtra(UID) }
    private val name by lazy { intent.getStringExtra(NAME) }
    private val image by lazy { intent.getStringExtra(IMAGE) }
    private val mCurrentId by lazy {
        FirebaseAuth.getInstance().uid
    }
    lateinit var currentUser: User
    private val db by lazy { FirebaseDatabase.getInstance() }
    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChatBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        EmojiManager.getInstance()
        setContentView(binding.root)

        mCurrentId?.let {
            FirebaseFirestore.getInstance().collection("users").document(it).get()
                    .addOnSuccessListener {
                        currentUser = it.toObject(User::class.java)!!
                    }
        }
        binding.nameTv.text = name
        Picasso.get().load(image).into(binding.userImgView)
        binding.sendBtn.setOnClickListener {
binding.msgEdtv.text.let {
        if(it!!.isNotEmpty()){
sendMessage(it.toString())
            it.clear()
        }
    }
}
        }

    private fun sendMessage(msg: String) {
val id=getMessages(friendsId!!).push()  //UniqueKey
        checkNotNull(id){"Cannot be null"}
        val msgMap=Message(msg,mCurrentId,id)
        getMessages(friendsId!!).child(id.toString()).setValue(msgMap).addOnSuccessListener {
        }
        updateLastMessage(msgMap)
    }

    private fun updateLastMessage(message: Message) {
val InboxMap=
    Inbox(
            message.msg,
            friendsId!!,
            name!!,
            image!!,
            count = 0
    )
}


    private fun getMessages(friendsId: String) = db.reference.child("messages/${getId(friendsId)}")
private fun getInbox(toUser: String,fromUser:String)=db.reference.child("chats/$toUser/$fromUser")
    private fun getId(friendsId: String): String {
return if(friendsId>mCurrentId.toString()){
    mCurrentId+friendsId
}
        else{
friendsId+mCurrentId
        }
    }
}