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
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.FirebaseDatabase
import java.sql.Types.NULL


//this activity is just used to upload user information to the database
const val EMULATORS_ENABLED = false
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
        binding = ActivityAfterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
        }else{
        binding.profileimage.setOnClickListener {
            checkPermissionForImage()
        }
        binding.savebtn.setOnClickListener {
            val name = binding.nameedt.text.toString()
            if (!::downloadUri.isInitialized) {
                Toast.makeText(this, "Image not provided!", Toast.LENGTH_SHORT).show()
            } else if (name.isEmpty()) {
                Toast.makeText(this, "Name not provided!", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(name, downloadUri, downloadUri, auth.uid!!)
                database.collection("users").document(auth.uid!!).set(user).addOnSuccessListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    binding.savebtn.isEnabled = true
                    Log.d("NOT DONE", "USERS NOT CREATED")
                }
            }
        }
    }
    }

    override fun onBackPressed() {

    }
    private fun isLoggedIn():Boolean{
        val userRef = FirebaseFirestore.getInstance().collection("users")
        val doesExist=userRef.whereEqualTo("uid","${auth.uid}")
        var hasLoggedInPreviously:Boolean=false
        val doesExistSnapshot=doesExist.get()

        Log.d("User id is","${auth.uid}")
        if(doesExistSnapshot.result?.isEmpty == false){
          hasLoggedInPreviously=true
        }
       return hasLoggedInPreviously
    }
    private fun checkPermissionForImage() {
        //A process to check for all the permissions wehn we click on the image preview
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
        //this is an intent used to tell to pick the image from the gallery
        val intent=Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(
            intent,1000
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //here we check whether both the permissions has been granted or not
        if(resultCode==Activity.RESULT_OK && requestCode==1000){
            data?.data.let {
                binding.profileimage.setImageURI(it)
                if (it != null) {
                    uploadImage(it)
                }
            }
        }
    }
    //uploading image to firebase
    private fun uploadImage(filePath: Uri) {
binding.savebtn.isEnabled=false
        //here we make a reference for storage and after child we create a folder of uploads and then we use authentication id as an
        //id for the image to get uploaded
        val ref=storage.reference.child("uploads/"+auth.uid.toString())
        val uploadtask=ref.putFile(filePath)
        uploadtask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>> { task ->
            if(!task.isSuccessful){
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
            }else{
                binding.savebtn.isEnabled=true
            }
        }.addOnFailureListener {
            Log.d("TAG","NOT SUCCESS")
        }
    }
}
//
//private fun startUpload(filePath: Uri) {
//    nextBtn.isEnabled = false
//    val ref = storage.reference.child("uploads/" + auth.uid.toString())
//    val uploadTask = ref.putFile(filePath)
//    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
//        if (!task.isSuccessful) {
//            task.exception?.let {
//                throw it
//            }
//        }
//        return@Continuation ref.downloadUrl
//    }).addOnCompleteListener { task ->
//        if (task.isSuccessful) {
//            downloadUrl = task.result.toString()
//            nextBtn.isEnabled = true
//        } else {
//            nextBtn.isEnabled = true
//            // Handle failures
//        }
//    }.addOnFailureListener {
//
//    }
//}
//
//}