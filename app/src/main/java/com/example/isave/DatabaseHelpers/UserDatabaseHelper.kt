package com.example.isave.DatabaseHelpers

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.isave.Classes.User

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "user"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME(" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT," +
                "$COLUMN_PASSWORD TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun createUser(user: User) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, user.username)
            put(COLUMN_PASSWORD, user.password)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getUsers(username: String, password: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    // INITIALIZING SHAREDPREFERENCES
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

    // SAVE SPENDING GOALS TO SHAREDPREFERENCES
    fun saveSpendingGoals(minGoal: Double, maxGoal: Double) {
        sharedPreferences.edit()
            .putFloat("min_spending_goal", minGoal.toFloat())
            .putFloat("max_spending_goal", maxGoal.toFloat())
            .apply()
    }

    // RETRIEVES DATA ON SPENDING GOALS FROM SHAREDPREFERENCES
    fun getSpendingGoals(): Pair<Double, Double> {
        val minGoal = sharedPreferences.getFloat("min_spending_goal", 0f).toDouble()
        val maxGoal = sharedPreferences.getFloat("max_spending_goal", 0f).toDouble()
        return Pair(minGoal, maxGoal)
    }
}