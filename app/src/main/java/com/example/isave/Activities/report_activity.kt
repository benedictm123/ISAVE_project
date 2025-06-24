package com.example.isave.Activities

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.isave.Classes.Expense
import com.example.isave.DatabaseHelpers.ExpenseDatabaseHelper
import com.example.isave.R
import com.example.isave.databinding.ActivityReportBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class report_activity : AppCompatActivity() {
    private lateinit var viewBinding:ActivityReportBinding
    private lateinit var expenseDatabaseHelper: ExpenseDatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBinding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets



        }
        expenseDatabaseHelper= ExpenseDatabaseHelper(this)
        var pieChart = viewBinding.pieChart

        val categoryTotals = expenseDatabaseHelper.getTotals()
        /*
        * CategoryTotal(4000,Food),
        * CategoryTotal(2000,Transport),
        * CategoryTotal(4000, Maintenance)
        *
        * PieEntry(3000,"Food")
        * */
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
        pieChart.centerText = "Income"
        pieChart.animateY(1000)
    }
}
