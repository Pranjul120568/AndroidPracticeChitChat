package com.pdinc.chitchat.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.pdinc.chitchat.Adapter.ChatAdapter
import com.pdinc.chitchat.Modals.*
import com.pdinc.chitchat.R
import com.pdinc.chitchat.Utils.KeyboardVisibilityUtil
import com.pdinc.chitchat.Utils.isSameDayAs
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


const val UID="uid"
const val NAME="name"
const val IMAGE="photo"

class ChatActivity : AppCompatActivity() {

    private val friendId by lazy {
        intent.getStringExtra(UID)
    }
    private val name by lazy {
        intent.getStringExtra(NAME)
    }
    private val image by lazy {
        intent.getStringExtra(IMAGE)
    }
    private val mCurrentUid: String by lazy {
        FirebaseAuth.getInstance().uid!!
    }
    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    lateinit var currentUser: User
    lateinit var chatAdapter: ChatAdapter
    lateinit var c:RelativeLayout
    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil
    private val mutableItems: MutableList<chatEvent> = mutableListOf()
    private val mLinearLayout: LinearLayoutManager by lazy { LinearLayoutManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider())
        setContentView(R.layout.activity_chat)
         c=findViewById(R.id.rootView)

        keyboardVisibilityHelper = KeyboardVisibilityUtil(c) {
            findViewById<RecyclerView>(R.id.msgRv).scrollToPosition(mutableItems.size - 1)
        }

        FirebaseFirestore.getInstance().collection("users").document(mCurrentUid).get()
                .addOnSuccessListener {
                    currentUser = it.toObject(User::class.java)!!
                }

        chatAdapter = ChatAdapter(mutableItems, mCurrentUid)

        findViewById<RecyclerView>(R.id.msgRv).apply {
            layoutManager = mLinearLayout
            adapter = chatAdapter
        }
        findViewById<TextView>(R.id.nameTv).text = name
        Picasso.get().load(image).into(findViewById<ImageView>(R.id.userImgView))

        val emojiPopup = EmojiPopup.Builder.fromRootView(c).build(findViewById(R.id.msgEdtv))
        findViewById<ImageView>(R.id.smileBtn).setOnClickListener {
            emojiPopup.toggle()
        }
        findViewById<SwipeRefreshLayout>(R.id.swipeToLoad).setOnRefreshListener {
            val workerScope = CoroutineScope(Dispatchers.Main)
            workerScope.launch {
                delay(2000)
                findViewById<SwipeRefreshLayout>(R.id.swipeToLoad).isRefreshing = false
            }
        }

        findViewById<ImageView>(R.id.sendBtn).setOnClickListener {
            findViewById<EmojiEditText>(R.id.msgEdtv).text?.let {
                if (it.isNotEmpty()) {
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }

        listenMessages() { msg, update ->
            if (update) {
                updateMessage(msg)
            } else {
                addMessage(msg)
            }
        }

        chatAdapter.highFiveClick = { id, status ->
            updateHighFive(id, status)
        }
        updateReadCount()
    }

    private fun updateReadCount() {
        getInbox(mCurrentUid, friendId!!).child("count").setValue(0)
    }

    private fun updateHighFive(id: String, status: Boolean) {
        getMessages(friendId!!).child(id).updateChildren(mapOf("liked" to status))
    }

    private fun addMessage(event: Message) {
        val eventBefore = mutableItems.lastOrNull()
        // Add date header if it's a different day
        if ((eventBefore != null
                        && !eventBefore.sentAt.isSameDayAs(event.sentAt))
                || eventBefore == null
        ) {
            mutableItems.add(
                    DateHeader(
                            event.sentAt, this
                    )
            )
        }
        mutableItems.add(event)
        chatAdapter.notifyItemInserted(mutableItems.size)
        findViewById<RecyclerView>(R.id.msgRv).scrollToPosition(mutableItems.size + 1)
    }
    private fun updateMessage(msg: Message) {
        val position = mutableItems.indexOfFirst {
            when (it) {
                is Message -> it.msgId == msg.msgId
                else -> false
            }
        }
        mutableItems[position] = msg

        chatAdapter.notifyItemChanged(position)
    }

    private fun listenMessages(newMsg: (msg: Message, update: Boolean) -> Unit) {
        getMessages(friendId!!)
                .orderByKey()
                .addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                    }

                    override fun onChildChanged(data: DataSnapshot, p1: String?) {
                        val msg = data.getValue(Message::class.java)!!
                        newMsg(msg, true)
                    }

                    override fun onChildAdded(data: DataSnapshot, p1: String?) {
                        val msg = data.getValue(Message::class.java)!!
                        newMsg(msg, false)
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {
                    }

                })

    }

    private fun sendMessage(msg: String) {
        val id = getMessages(friendId!!).push().key
        checkNotNull(id) { "Cannot be null" }
        val msgMap = Message(msg, mCurrentUid, id)
        getMessages(friendId!!).child(id).setValue(msgMap)
        updateLastMessage(msgMap, mCurrentUid)
    }

    private fun updateLastMessage(message: Message, mCurrentUid: String) {
        val inboxMap = Inbox(
                message.msg,
                friendId!!,
                name!!,
                image!!,
                message.sentAt,
                0
        )

        getInbox(mCurrentUid, friendId!!).setValue(inboxMap)

        getInbox(friendId!!, mCurrentUid).addListenerForSingleValueEvent(object :
                ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                val value = p0.getValue(Inbox::class.java)
                inboxMap.apply {
                    from = message.senderId
                    name = currentUser.name
                    image = currentUser.thumbImage
                    count = 1
                }
                if (value?.from == message.senderId) {
                    inboxMap.count = value.count + 1
                }
                getInbox(friendId!!, mCurrentUid).setValue(inboxMap)
            }

        })
    }


    private fun getMessages(friendId: String) = db.reference.child("messages/${getId(friendId)}")

    private fun getInbox(toUser: String, fromUser: String) =
            db.reference.child("chats/$toUser/$fromUser")

    private fun getId(friendId: String): String {
        return if (friendId > mCurrentUid) {
            mCurrentUid + friendId
        } else {
            friendId + mCurrentUid
        }
    }

    override fun onResume() {
        super.onResume()
        c.viewTreeObserver
                .addOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
    }


    override fun onPause() {
        super.onPause()
        c.viewTreeObserver
                .removeOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
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





























//
//const val UID="uid"
//const val NAME="name"
//const val IMAGE="photo"
//class ChatActivity : AppCompatActivity() {
//
//    private lateinit var  c: View
//    private val friendsId by lazy { intent.getStringExtra(UID) }
//    private val name by lazy { intent.getStringExtra(NAME) }
//    private val image by lazy { intent.getStringExtra(IMAGE) }
//    private val mCurrentId by lazy { FirebaseAuth.getInstance().uid }
//    lateinit var currentUser: User
//    private val messages= mutableListOf<chatEvent>()
//    lateinit var chatAdapter:ChatAdapter
//    private val db by lazy { FirebaseDatabase.getInstance() }
// //   private lateinit var binding: ActivityChatBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//  //      binding = ActivityChatBinding.inflate(layoutInflater)
//        EmojiManager.install(GoogleEmojiProvider())
//        setContentView(R.layout.activity_chat)
//        mCurrentId?.let {
//            FirebaseFirestore.getInstance().collection("users").document(it).get()
//                    .addOnSuccessListener {
//                        currentUser = it.toObject(User::class.java)!!
//                    }
//        }
//     chatAdapter= ChatAdapter(messages,mCurrentId!!)
//     findViewById<RecyclerView>(R.id.msgRv).apply {
//         layoutManager=LinearLayoutManager(this@ChatActivity)
//         adapter=chatAdapter
//
//     }
//     val emojiPopUp=EmojiPopup.Builder.fromRootView(c).build(findViewById(R.id.msgEdtv))
//      findViewById<ImageView>(R.id.smileBtn).setOnClickListener {
//      emojiPopUp.toggle()
//}
//
//     listenToMessages()
//        findViewById<TextView>(R.id.nameTv).text = name
//        Picasso.get().load(image).into(findViewById<ImageView>(R.id.userImgView))
//        findViewById<ImageView>(R.id.sendBtn).setOnClickListener {
//findViewById<EmojiEditText>(R.id.msgEdtv).text.let {
//        if(it!!.isNotEmpty()){
//            sendMessage(it.toString())
//            it.clear()
//        }
//    }
//}
//        }
//
//    private fun listenToMessages(){
//        getMessages(friendsId!!)
//                .orderByKey()
//                .addChildEventListener(object :ChildEventListener{
//                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                          val msg=snapshot.getValue(Message::class.java)
//                        addMessage(msg)
//                    }
//
//                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onChildRemoved(snapshot: DataSnapshot) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        TODO("Not yet implemented")
//                    }
//
//                })
//
//    }
//
//    private fun addMessage(msg: Message?) {
//          val eventBefore=messages.lastOrNull()
//        if((eventBefore!=null && eventBefore.sentAt.isSameDayAs(msg!!.sentAt))|| eventBefore==null ){
//            messages.add(
//                    DateHeader(
//                            msg!!.sentAt,context = this
//                    )
//            )
//        }
//        messages.add(msg!!)
//        chatAdapter.notifyItemInserted(messages.size-1)
//        findViewById<RecyclerView>(R.id.msgRv).scrollToPosition(messages.size-1)
//    }
//
//
//    private fun sendMessage(msg: String) {
//        val id=getMessages(friendsId!!).push().key  //UniqueKey
//        checkNotNull(id){"Cannot be null"}
//        val msgMap= mCurrentId?.let { Message(msg, it, id)}
//        getMessages(friendsId!!).child(id).setValue(msgMap).addOnSuccessListener {
//            Log.i("CHATS", "completed")
//        }.addOnFailureListener {
//            Log.i("CHATS", it.localizedMessage)
//        }
//        updateLastMessage(msgMap!!)
//    }
//    private fun updateLastMessage(message: Message) {
//val InboxMap= Inbox(
//        message.msg,
//        friendsId!!,
//        name!!,
//        image!!,
//        count = 0
//)
//         getInbox(mCurrentId!!, friendsId!!).setValue(InboxMap).addOnSuccessListener {
//             getInbox(friendsId!!, mCurrentId!!).addListenerForSingleValueEvent(object : ValueEventListener {
//    override fun onDataChange(snapshot: DataSnapshot) {
//        val value = snapshot.getValue(Inbox::class.java)
//        InboxMap.apply {
//            from = message.senderId
//            name = currentUser.name
//            image = currentUser.imageUrl
//            count = 1
//        }
//        value?.let {
//                if (it.from == message.senderId) {
//                    InboxMap.count = value.count + 1
//            }
//        }
//        getInbox(friendsId!!, mCurrentId!!).setValue(InboxMap)
//    }
//
//    override fun onCancelled(error: DatabaseError) {
//    }
//})
//         }
//}
//    private fun markAsRead(){
//        getInbox(friendsId!!, mCurrentId!!).child("count").setValue(0)
//    }
//    private fun getMessages(friendsId: String) = db.reference.child("messages/${getId(friendsId)}")
//
//
//    private fun getInbox(toUser: String, fromUser: String)=db.reference.child("chats/$toUser/$fromUser")
//
//
//    private fun getId(friendsId: String): String {
//     return if(friendsId>mCurrentId!! ){
//         mCurrentId+friendsId
//              }
//        else{
//         friendsId+mCurrentId
//        }
//    }
//    companion object {
//        fun createChatActivity(context: Context, id: String, name: String, image: String): Intent {
//            val intent = Intent(context, ChatActivity::class.java)
//            intent.putExtra(UID, id)
//            intent.putExtra(NAME, name)
//            intent.putExtra(IMAGE, image)
//            return intent
//        }
//    }
