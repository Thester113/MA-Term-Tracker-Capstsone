package com.example.wgutscheduler.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wgutscheduler.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class LoginActivity : AppCompatActivity() {
    private lateinit var btnSubmit: Button
    private lateinit var userName: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnSubmit = findViewById(R.id.btn_submit)
        userName =findViewById(R.id.et_user_name)
        etPassword = findViewById(R.id.et_password)
        btnSubmit.setOnClickListener {
            val intent = Intent(applicationContext, MainPage::class.java)
            startActivity(intent)
        }
        val userName = userName.text
        val password = etPassword.text
        Toast.makeText(this@LoginActivity, userName, Toast.LENGTH_LONG).show()

    }
}




