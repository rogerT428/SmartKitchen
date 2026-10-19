package com.example.smartkitchen

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.util.Patterns
import android.widget.Toast

class RegisterActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val name = findViewById<EditText>(R.id.etName)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val confirm = findViewById<EditText>(R.id.etConfirmPassword)
        val register = findViewById<Button>(R.id.btnRegister)
        val login = findViewById<TextView>(R.id.tvGoLogin)

        login.setOnClickListener {
            finish()
        }

        register.setOnClickListener {
            val nameValue = name.text.toString().trim()
            val emailValue = email.text.toString().trim()
            val passwordValue = password.text.toString()
            val confirmValue = confirm.text.toString()

            if (nameValue.isEmpty()) {
                name.error = "Nama wajib diisi"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
                email.error = "Email tidak valid"
                return@setOnClickListener
            }

            if (passwordValue.length < 6) {
                password.error = "Minimal 6 karakter"
                return@setOnClickListener
            }

            if (passwordValue != confirmValue) {
                confirm.error = "Password tidak sama"
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Form valid. Autentikasi belum terhubung.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}