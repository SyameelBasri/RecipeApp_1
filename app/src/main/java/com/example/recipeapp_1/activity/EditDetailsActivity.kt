package com.example.recipeapp_1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.recipeapp_1.DatabaseHelper
import com.example.recipeapp_1.R
import com.example.recipeapp_1.model.RecipeModel

class EditDetailsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recipe: RecipeModel
    private lateinit var recipeTypes: Spinner
    private lateinit var recipeTime: Spinner
    private lateinit var recipePax: Spinner
    private lateinit var fieldRecipeName: EditText
    private lateinit var btnEdit: Button
    private var selectedType = ""
    private var selectedTime = ""
    private var selectedPax = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_details)

        dbHelper = DatabaseHelper(this)
        val id = intent.getIntExtra("id", 0)
        recipe = dbHelper.getSpecificRecipe(id)!!

        initView()

        fieldRecipeName.setText(recipe.recipeName)

        btnEdit.setOnClickListener{
            editRecipeDetails()
            val intent = Intent(this, RecipeDetailsActivity::class.java)
            intent.putExtra("id", recipe.recipeId)
            startActivity(intent)
            this.finish()
        }

    }

    private fun editRecipeDetails(){
        recipe.recipeName = fieldRecipeName.text.toString()
        recipe.recipeType = selectedType
        recipe.recipeTime = selectedTime
        recipe.recipePax = selectedPax

        val status =dbHelper.updateRecipe(recipe)
        if(status > -1){
            Toast.makeText(this,"Recipe Updated", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this,"Update failed", Toast.LENGTH_LONG).show()
        }

    }

    private fun initView(){
        recipeTypes = findViewById(R.id.spinnerRecipeType)
        recipeTime = findViewById(R.id.spinnerRecipeTime)
        recipePax = findViewById(R.id.spinnerRecipePax)
        fieldRecipeName = findViewById(R.id.recipeName)
        btnEdit = findViewById(R.id.editButton)

        val recipeTypeList: MutableList<String> = resources.getStringArray(R.array.recipe_type).toMutableList()
        recipeTypeList.remove("All")
        val recipeTimeList: MutableList<String> = resources.getStringArray(R.array.recipe_time).toMutableList()
        recipeTimeList.remove("All")
        val recipePaxList: MutableList<String> = resources.getStringArray(R.array.recipe_pax).toMutableList()
        recipePaxList.remove("All")

        recipeTypes.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,recipeTypeList)
        recipeTime.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,recipeTimeList)
        recipePax.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,recipePaxList)

        recipeTypes.onItemSelectedListener = this
        recipeTime.onItemSelectedListener = this
        recipePax.onItemSelectedListener = this

        recipeTypes.setSelection(getSpinnerIndex(recipeTypes, recipe.recipeType))
        recipeTime.setSelection(getSpinnerIndex(recipeTime, recipe.recipeTime))
        recipePax.setSelection(getSpinnerIndex(recipePax, recipe.recipePax))

    }

    private fun getSpinnerIndex(spinner: Spinner, value: String): Int{
        for (i in 0..spinner.count){
            if(spinner.getItemAtPosition(i).toString() == value) return i
        }
        return 0
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val selected = parent.getItemAtPosition(pos).toString()
        when(parent.id){
            R.id.spinnerRecipeType -> selectedType = selected
            R.id.spinnerRecipeTime -> selectedTime = selected
            R.id.spinnerRecipePax -> selectedPax = selected
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedType = recipe.recipeType
        selectedTime = recipe.recipeTime
        selectedPax = recipe.recipePax
    }
}