package com.example.isave.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log // Added for debugging
import android.widget.Toast // Added for user feedback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog // Added for confirmation dialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isave.Adapters.TransactionAdapter // CORRECTED: Ensure this import is for TransactionAdapter
import com.example.isave.Classes.Expense
import com.example.isave.DatabaseHelpers.ExpenseDatabaseHelper
import com.example.isave.Models.UserBadge // Added for badge logic
import com.example.isave.R
import com.example.isave.Utils.BadgeConstants // Added for badge logic
import com.example.isave.databinding.ActivityTransactionsBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.text.SimpleDateFormat // Added for badge date formatting
import java.util.Calendar // Added for badge logic
import java.util.Date // Added for badge logic
import java.util.Locale // Added for badge date formatting

class TransactionsActivity : AppCompatActivity(), TransactionAdapter.OnItemDeleteListener {

    private lateinit var binding: ActivityTransactionsBinding
    private lateinit var expenseDatabaseHelper: ExpenseDatabaseHelper
    private lateinit var transactionAdapter: TransactionAdapter // CORRECTED: Use transactionAdapter
    private var allExpenses: List<Expense> = listOf() // Store all expenses to filter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets for system bars if needed (from previous template)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets -> // Changed binding.main to binding.root
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.goHomeTransactions.setOnClickListener {
            val intent = Intent(this,HomeScreenActivity::class.java)
            startActivity(intent)
        }
        binding.newTransactions.setOnClickListener {
            val intent = Intent(this,AddTransactionScreen::class.java)
            startActivity(intent)
        }
        binding.goToCategoriesTransactions.setOnClickListener {
            val intent = Intent(this,Categories::class.java)
            startActivity(intent)
        }
        binding.goToMoreTransactions.setOnClickListener {
            val intent = Intent(this,MenuActivity::class.java)
            startActivity(intent)
        }

        expenseDatabaseHelper = ExpenseDatabaseHelper(this)

        // CORRECTED: Initialize the adapter with TransactionAdapter and pass 'this' as the listener
        transactionAdapter = TransactionAdapter(allExpenses, this)
        binding.transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.transactionsRecyclerView.adapter = transactionAdapter

        // Load expenses initially
        loadExpenses()

        // Pie chart setup (your existing code)
        var pieChart = binding.pieChart

        val categoryTotals = expenseDatabaseHelper.getTotals()
        var entries = mutableListOf<PieEntry>()
        var n = 0
        while (n < categoryTotals.count()){
            entries.add(PieEntry(categoryTotals[n].amount.toFloat(),categoryTotals[n].category))
            n++
        }
        val dataSet = PieDataSet(entries, "Categories")
        dataSet.setColors(
            Color.BLUE,
            Color.MAGENTA,
            Color.GREEN,
            Color.CYAN,
            Color.RED,
            Color.MAGENTA,
            Color.DKGRAY
        )
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 16f

        val data = PieData(dataSet)

        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.centerText = "Income" // Consider if "Income" is the correct text here for expenses
        pieChart.animateY(1000)
    }

    // Add this onResume to refresh data when returning to this activity
    override fun onResume() {
        super.onResume()
        loadExpenses() // Reload expenses to update the RecyclerView after AddTransactionScreen
        checkAndAwardDailyStreakBadges() // Re-check badges (important after adding/deleting transactions)
    }

    // --- NEW/UPDATED: Helper method to load and update expenses ---
    private fun loadExpenses() {
        allExpenses = expenseDatabaseHelper.getAllExpenses()
        // Make sure the adapter is updated correctly after loading
        if (::transactionAdapter.isInitialized) {
            transactionAdapter.updateData(allExpenses) // Call updateData on the adapter
        }
    }

    // --- NEW: Implementation of OnItemDeleteListener method ---
    override fun onDeleteClick(expenseId: Int) {
        // Show a confirmation dialog before deleting
        AlertDialog.Builder(this)
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense? This action cannot be undone.")
            .setPositiveButton("Delete") { dialog, _ ->
                val rowsAffected = expenseDatabaseHelper.deleteExpense(expenseId)
                if (rowsAffected > 0) {
                    Toast.makeText(this, "Expense deleted successfully", Toast.LENGTH_SHORT).show()
                    loadExpenses() // Reload expenses to update the RecyclerView
                    // After deleting an expense, re-check daily streak badges
                    checkAndAwardDailyStreakBadges()
                } else {
                    Toast.makeText(this, "Failed to delete expense", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    // --- End NEW ---

    // --- NEW: Badge checking methods (copied from previous instructions) ---
    private fun checkAndAwardDailyStreakBadges() {
        val consecutiveDays = expenseDatabaseHelper.getConsecutiveLoggingDays()
        Log.d("Badges", "Consecutive logging days: $consecutiveDays")

        when (consecutiveDays) {
            3 -> awardBadge("logger_streak_3")
            7 -> awardBadge("logger_streak_7")
            30 -> awardBadge("logger_streak_30")
            // Add more cases if you have other streak badges
        }
    }

    private fun awardBadge(badgeId: String) {
        if (!expenseDatabaseHelper.hasBadge(badgeId)) {
            val badge = BadgeConstants.getBadgeById(badgeId)
            badge?.let {
                val userBadge = UserBadge(badgeId = it.id, earnedDate = System.currentTimeMillis())
                val insertedId = expenseDatabaseHelper.insertUserBadge(userBadge)
                if (insertedId != -1L) {
                    Toast.makeText(this, "Badge Earned: ${it.name}!", Toast.LENGTH_LONG).show()
                    // Optional: You could show a custom dialog here like the budget one
                }
            }
        }
    }
    // --- End NEW Badge methods ---
}