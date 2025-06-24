package com.example.isave.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isave.Activities.Categories
import com.example.isave.Classes.Category
import com.example.isave.R

class CategoryAdapter(private var categories: List<Category>,context: Context):
RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val categoryTitle = itemView.findViewById<TextView>(R.id.categoryHolderText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_layout,parent,false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount()= categories.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryTitle.text = category.description
    }

    fun refreshData(newCategories:List<Category>){
        categories = newCategories
        notifyDataSetChanged()
    }

}