//User edit recipe steps.
//Recipe steps cannot be left empty.

package com.example.recipeapp_1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.recipeapp_1.DatabaseHelper
import com.example.recipeapp_1.R
import com.example.recipeapp_1.model.RecipeModel

class EditStepsActivity : AppCompatActivity() {
    private lateinit var fieldSteps: EditText
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recipe: RecipeModel
    private lateinit var btnEdit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_steps)

        dbHelper = DatabaseHelper(this)
        val id = intent.getIntExtra("id", 0)
        recipe = dbHelper.getSpecificRecipe(id)!!

        btnEdit = findViewById(R.id.editButton)
        fieldSteps = findViewById(R.id.editRecipeSteps)
        fieldSteps.setText(recipe.recipeSteps)

        btnEdit.setOnClickListener{
            val steps = fieldSteps.text.toString()
            if(steps.isEmpty()){
                Toast.makeText(this,"Please enter recipe steps.", Toast.LENGTH_LONG).show()
            }else{
                recipe.recipeSteps = steps
                val status =dbHelper.updateRecipe(recipe)
                if(status > -1){
                    Toast.makeText(this,"Recipe Updated", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, RecipeDetailsActivity::class.java)
                    intent.putExtra("id", recipe.recipeId)
                    startActivity(intent)
                    this.finish()
                } else {
                    Toast.makeText(this,"Update failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}