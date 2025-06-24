package com.example.isave.Adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isave.Classes.Expense
import com.example.isave.R
import kotlin.math.exp

class ExpenseAdapter(private var expenses:List<Expense>, context: Context):
RecyclerView.Adapter<ExpenseAdapter.ExpensesViewHolder>(){
    class ExpensesViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val categoryTitle: TextView = itemView.findViewById(R.id.categoryTextHolder)
        val dateTextHolder: TextView = itemView.findViewById(R.id.dateTextHolder)
        val amount: TextView = itemView.findViewById(R.id.amountTextHolder)
        val expensePicture: ImageView = itemView.findViewById(R.id.expensePhotoHolder)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_layout,parent,false)
        return ExpensesViewHolder(view)
    }

    override fun getItemCount() = expenses.size

    override fun onBindViewHolder(holder: ExpensesViewHolder, position: Int) {
        val expense = expenses[position]
        val category = expense.category
        val date = expense.date
        val amount = expense.amount

        holder.categoryTitle.text = category
        holder.dateTextHolder.text = date
        holder.amount.text = "R $amount"
        holder.expensePicture.setImageBitmap(byteArrayToBitmap(expense.photoExpense))
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun refreshExpenses(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}