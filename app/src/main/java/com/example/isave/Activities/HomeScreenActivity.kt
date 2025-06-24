// HomeScreenActivity.kt
package com.example.isave.Activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.isave.Adapters.ExpenseAdapter
import com.example.isave.DatabaseHelpers.ExpenseDatabaseHelper
import com.example.isave.R
import com.example.isave.databinding.ActivityHomeScreenBinding

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeScreenBinding
    private lateinit var expenseDb: ExpenseDatabaseHelper
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.topAppBar))


        expenseDb = ExpenseDatabaseHelper(this)
        val expenses = expenseDb.getAllExpenses()
        expenseAdapter = ExpenseAdapter(expenses, this)

        binding.homeTransactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.homeTransactionsRecyclerView.adapter = expenseAdapter

        binding.addTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionScreen::class.java))
        }


        binding.homeBtn.setOnClickListener {
        }

        binding.moreBtn.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        binding.categoriesBtn.setOnClickListener {
            startActivity(Intent(this, Categories::class.java))
        }

        binding.transactionsBtn.setOnClickListener {
            startActivity(Intent(this, TransactionsActivity::class.java))
        }
    }


    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (menu.javaClass.simpleName == "MenuBuilder") {
            try {
                val method = menu.javaClass.getDeclaredMethod(
                    "setOptionalIconsVisible", Boolean::class.javaPrimitiveType
                )
                method.isAccessible = true
                method.invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isDark = prefs.getBoolean("dark_mode", false)
        menu?.findItem(R.id.action_toggle_dark)?.isChecked = isDark
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_dark -> {
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                val isDark = !item.isChecked

                prefs.edit().putBoolean("dark_mode", isDark).apply()

                AppCompatDelegate.setDefaultNightMode(
                    if (isDark) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )

                recreate()

                item.isChecked = isDark
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        // Always show overflow icon if present
        return super.onPrepareOptionsMenu(menu)
    }



}


