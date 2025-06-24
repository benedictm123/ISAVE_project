package com.example.isave.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isave.Adapters.CategoryAdapter
import com.example.isave.DatabaseHelpers.CategoriesDatabaseHelper
import com.example.isave.R
import com.example.isave.databinding.ActivityCategoriesOptionsBinding

class CategoriesOptions : AppCompatActivity() {
    private lateinit var binding:ActivityCategoriesOptionsBinding
    private lateinit var db: CategoriesDatabaseHelper
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCategoriesOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = CategoriesDatabaseHelper(this)
        categoryAdapter = CategoryAdapter(db.getCategories(),this)
        binding.expenseCategoriesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.expenseCategoriesRecyclerView.adapter = categoryAdapter

        binding.addNewCategoryBtn.setOnClickListener {
            val intent = Intent(this,AddNewCategory::class.java)
            startActivity(intent)
        }
        binding.backToCatMenuBtn.setOnClickListener {
            val intent = Intent(this,Categories::class.java)
            startActivity(intent)
        }



    }
    override fun onResume() {
        super.onResume()
        categoryAdapter.refreshData(db.getCategories())
    }

}