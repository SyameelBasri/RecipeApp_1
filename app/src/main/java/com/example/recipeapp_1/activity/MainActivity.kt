//MainActivity is the app homepage.
//Contains list of recipe and filter for recipe type, time require and serve pax

package com.example.recipeapp_1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp_1.DatabaseHelper
import com.example.recipeapp_1.R
import com.example.recipeapp_1.RecipeListAdapter
import com.example.recipeapp_1.model.RecipeModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var recipeTypes: Spinner
    private lateinit var recipeTime: Spinner
    private lateinit var recipePax: Spinner
    private lateinit var fab: FloatingActionButton

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: RecipeListAdapter? = null
    private var recipeList: ArrayList<RecipeModel> = ArrayList()

    private var selectedType = "All"
    private var selectedTime = "All"
    private var selectedPax = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        initView()
        initRecyclerView()

        recipeList = dbHelper.getRecipes(selectedType, selectedTime, selectedPax)
        adapter?.addItems(recipeList)

        fab.setOnClickListener{
            startActivity(Intent(this, AddRecipeActivity::class.java))
        }

        adapter?.setOnClickItem {
            val intent = Intent(this, RecipeDetailsActivity::class.java)
            intent.putExtra("id", it.recipeId)
            startActivity(intent)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val selected = parent.getItemAtPosition(pos).toString()
        when(parent.id){
            R.id.spinnerRecipeType -> selectedType = selected
            R.id.spinnerRecipeTime -> selectedTime = selected
            R.id.spinnerRecipePax -> selectedPax = selected
        }
        recipeList = dbHelper.getRecipes(selectedType, selectedTime, selectedPax)
        adapter?.addItems(recipeList)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedType = "All"
        selectedTime = "All"
        selectedPax = "All"
    }

    private fun initView(){
        fab = findViewById(R.id.fabAdd)
        recipeTypes = findViewById(R.id.spinnerRecipeType)
        recipeTime = findViewById(R.id.spinnerRecipeTime)
        recipePax = findViewById(R.id.spinnerRecipePax)
        recyclerView = findViewById(R.id.recyclerView)

        recipeTypes.adapter = ArrayAdapter.createFromResource(this, R.array.recipe_type, android.R.layout.simple_spinner_dropdown_item)
        recipeTime.adapter = ArrayAdapter.createFromResource(this, R.array.recipe_time, android.R.layout.simple_spinner_dropdown_item)
        recipePax.adapter = ArrayAdapter.createFromResource(this, R.array.recipe_pax, android.R.layout.simple_spinner_dropdown_item)

        recipeTypes.onItemSelectedListener = this
        recipeTime.onItemSelectedListener = this
        recipePax.onItemSelectedListener = this
    }

    private fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecipeListAdapter()
        recyclerView.adapter = adapter
    }

}