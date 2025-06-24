package com.example.isave.DatabaseHelpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log // Added for logging
import com.example.isave.Classes.Expense
import com.example.isave.Classes.MonthlyTotals
import com.example.isave.Models.UserBadge
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ExpenseDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "expensesDB"
        private const val DATABASE_VERSION = 4 // <--- **IMPORTANT: INCREMENT THIS!**
        private const val TABLE_NAME = "expensesTable"
        private const val COLUMN_ID = "id" // This is the column name in the DB
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_PICTURE = "picture"

        // New constants for user_badges table (if not already present)
        private const val TABLE_USER_BADGES = "user_badges"
        private const val COLUMN_USER_BADGE_ID = "_id" // Or just 'id' if that's your primary key name in the table
        private const val COLUMN_BADGE_ID = "badge_id"
        private const val COLUMN_EARNED_DATE = "earned_date"

        // NEW constants for daily_logging table
        private const val TABLE_DAILY_LOGGING = "daily_logging"
        private const val COLUMN_LOGGING_DATE = "log_date" // Stores the date a log occurred (yyyy-MM-dd)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createExpenseTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_AMOUNT REAL,
                $COLUMN_PICTURE BLOB
            )
        """.trimIndent()

        val createBadgesTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_USER_BADGES (
                $COLUMN_USER_BADGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_BADGE_ID TEXT NOT NULL,
                $COLUMN_EARNED_DATE INTEGER
            )
        """.trimIndent()

        // NEW: Create daily_logging table
        val createDailyLoggingTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_DAILY_LOGGING (
                $COLUMN_LOGGING_DATE TEXT PRIMARY KEY
            )
        """.trimIndent()


        db?.execSQL(createExpenseTable)
        db?.execSQL(createBadgesTable)
        db?.execSQL(createDailyLoggingTable) // Execute the new table creation
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("DBHelper", "Upgrading DB from version $oldVersion to $newVersion")
        // Drop existing tables
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER_BADGES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_DAILY_LOGGING") // Drop the new table on upgrade too

        // Recreate all tables
        onCreate(db)
    }

    fun addExpense(expense: Expense) {
        val db = writableDatabase
        val values = ContentValues().apply {
            // Ensure you're putting the correct column names and values
            // Remember expense.expenseId is the property name in your data class
            // but COLUMN_ID is the database column name ("id")
            // For new expenses, don't put the ID, let DB auto-increment.
            // If you're updating, you'd include it. For add, exclude.

            put(COLUMN_CATEGORY, expense.category)
            put(COLUMN_DESCRIPTION, expense.description)
            put(COLUMN_DATE, expense.date)
            put(COLUMN_AMOUNT, expense.amount)
            put(COLUMN_PICTURE, expense.photoExpense)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
        // IMPORTANT: Call logDailyExpense after adding an expense
        // Use the date from the expense object
        Log.d("DBHelper", "Expense added. Logging daily expense for date: ${expense.date}")
        logDailyExpense(expense.date)
    }

    fun deleteExpense(expenseId: Int): Int {
        val db = writableDatabase
        val rowsAffected = db.delete(
            TABLE_NAME,
            "$COLUMN_ID = ?", // This uses the DB column name "id"
            arrayOf(expenseId.toString())
        )
        db.close()
        Log.d("DBHelper", "Deleted $rowsAffected rows for expense ID: $expenseId")
        return rowsAffected
    }

    fun getAllExpenses(): List<Expense> {
        val db = readableDatabase
        val expenses = mutableListOf<Expense>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        while (cursor.moveToNext()) {
            // When retrieving, use the COLUMN_ID constant for the database column name
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)) // Getting DB column "id"
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
            val picture = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PICTURE))
            // Map it to your Expense data class's expenseId property
            expenses.add(Expense(id, category, description, date, amount, picture))
        }

        cursor.close()
        db.close()
        Log.d("DBHelper", "Retrieved ${expenses.size} expenses.")
        return expenses
    }

    fun getTotals(): List<MonthlyTotals> {
        val db = readableDatabase
        val totals = mutableListOf<MonthlyTotals>()
        val query = "SELECT $COLUMN_CATEGORY, SUM($COLUMN_AMOUNT) AS total_amount FROM $TABLE_NAME GROUP BY $COLUMN_CATEGORY"
        val cursor = db.rawQuery(query, null)

        var i = 0
        while (cursor.moveToNext()) {
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount"))
            totals.add(MonthlyTotals(i, category, amount))
            i++
        }

        cursor.close()
        db.close()
        return totals
    }

    fun getTotalSpendingForPastMonth(): Double {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val startDate = formatDate(calendar.time)
        val endDate = formatDate(Calendar.getInstance().time)
        return getTotalSpendingBetweenDates(startDate, endDate)
    }

    private fun getTotalSpendingBetweenDates(startDate: String, endDate: String): Double {
        val db = readableDatabase
        val query = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_NAME WHERE $COLUMN_DATE BETWEEN ? AND ?"
        val cursor = db.rawQuery(query, arrayOf(startDate, endDate))

        val total = if (cursor.moveToFirst()) cursor.getDouble(0) else 0.0

        cursor.close()
        db.close()
        return total
    }

    private fun formatDate(date: java.util.Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getBudgetGoalForCurrentPeriod(): Double {
        // Placeholder: You can make this configurable later, e.g., from user settings
        return 2000.00
    }

    fun getTotalExpensesForCurrentPeriod(): Double {
        val db = readableDatabase
        var totalAmount = 0.0

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of the current month
        val startDate = formatDate(calendar.time)

        calendar.add(Calendar.MONTH, 1) // Move to next month
        calendar.set(Calendar.DAY_OF_MONTH, 1) // Set to first day of next month
        calendar.add(Calendar.DAY_OF_YEAR, -1) // Go back one day to get last day of current month
        val endDate = formatDate(calendar.time)

        val query = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_NAME WHERE $COLUMN_DATE BETWEEN ? AND ?"
        val cursor = db.rawQuery(query, arrayOf(startDate, endDate))

        if (cursor.moveToFirst()) {
            totalAmount = cursor.getDouble(0)
        }

        cursor.close()
        db.close()
        Log.d("DBHelper", "Total expenses for current period ($startDate to $endDate): $totalAmount")
        return totalAmount
    }

    // --------------------------
    // Daily Logging Methods (for streaks) - NEW/UPDATED
    // --------------------------
    fun logDailyExpense(dateString: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LOGGING_DATE, dateString)
        }
        // Using insertWithOnConflict to handle cases where date already exists
        // CONFLICT_IGNORE means if a row with the same primary key (log_date) exists,
        // it just ignores the insertion, preventing duplicates.
        val result = db.insertWithOnConflict(TABLE_DAILY_LOGGING, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        db.close()
        Log.d("DBHelper", "Logged daily expense for $dateString. Result: $result")
    }

    fun getConsecutiveLoggingDays(): Int {
        val db = readableDatabase
        // Select all log dates, ordered descending (most recent first)
        val cursor = db.rawQuery("SELECT $COLUMN_LOGGING_DATE FROM $TABLE_DAILY_LOGGING ORDER BY $COLUMN_LOGGING_DATE DESC", null)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        var consecutiveDays = 0

        cursor.use {
            if (it.moveToFirst()) {
                var previousDate: Calendar? = null // To compare with the current date in loop

                // Get today's date (beginning of loop)
                calendar.time = Date()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val today = calendar.clone() as Calendar

                // Check if the first entry is today or yesterday (start of streak)
                val firstDateString = it.getString(it.getColumnIndexOrThrow(COLUMN_LOGGING_DATE))
                try {
                    calendar.time = dateFormat.parse(firstDateString)!!
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    // Check if the most recent logged date is today or yesterday
                    val diff = (today.timeInMillis - calendar.timeInMillis) / (1000 * 60 * 60 * 24)
                    if (diff.toInt() == 0 || diff.toInt() == 1) { // Today or Yesterday
                        consecutiveDays = 1 // Start streak count
                        previousDate = calendar.clone() as Calendar
                    } else {
                        // If the most recent log isn't today or yesterday, streak is 0
                        return 0
                    }

                } catch (e: Exception) {
                    Log.e("DBHelper", "Error parsing date: $firstDateString", e)
                    return 0
                }

                // Iterate through remaining dates (starting from second entry)
                while (it.moveToNext()) {
                    val dateString = it.getString(it.getColumnIndexOrThrow(COLUMN_LOGGING_DATE))
                    try {
                        calendar.time = dateFormat.parse(dateString)!!
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)

                        // Calculate expected date for a consecutive streak
                        val expectedPreviousDate = previousDate?.clone() as Calendar
                        expectedPreviousDate.add(Calendar.DAY_OF_YEAR, -1) // Expecting date to be one day before previous

                        // If the current date is the same as the previous (duplicate log for same day, already counted)
                        // OR if the current date is exactly one day before the previous date (consecutive)
                        if (calendar.equals(previousDate) || calendar.equals(expectedPreviousDate)) {
                            if (!calendar.equals(previousDate)) { // Only increment if it's a new consecutive day
                                consecutiveDays++
                            }
                            previousDate = calendar.clone() as Calendar // Update previous date
                        } else {
                            // A gap was found, break the streak
                            break
                        }
                    } catch (e: Exception) {
                        Log.e("DBHelper", "Error parsing date in streak: $dateString", e)
                        break // Break on error
                    }
                }
            }
        }
        db.close()
        Log.d("DBHelper", "Calculated consecutive days: $consecutiveDays")
        return consecutiveDays
    }


    // --------------------------
    // Badge-related Methods
    // --------------------------

    fun hasBadge(badgeId: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_USER_BADGES WHERE $COLUMN_BADGE_ID = ?",
            arrayOf(badgeId)
        )
        val exists = cursor.moveToFirst() && cursor.getInt(0) > 0
        cursor.close()
        db.close()
        return exists
    }

    fun insertUserBadge(userBadge: UserBadge): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BADGE_ID, userBadge.badgeId)
            put(COLUMN_EARNED_DATE, userBadge.earnedDate)
        }
        val result = db.insert(TABLE_USER_BADGES, null, values)
        db.close()
        return result
    }

    fun getAllUserBadges(): List<UserBadge> {
        val userBadges = mutableListOf<UserBadge>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USER_BADGES", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_BADGE_ID))
            val badgeId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BADGE_ID))
            val earnedDate = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EARNED_DATE))
            userBadges.add(UserBadge(id, badgeId, earnedDate))
        }

        cursor.close()
        db.close()
        return userBadges
    }
}