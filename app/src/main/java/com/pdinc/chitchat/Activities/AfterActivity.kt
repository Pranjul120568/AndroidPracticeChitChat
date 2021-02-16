package com.pdinc.chitchat.Activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.pdinc.chitchat.Modals.User
import com.pdinc.chitchat.databinding.ActivityAfterBinding

class AfterActivity : AppCompatActivity() {
    private val storage by lazy {
        Firebase.storage
    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    private val database by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var downloadUri:String
    private lateinit var binding: ActivityAfterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityAfterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.profileimage.setOnClickListener {
            checkPermissionForImage()
        }
        binding.savebtn.setOnClickListener {
            val name=binding.nameedt.text.toString()
            if(name.isNotEmpty()){
                Toast.makeText(this,"Name not provided!",Toast.LENGTH_SHORT).show()
            }
            else if(!::downloadUri.isInitialized){
                Toast.makeText(this,"Image not provided!",Toast.LENGTH_SHORT).show()
            }
            else{
                val user= User(name,downloadUri,downloadUri,auth.uid!!)
                database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener{
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
    private fun checkPermissionForImage() {
        if(checkSelfPermission(READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED &&
            (checkSelfPermission(WRITE_EXTERNAL_STORAGE))==PackageManager.PERMISSION_DENIED){
            val permission= arrayOf(READ_EXTERNAL_STORAGE)
            val permissionwrite= arrayOf(WRITE_EXTERNAL_STORAGE)

            requestPermissions(
                permission,
                1001
            )
            requestPermissions(
                permissionwrite,1002
            )
        }
        else{
            pickImageFromGallery()
        }
    }
    private fun pickImageFromGallery() {
        val intent=Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(
            intent,1000
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode==1000){
            data?.data.let {
                binding.profileimage.setImageURI(it)
                if (it != null) {
                    uploadImage(it)
                }
            }
        }
    }
    private fun uploadImage(it: Uri) {
binding.savebtn.isEnabled=false
        val ref=storage.reference.child("uploads/"+auth.uid.toString())
        val uploadtask=ref.putFile(it)
        uploadtask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>> { task ->
            if(task.isSuccessful){
                task.exception.let {
                    throw it!!
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener{ task ->
            binding.savebtn.isEnabled=true
            if(task.isSuccessful){
                downloadUri=task.result.toString()
                Log.i("URL","downloadurl: $downloadUri")
            }
        }
    }
}