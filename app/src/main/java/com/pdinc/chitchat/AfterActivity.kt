package com.pdinc.chitchat

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pdinc.chitchat.databinding.ActivityAfterBinding

class AfterActivity : AppCompatActivity() {
    lateinit var binding: ActivityAfterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityAfterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.profileimage.setOnClickListener {
            checkPermissionForImage()
        } }
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
            }
        }
    }
}