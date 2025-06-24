package com.example.isave.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.isave.DatabaseHelpers.UserDatabaseHelper
import com.example.isave.databinding.ActivitySpendingGoalsBinding

class SpendingGoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpendingGoalsBinding
    private lateinit var userDatabaseHelper: UserDatabaseHelper

    companion object {
        private const val ERROR_INVALID_GOALS = "Please enter valid goals."
        private const val ERROR_EMPTY_FIELDS = "Please enter both minimum and maximum goals."
        private const val ERROR_POSITIVE_VALUES = "Goals must be positive numbers."
        private const val SUCCESS_SAVED_GOALS = "Spending goals saved successfully!"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpendingGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDatabaseHelper = UserDatabaseHelper(this)

        binding.saveGoalsButton.setOnClickListener {
            val minGoal = binding.minGoalInput.text.toString().toDoubleOrNull()
            val maxGoal = binding.maxGoalInput.text.toString().toDoubleOrNull()

            // Validate input fields
            if (binding.minGoalInput.text.isNullOrEmpty() || binding.maxGoalInput.text.isNullOrEmpty()) {
                Toast.makeText(this, ERROR_EMPTY_FIELDS, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate positive values
            if (minGoal == null || maxGoal == null || minGoal <= 0 || maxGoal <= 0) {
                Toast.makeText(this, ERROR_POSITIVE_VALUES, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate logical constraint
            if (minGoal >= maxGoal) {
                Toast.makeText(this, ERROR_INVALID_GOALS, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save spending goals
            userDatabaseHelper.saveSpendingGoals(minGoal, maxGoal)
            Toast.makeText(this, SUCCESS_SAVED_GOALS, Toast.LENGTH_SHORT).show()
            finish() // Close the activity after saving
        }
    }
}