package com.example.isave.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isave.Activities.Categories
import com.example.isave.Adapters.CategoryAdapter.CategoryViewHolder
import com.example.isave.Classes.Expense
import com.example.isave.Classes.MonthlyTotals
import com.example.isave.R

class MonthlyCategoryTotalsAdapter(private var categories: List<MonthlyTotals>, context: Context):
    RecyclerView.Adapter<MonthlyCategoryTotalsAdapter.MonthlyCategoryViewHolder>(){

    class MonthlyCategoryViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val categoryDescription: TextView = itemView.findViewById(R.id.MonthlyCategoryTextView)
        val categoryTotal:TextView = itemView.findViewById(R.id.MonthlyTotalTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlyCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.monthly_categories_layout,parent,false)
        return MonthlyCategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: MonthlyCategoryViewHolder, position: Int) {
        val expense = categories[position]
        holder.categoryDescription.text = expense.category
        holder.categoryTotal.text = "R ${expense.amount}"
    }

    fun refreshItems(newItems:List<MonthlyTotals>){
        categories = newItems
        notifyDataSetChanged()
    }
}