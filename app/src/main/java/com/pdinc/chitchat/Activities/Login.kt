package com.pdinc.chitchat.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pdinc.chitchat.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var countryCode: String
    private lateinit var phoneNumber: String
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.phonenoet.addTextChangedListener {
            if (it != null) {
                binding.nextbtn.isEnabled = it.length == 10
            }
        }
        binding.nextbtn.setOnClickListener {
            checkNumber()
        }
    }

    private fun checkNumber() {
        countryCode = binding.ccpicker.selectedCountryCodeWithPlus
        phoneNumber = countryCode + binding.phonenoet.text.toString()
        confirmPhoneNumber()
    }
    private fun confirmPhoneNumber() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("Please confirm your Phone Number $phoneNumber\n"
                    + "Press OK to proceed or Edit to edit the number")
            setPositiveButton("OK") { _, _ ->
                showOtpActivity()
            }
            setNegativeButton("EDIT") { dialog, which ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }
    private fun showOtpActivity() {
        startActivity(Intent(this, OtpActivity::class.java).putExtra(phoneno, phoneNumber))
        finish()
    }
}
