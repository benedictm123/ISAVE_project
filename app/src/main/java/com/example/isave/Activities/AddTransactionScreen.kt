package com.example.isave.Activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.isave.Adapters.ExpenseAdapter
import com.example.isave.Classes.Expense
import com.example.isave.DatabaseHelpers.CategoriesDatabaseHelper
import com.example.isave.DatabaseHelpers.ExpenseDatabaseHelper
import com.example.isave.R
import com.example.isave.databinding.ActivityAddTransactionScreenBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionScreen : AppCompatActivity() {

    private lateinit var binding:ActivityAddTransactionScreenBinding
    private lateinit var CategoriesDb: CategoriesDatabaseHelper
    private lateinit var ExpenseDatabaseHelper:ExpenseDatabaseHelper
    private lateinit var categorySpinner:Spinner

    companion object{
        private lateinit var RecieptArray:ByteArray
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddTransactionScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.HomeAdd.setOnClickListener {
            val intent = Intent(this,HomeScreenActivity::class.java)
            startActivity(intent)
        }
        binding.TrasactionsAdd.setOnClickListener {
            val intent = Intent(this,TransactionsActivity::class.java)
            startActivity(intent)
        }
        binding.categoriesAdd.setOnClickListener {
            val intent = Intent(this,Categories::class.java)
            startActivity(intent)
        }
        binding.categoryTotalsAdd.setOnClickListener {
            val intent = Intent(this,MenuActivity::class.java)
            startActivity(intent)
        }


        CategoriesDb = CategoriesDatabaseHelper(this)
        ExpenseDatabaseHelper = ExpenseDatabaseHelper(this)
        categorySpinner = binding.categorySpinner


        binding.expensePhoto.visibility = View.INVISIBLE
        RecieptArray = imageToByteArray(binding.expensePhoto)

        val categories = mutableListOf<String>()
        val categoryObjects = CategoriesDb.getCategories()
        var i = 0
        while (i < categoryObjects.size){
            val description = categoryObjects[i].description
            categories.add(description)
            i++
        }

        val arrayAdapter = ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_item,categories)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = arrayAdapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                binding.categoryTextView.text = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val calendarBox = Calendar.getInstance()
        val dateBox = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
            calendarBox.set(Calendar.YEAR,year)
            calendarBox.set(Calendar.MONTH,month)
            calendarBox.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            setDate(calendarBox)
        }
        binding.addDateBtn.setOnClickListener {
            DatePickerDialog(binding.root.context,dateBox,calendarBox.get(Calendar.YEAR),calendarBox.get(
                Calendar.MONTH),calendarBox.get(Calendar.DAY_OF_MONTH)).show()

        }

        binding.addPhotoBtn.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(cameraIntent)
        }

        binding.CreateExpenseBtn.setOnClickListener {


            val category = binding.categoryTextView.text.toString()
            val date = binding.DateEditText.text.toString()
            val description = binding.DescriptionEditText.text.toString()
            val amount = binding.AmountEditText.text.toString()
            val expensePhoto = imageToByteArray(binding.expensePhoto)

            if(category == "" || amount == ""){
                Toast.makeText(this,"Please add an amount",Toast.LENGTH_SHORT).show()
            }else{


                val expenseTransaction = Expense(0,category,description,date,amount.toDouble(),expensePhoto)

                ExpenseDatabaseHelper.addExpense(expenseTransaction)
                Toast.makeText(this,"Expense Added Successfully",Toast.LENGTH_SHORT).show()
            }

        }


    }
    private fun setDate(calendar: Calendar){
        val dateFormat = "yyyy-MM-dd"
        val simple = SimpleDateFormat(dateFormat, Locale.UK)
        binding.DateEditText.text = simple.format(calendar.time)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    val bitmap = result.data?.extras?.get("data") as Bitmap
                    binding.expensePhoto.setImageBitmap(bitmap)
                    binding.expensePhoto.visibility = View.VISIBLE
                }
            }
        }

    fun imageToByteArray(imageView: ImageView): ByteArray {

        val drawable = imageView.drawable
        val bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}