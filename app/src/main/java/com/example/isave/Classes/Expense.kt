package com.example.isave.Classes

data class Expense(val expenseId:Int,val category:String,val description: String,val date:String,val amount:Double,val photoExpense:ByteArray)
