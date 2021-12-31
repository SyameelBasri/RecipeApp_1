//AddRecipeActivity is where user create/add new recipe.
//User is required to fill up the recipe name, ingredients and step.
//Recipe image can be added through camera or picking image from gallery.

package com.example.recipeapp_1.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.recipeapp_1.DatabaseHelper
import com.example.recipeapp_1.R
import com.example.recipeapp_1.model.RecipeModel
import java.io.ByteArrayOutputStream

class AddRecipeActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object{
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
        private const val PICK_IMAGE = 3
    }

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var recipeTypes: Spinner
    private lateinit var recipeTime: Spinner
    private lateinit var recipePax: Spinner
    private lateinit var recipeImage: ImageView
    private lateinit var fieldRecipeName: EditText
    private lateinit var fieldRecipeIngredients: EditText
    private lateinit var fieldRecipeSteps: EditText
    private lateinit var btnAddRecipe: Button
    private var selectedType = ""
    private var selectedTime = ""
    private var selectedPax = ""
    private var recipeThumbnail: Bitmap? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        dbHelper = DatabaseHelper(this)

        initView()


        val editRecipeImage:ImageButton = findViewById(R.id.edit_recipeImage)
        editRecipeImage.setOnClickListener{addImage()}

        btnAddRecipe.setOnClickListener{addRecipe()}
    }

    private fun addRecipe(){
        val name = fieldRecipeName.text.toString()
        val ingredients = fieldRecipeIngredients.text.toString()
        val steps = fieldRecipeSteps.text.toString()

        if(name.isEmpty() || ingredients.isEmpty() || steps.isEmpty()){
            Toast.makeText(this,"Please enter recipe name, ingredients & steps", Toast.LENGTH_LONG).show()
        }else{
            val stream = ByteArrayOutputStream()
            recipeThumbnail?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArrayImage = stream.toByteArray()

            val recipe = RecipeModel(
                recipeName = name,
                recipeType = selectedType,
                recipeTime = selectedTime,
                recipePax = selectedPax,
                recipeIngredients = ingredients,
                recipeSteps = steps,
                recipeImage = byteArrayImage
            )
            val status = dbHelper.insertRecipe(recipe)
            if(status > -1){
                Toast.makeText(this,"Recipe Added", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                this.finish()
            }else{
                Toast.makeText(this, "Recipe not saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initView(){
        recipeTypes = findViewById(R.id.spinnerRecipeType)
        recipeTime = findViewById(R.id.spinnerRecipeTime)
        recipePax = findViewById(R.id.spinnerRecipePax)
        recipeImage = findViewById(R.id.recipeImageView)
        fieldRecipeName = findViewById(R.id.recipeName)
        fieldRecipeIngredients = findViewById(R.id.recipeIngredients)
        fieldRecipeSteps = findViewById(R.id.recipeSteps)
        btnAddRecipe = findViewById(R.id.addButton)

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
        selectedType = "Chicken"
        selectedTime = "0 - 5min"
        selectedPax = "1 - 2 pax"
    }

    private fun addImage(){
        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Get image from camera or gallery")

        val method = arrayOf("Camera", "Gallery")
        builder.setItems(method) { dialog, which ->
            when (which) {
                0 -> {startCamera()}
                1 -> {openGallery()}
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun openGallery(){
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    private fun startCamera(){
        if(ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }else{
                Toast.makeText(this,"Camere Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CAMERA_REQUEST_CODE){
                recipeThumbnail = data!!.extras!!.get("data") as Bitmap
                recipeImage.setImageBitmap(recipeThumbnail)
            }
            if (requestCode == PICK_IMAGE) {
                imageUri = data?.data
                recipeThumbnail = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                recipeImage.setImageBitmap(recipeThumbnail)
            }
        }
    }
}