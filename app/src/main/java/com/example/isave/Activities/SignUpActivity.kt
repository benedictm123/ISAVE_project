package com.example.isave.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.isave.Classes.User
import com.example.isave.DatabaseHelpers.UserDatabaseHelper
import com.example.isave.R
import com.example.isave.databinding.ActivitySignUpBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.hashMapOf

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySignUpBinding
    private lateinit var userDatabase:UserDatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userDatabase = UserDatabaseHelper(this)

        binding.SignUpButton.setOnClickListener {
            val username = binding.UsernameEditTextSignUp.text.toString().trim()
            val password = binding.PasswordEditTextSignUp.text.toString().trim()
            val passwordConfirm = binding.PasswordConfirmSignUp.text.toString().trim()

            //create an instance for the firebase firestore
            val database  = FirebaseFirestore.getInstance();

            val new_user = hashMapOf(
                "username" to username,
                "password" to password,
                "passwordConfirm" to passwordConfirm
            )

            //store in the collection in firebase firestore
            database.collection("users")
                .add(new_user)
                .addOnSuccessListener { documentReferce ->
                    //show success message
                    Toast.makeText(this, "User registered", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{ exeption ->
                    //show error message if fail to insert
                    Toast.makeText(this, "Error on" + exeption, Toast.LENGTH_SHORT).show()
                }
        }
    }
}