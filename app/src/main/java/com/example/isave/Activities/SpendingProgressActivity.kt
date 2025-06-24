package com.example.isave.Activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.isave.DatabaseHelpers.ExpenseDatabaseHelper
import com.example.isave.DatabaseHelpers.UserDatabaseHelper
import com.example.isave.Models.Badge
import com.example.isave.Models.UserBadge
import com.example.isave.R
import com.example.isave.Utils.BadgeConstants
import com.example.isave.databinding.ActivitySpendingProgressBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class SpendingProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpendingProgressBinding
    private lateinit var expenseDatabaseHelper: ExpenseDatabaseHelper
    private lateinit var userDatabaseHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySpendingProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expenseDatabaseHelper = ExpenseDatabaseHelper(this)
        userDatabaseHelper = UserDatabaseHelper(this)

        val (minGoal, maxGoal) = userDatabaseHelper.getSpendingGoals()
        val totalSpending = expenseDatabaseHelper.getTotalSpendingForPastMonth()
        val isWithinGoals = totalSpending in minGoal..maxGoal

        // Show status text
        binding.spendingStatusText.text = when {
            totalSpending < minGoal -> "You're under your minimum goal!"
            totalSpending > maxGoal -> "You've exceeded your maximum goal!"
            else -> "You're within your spending goals!"
        }

        // Set up bar chart
        val barEntries = mutableListOf<BarEntry>().apply {
            add(BarEntry(0f, minGoal.toFloat()))
            add(BarEntry(1f, maxGoal.toFloat()))
            add(BarEntry(2f, totalSpending.toFloat()))
        }

        val barDataSet = BarDataSet(barEntries, "Spending Goals").apply {
            setColors(Color.BLUE, Color.GREEN, Color.RED)
        }

        binding.barChart.data = BarData(barDataSet)
        binding.barChart.description.isEnabled = false
        binding.barChart.animateY(1000)

        // Set progress bar
        val progressPercentage = ((totalSpending - minGoal) / (maxGoal - minGoal)) * 100
        binding.progressBar.progress = progressPercentage.toInt()
        binding.progressBar.max = 100

        // Check if within goal and award badge
        if (isWithinGoals && !expenseDatabaseHelper.hasBadge("budget_goal_met")) {
            awardBudgetGoalBadge("budget_goal_met")
        }
    }

    private fun awardBudgetGoalBadge(badgeId: String) {
        val badge = BadgeConstants.getBadgeById(badgeId)
        badge?.let {
            val userBadge = UserBadge(badgeId = it.id, earnedDate = System.currentTimeMillis())
            val insertedId = expenseDatabaseHelper.insertUserBadge(userBadge)
            if (insertedId != -1L) {
                showBudgetGoalAwardPopUp(it)
            }
        }
    }

    private fun showBudgetGoalAwardPopUp(badge: Badge) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_badge_award, null)

        val badgeIcon: ImageView = dialogView.findViewById(R.id.badgeIcon)
        val badgeTitle: TextView = dialogView.findViewById(R.id.badgeTitle)
        val badgeMessage: TextView = dialogView.findViewById(R.id.badgeMessage)

        val okButton: Button = dialogView.findViewById(R.id.okButton)

        badgeIcon.setImageResource(badge.iconResId)
        badgeTitle.text = badge.name
        badgeMessage.text = "${badge.description}\n\nKeep up the great work!"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //set listener for the "OK" button

        okButton.setOnClickListener {
            dialog.dismiss() //just dismiss the pop-up, user stays on the current screen
        }
        dialog.show()
    }
}
