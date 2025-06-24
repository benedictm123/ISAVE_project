package com.example.isave.DatabaseHelpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.isave.Classes.Category

class CategoriesDatabaseHelper(context: Context):SQLiteOpenHelper(context, DATABASE_NAME,null,
    DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "categoryDb"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "categories"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME(" +
                "$COLUMN_ID INT PRIMARY KEY," +
                "$COLUMN_DESCRIPTION TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val deleteQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(deleteQuery)
        onCreate(db)
    }

    fun saveCategory(category: Category){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DESCRIPTION,category.description)

        }
        db.insert(TABLE_NAME,null,values)
        db.close()
    }

    fun getCategories():List<Category>{
        val db = readableDatabase
        val categoryList = mutableListOf<Category>()
        val query = "SELECT * FROM $TABLE_NAME"

        val cursor = db.rawQuery(query,null)
         while (cursor.moveToNext()){
             val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
             val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))

             val category = Category(id,description)
             categoryList.add(category)
         }
        cursor.close()
        db.close()
        return categoryList
    }
    fun getCategoryDescriptions():List<String>{
        val categories = mutableListOf<String>()
        val categoryObjects = getCategories()
        val i = 0
        while (i<categoryObjects.size){
            val description = categoryObjects[i].description
            categories.add(description)
        }
        return categories
    }
}