package com.example.smartkitchen

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val login = findViewById<Button>(R.id.btnLogin)
        val register = findViewById<TextView>(R.id.tvGoRegister)

        register.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

        login.setOnClickListener {
            val emailValue = email.text.toString().trim()
            val passwordValue = password.text.toString()

            if (emailValue.isEmpty() || passwordValue.isEmpty()) {
                Toast.makeText(
                    this,
                    "Email dan password wajib diisi",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(emailValue).matches()) {
                email.error = "Format email tidak valid"
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Tampilan Login berhasil dibuat. Autentikasi belum terhubung.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}