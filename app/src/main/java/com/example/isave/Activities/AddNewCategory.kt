package com.example.isave.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.isave.Classes.Category
import com.example.isave.DatabaseHelpers.CategoriesDatabaseHelper
import com.example.isave.R
import com.example.isave.databinding.ActivityAddNewCategoryBinding

class AddNewCategory : AppCompatActivity() {
    private lateinit var binding:ActivityAddNewCategoryBinding
    private lateinit var db: CategoriesDatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        db = CategoriesDatabaseHelper(this)

        binding = ActivityAddNewCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveCategoriesBtn.setOnClickListener {

            val description = binding.categoryTitleTxt.text.toString()
            if(description!=""){

                val category = Category(0,description)
                db.saveCategory(category)
                Toast.makeText(this,"${category} saved successfully",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, CategoriesOptions::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this,"Please enter a description",Toast.LENGTH_LONG).show()
            }

        }
        binding.backToCategoriesBtn.setOnClickListener {
            val intent = Intent(this,CategoriesOptions::class.java)
            startActivity(intent)
        }
        binding.cancelSaveCategoryBtn.setOnClickListener {
            val intent = Intent(this,CategoriesOptions::class.java)
            startActivity(intent)
        }
    }
}