//DatabaseHelper manage the data insert, fetch, update & delete.
//Upon installation, the preload 'recipe.db' in assets folder will be copied and used as the database.

package com.example.recipeapp_1

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.recipeapp_1.model.RecipeModel
import java.io.*
import java.lang.Exception
import kotlin.collections.ArrayList

class DatabaseHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "recipe.db"
        private const val TBL_RECIPE = "tbl_recipe"
        private const val ID ="id"
        private const val RECIPE_NAME = "name"
        private const val RECIPE_TYPE = "type"
        private const val RECIPE_TIME = "time"
        private const val RECIPE_PAX = "pax"
        private const val RECIPE_INGREDIENTS = "ingredients"
        private const val RECIPE_STEPS = "steps"
        private const val RECIPE_IMAGE = "image"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(
        "${context.packageName}.database_versions",
        Context.MODE_PRIVATE
    )

    private fun installedDatabaseIsOutdated(): Boolean {
        return preferences.getInt(DATABASE_NAME, 0) < DATABASE_VERSION
    }

    private fun writeDatabaseVersionInPreferences() {
        preferences.edit().apply {
            putInt(DATABASE_NAME, DATABASE_VERSION)
            apply()
        }
    }

    private fun installDatabaseFromAssets() {
        val inputStream = context.assets.open("recipe.db")

        try {
            val outputFile = File(context.getDatabasePath(DATABASE_NAME).path)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)
            inputStream.close()

            outputStream.flush()
            outputStream.close()
        } catch (exception: Throwable) {
            throw RuntimeException("The $DATABASE_NAME database couldn't be installed.", exception)
        }
    }

    @Synchronized
    private fun installOrUpdateIfNecessary() {
        if (installedDatabaseIsOutdated()) {
            context.deleteDatabase(DATABASE_NAME)
            installDatabaseFromAssets()
            writeDatabaseVersionInPreferences()
        }
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getWritableDatabase()
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getReadableDatabase()
    }

    override fun onCreate(db: SQLiteDatabase?) {
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
    }


    fun insertRecipe(recipe: RecipeModel): Long{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(RECIPE_NAME, recipe.recipeName)
        cv.put(RECIPE_TYPE, recipe.recipeType)
        cv.put(RECIPE_TIME, recipe.recipeTime)
        cv.put(RECIPE_PAX, recipe.recipePax)
        cv.put(RECIPE_INGREDIENTS, recipe.recipeIngredients)
        cv.put(RECIPE_STEPS, recipe.recipeSteps)
        cv.put(RECIPE_IMAGE, recipe.recipeImage)

        val result = db.insert(TBL_RECIPE, null, cv)
        db.close()
        return result
    }

    fun getSpecificRecipe(id: Int): RecipeModel?{
        val selectQuery = "SELECT * FROM $TBL_RECIPE WHERE id = $id"
        val db = this.readableDatabase
        db.rawQuery(selectQuery, null).use{
            if (it.moveToFirst()) {
                val id: Int = it.getInt(it.getColumnIndexOrThrow("id"))
                val recipeName: String = it.getString(it.getColumnIndexOrThrow("name"))
                val recipeType: String = it.getString(it.getColumnIndexOrThrow("type"))
                val recipeTime: String = it.getString(it.getColumnIndexOrThrow("time"))
                val recipePax: String = it.getString(it.getColumnIndexOrThrow("pax"))
                val recipeIngredients: String =
                    it.getString(it.getColumnIndexOrThrow("ingredients"))
                val recipeSteps: String = it.getString(it.getColumnIndexOrThrow("steps"))
                val recipeImage: ByteArray = it.getBlob(it.getColumnIndexOrThrow("image"))

                return RecipeModel(
                    recipeId = id,
                    recipeName = recipeName,
                    recipeType = recipeType,
                    recipeTime = recipeTime,
                    recipePax = recipePax,
                    recipeIngredients = recipeIngredients,
                    recipeSteps = recipeSteps,
                    recipeImage = recipeImage
                )
            }
            return null
        }
    }

    fun getRecipes(type: String, time: String, pax: String): ArrayList<RecipeModel>{
        val recipeList: ArrayList<RecipeModel> = ArrayList()
        var selectQuery = "SELECT * FROM $TBL_RECIPE"
        val db = this.readableDatabase

        if(type != "All"){
            selectQuery = "$selectQuery WHERE type = '$type'"
            if(time != "All"){
                selectQuery = "$selectQuery AND time = '$time'"
                if(pax != "All"){
                    selectQuery = "$selectQuery AND pax = '$pax'"
                }
            } else if(pax != "All"){
                selectQuery = "$selectQuery AND pax = '$pax'"
            }
        } else if(time != "All"){
            selectQuery = "$selectQuery WHERE time = '$time'"
            if(pax != "All"){
                selectQuery = "$selectQuery AND pax = '$pax'"
            }
        } else if(pax != "All"){
            selectQuery = "$selectQuery WHERE pax = '$pax'"
        }

        val cursor: Cursor?
        try {
            selectQuery = "$selectQuery ORDER BY $ID DESC"
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if(cursor.moveToFirst()){
            do{
                val id: Int = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val recipeName: String = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val recipeType: String = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                val recipeTime: String = cursor.getString(cursor.getColumnIndexOrThrow("time"))
                val recipePax: String = cursor.getString(cursor.getColumnIndexOrThrow("pax"))
                val recipeIngredients: String = cursor.getString(cursor.getColumnIndexOrThrow("ingredients"))
                val recipeSteps: String = cursor.getString(cursor.getColumnIndexOrThrow("steps"))
                val recipeImage: ByteArray  = cursor.getBlob(cursor.getColumnIndexOrThrow("image"))

                val recipe = RecipeModel(
                    recipeId = id,
                    recipeName = recipeName,
                    recipeType = recipeType,
                    recipeTime = recipeTime,
                    recipePax = recipePax,
                    recipeIngredients = recipeIngredients,
                    recipeSteps = recipeSteps,
                    recipeImage = recipeImage
                )
                recipeList.add(recipe)
            } while (cursor.moveToNext())
        }
        return recipeList
    }

    fun updateRecipe(recipe: RecipeModel): Int{
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(ID, recipe.recipeId)
        cv.put(RECIPE_NAME, recipe.recipeName)
        cv.put(RECIPE_TYPE, recipe.recipeType)
        cv.put(RECIPE_TIME, recipe.recipeTime)
        cv.put(RECIPE_PAX, recipe.recipePax)
        cv.put(RECIPE_INGREDIENTS, recipe.recipeIngredients)
        cv.put(RECIPE_STEPS, recipe.recipeSteps)
        cv.put(RECIPE_IMAGE, recipe.recipeImage)

        val result = db.update(TBL_RECIPE, cv, "id=" + recipe.recipeId, null)
        db.close()
        return result
    }

    fun deleteRecipe(id:Int?): Int{
        val db = this.writableDatabase

        val success = db.delete(TBL_RECIPE, "id=$id", null)
        db.close()
        return success
    }
}