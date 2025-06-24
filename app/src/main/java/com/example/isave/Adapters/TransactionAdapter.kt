// Path: app/java/com/example/isave/Adapters/TransactionAdapter.kt

package com.example.isave.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.isave.Classes.Expense
import com.example.isave.R

// NEW: Define an interface to communicate delete events back to the activity
class TransactionAdapter(
    private var expenses: List<Expense>,
    private val listener: OnItemDeleteListener // Pass the listener here
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    // Interface definition
    interface OnItemDeleteListener {
        fun onDeleteClick(expenseId: Int)
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        init {
            // NEW: Set a LongClickListener on the item view
            itemView.setOnLongClickListener {
                // Get the position of the item that was long-pressed
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // Ensure position is valid
                    val expenseToDelete = expenses[position]
                    // Call the onDeleteClick method on the listener (which is TransactionsActivity)
                    listener.onDeleteClick(expenseToDelete.expenseId)
                    true // Consume the long-click event
                } else {
                    false
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val expense = expenses[position]
        holder.categoryTextView.text = expense.category
        holder.descriptionTextView.text = expense.description
        holder.amountTextView.text = String.format("R%.2f", expense.amount) // Format as currency
        holder.dateTextView.text = expense.date
    }

    override fun getItemCount(): Int = expenses.size

    // Method to update the data in the adapter and refresh the RecyclerView
    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged() // Notifies the adapter that the dataset has changed
    }
}