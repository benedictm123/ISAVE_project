package com.example.isave.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.isave.DatabaseHelpers.CategoriesDatabaseHelper
import com.example.isave.R
import com.example.isave.databinding.ActivityCategoriesBinding

class Categories : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriesBinding
    private lateinit var db: CategoriesDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CategoriesDatabaseHelper(this)

        binding.HomeMenu.setOnClickListener {
            val intent = Intent(this,HomeScreenActivity::class.java)
            startActivity(intent)
        }
        binding.TrasactionsMenu.setOnClickListener {
            val intent = Intent(this,TransactionsActivity::class.java)
            startActivity(intent)
        }
        binding.newTransactionsMenu.setOnClickListener {
            val intent = Intent(this,TransactionsActivity::class.java)
            startActivity(intent)
        }
        binding.categoryTotalsMenu.setOnClickListener {
            val intent = Intent(this,MenuActivity::class.java)
            startActivity(intent)
        }
        binding.gotoCategoriesMenu.setOnClickListener {
            val intent = Intent(this,CategoriesOptions::class.java)
            startActivity(intent)
        }


    }
}