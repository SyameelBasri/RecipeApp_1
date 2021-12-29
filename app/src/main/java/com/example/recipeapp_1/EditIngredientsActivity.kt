package com.example.recipeapp_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.recipeapp_1.model.RecipeModel

class EditIngredientsActivity : AppCompatActivity() {

    private lateinit var fieldIngredients: EditText
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recipe: RecipeModel
    private lateinit var btnEdit: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_ingredients)

        dbHelper = DatabaseHelper(this)
        val id = intent.getIntExtra("id", 0)
        recipe = dbHelper.getSpecificRecipe(id)!!

        btnEdit = findViewById(R.id.editButton)
        fieldIngredients = findViewById(R.id.editRecipeIngredients)
        fieldIngredients.setText(recipe.recipeIngredients)

        btnEdit.setOnClickListener{
            val ingredients = fieldIngredients.text.toString()
            if(ingredients.isEmpty()){
                Toast.makeText(this,"Please enter recipe ingredients.", Toast.LENGTH_LONG).show()
            }else{
                recipe.recipeIngredients = ingredients
                val status =dbHelper.updateRecipe(recipe)
                if(status > -1){
                    Toast.makeText(this,"Recipe Updated", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"Update failed", Toast.LENGTH_LONG).show()
                }
                val intent = Intent(this,RecipeDetailsActivity::class.java)
                intent.putExtra("id", recipe.recipeId)
                startActivity(intent)
                this.finish()
            }
        }
    }
}