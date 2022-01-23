package com.pdinc.chitchat.Activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.pdinc.chitchat.Modals.User
import com.pdinc.chitchat.R
import com.pdinc.chitchat.databinding.ActivitySettingsBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Callback
import java.lang.Exception


class SettingsActivity : AppCompatActivity() {
    private val auth by lazy {
        FirebaseAuth.getInstance().uid
    }
    val firebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val firebaseDB by lazy {
        FirebaseDatabase.getInstance()
    }
    lateinit var currentUser:User
    private lateinit var binding:ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_settings)
//        binding.dpIV.setIma
        FirebaseFirestore.getInstance().collection("users").document(auth!!).get().addOnSuccessListener {
            currentUser = it.toObject(User::class.java)!!

            val image = currentUser.thumbImage
            val imageUrl: String = image
            if (imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(image)
                    .into(binding.dpIV, object : Callback {
                        override fun onSuccess() {
                            Toast.makeText(
                                this@SettingsActivity,
                                "Looking Good",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onError(e: Exception?) {
                            Toast.makeText(
                                this@SettingsActivity,
                                "Sorry Some Error Occur",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            } else {
                Toast.makeText(
                    this@SettingsActivity,
                    "Sorry Some Error Occured",
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.nameTv.text = currentUser.name
            binding.statusTv.text = currentUser.status
        }
       // val s: SpannableString =SpannableString(auth.currentUser?.displayName)
       // binding.NameTextEt
//        binding.changeNameTab.setOnClickListener{
//            updateName()
//        }
    }
//    private fun updateName() {
//        val nameEt:EditText=findViewById(R.id.nameedt)
//        auth.currentUser!!.displayName
//    }
}