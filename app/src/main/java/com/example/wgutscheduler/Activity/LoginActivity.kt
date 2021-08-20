package com.example.wgutscheduler.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wgutscheduler.DB.DataBase
import com.example.wgutscheduler.Entity.User
import com.example.wgutscheduler.R
import com.example.wgutscheduler.Utilities.hash



class LoginActivity : AppCompatActivity() {
    lateinit var db: DataBase
    private lateinit var btnSubmit: Button
    private lateinit var userName: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        db = DataBase.getInstance(applicationContext)!!
        if (db.UserDao()?.hasTestUser() == false) {
            val user = User()
            user.user_name = "test"
            user.user_password = "098F6BCD4621D373CADE4E832627B4F6"
            db.UserDao()?.insertUser(user)
        }
        btnSubmit = findViewById(R.id.btn_submit)
        userName = findViewById(R.id.et_user_name)
        etPassword = findViewById(R.id.et_password)
        btnReset = findViewById(R.id.btn_reset)
        btnSubmit.setOnClickListener {
            val userName = userName.text.toString()
            val password = etPassword.text.toString()
            val user = db.UserDao()?.getUser(userName = userName)
            if (user != null && hash(password) == user.user_password) {
                val intent = Intent(applicationContext, MainPage::class.java)
                startActivity(intent)
                finish()

            }
            Toast.makeText(this@LoginActivity, userName, Toast.LENGTH_LONG).show()
        }
        btnReset.setOnClickListener {
            userName.setText("")
            etPassword.setText("")

        }


    }

}




