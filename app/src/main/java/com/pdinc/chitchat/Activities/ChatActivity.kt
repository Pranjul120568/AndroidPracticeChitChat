package com.pdinc.chitchat.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.pdinc.chitchat.Modals.Inbox
import com.pdinc.chitchat.Modals.Message
import com.pdinc.chitchat.Modals.User
import com.pdinc.chitchat.R
import com.pdinc.chitchat.databinding.ActivityChatBinding
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider

const val UID="uid"
const val NAME="name"
const val IMAGE="photo"
class ChatActivity : AppCompatActivity() {
    private val friendsId by lazy { intent.getStringExtra(UID) }
    private val name by lazy { intent.getStringExtra(NAME) }
    private val image by lazy { intent.getStringExtra(IMAGE) }
    private val mCurrentId by lazy { FirebaseAuth.getInstance().uid }
    lateinit var currentUser: User
    private val db by lazy { FirebaseDatabase.getInstance() }
    private lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityChatBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
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
val id=getMessages(friendsId!!).push().key  //UniqueKey
        checkNotNull(id){"Cannot be null"}
        val msgMap= mCurrentId?.let { Message(msg, it,id) }
        getMessages(friendsId!!).child(id.toString()).setValue(msgMap).addOnSuccessListener {
            Log.i("CHATS","completed")
        }.addOnFailureListener {
            Log.i("CHATS",it.localizedMessage)
        }
        updateLastMessage(msgMap!!)
    }
    private fun updateLastMessage(message: Message) {
val InboxMap= Inbox(
            message.msg,
            friendsId!!,
            name!!,
            image!!,
            count = 0
    )
         getInbox(mCurrentId!!,friendsId!!).setValue(InboxMap).addOnSuccessListener {
getInbox(friendsId!!,mCurrentId!!).addListenerForSingleValueEvent(object :ValueEventListener{
    override fun onDataChange(snapshot: DataSnapshot) {
         val value=snapshot.getValue(Inbox::class.java)
        InboxMap.apply {
            from=message.senderId
            name=currentUser.name
            image=currentUser.thumbImage
            count=1
        }
       value.let {
           if(it!!.from==message.senderId){
               InboxMap.count=value!!.count+1
           }
       }
        getInbox(friendsId!!,mCurrentId!!)
    }
    override fun onCancelled(error: DatabaseError) {

    }

})
         }
}
    private fun markAsRead(){
        getInbox(friendsId!!,mCurrentId!!).child("count").setValue(0)
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
    companion object {

        fun createChatActivity(context: Context, id: String, name: String, image: String): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(UID, id)
            intent.putExtra(NAME, name)
            intent.putExtra(IMAGE, image)

            return intent
        }
    }
}