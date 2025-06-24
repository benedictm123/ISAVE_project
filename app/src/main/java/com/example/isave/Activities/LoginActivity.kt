// LoginActivity.kt
package com.example.isave.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.isave.DatabaseHelpers.UserDatabaseHelper
import com.example.isave.databinding.ActivityLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userDatabaseHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDatabaseHelper = UserDatabaseHelper(this)

        binding.goToRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.LogInButton.setOnClickListener {
            val username = binding.UsernameEditText.text.toString().trim()
            val password = binding.PasswordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val database = FirebaseFirestore.getInstance()
            database.collection("users")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener { document ->
                    if (!document.isEmpty) {
                        Toast.makeText(this, "Log in Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeScreenActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
