//Detailed recipe list.
//All items displayed are editable.

package com.example.recipeapp_1.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.recipeapp_1.DatabaseHelper
import com.example.recipeapp_1.R
import com.example.recipeapp_1.model.RecipeModel
import java.io.ByteArrayOutputStream

class RecipeDetailsActivity : AppCompatActivity() {

    companion object{
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
        private const val PICK_IMAGE = 3
    }

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recipe: RecipeModel

    private lateinit var ivRecipeImg: ImageView
    private lateinit var tvRecipeName: TextView
    private lateinit var tvRecipeType: TextView
    private lateinit var tvRecipeTime: TextView
    private lateinit var tvRecipePax: TextView
    private lateinit var tvRecipeIngredients: TextView
    private lateinit var tvRecipeSteps: TextView
    private lateinit var btnEditRecipeImage: ImageButton
    private lateinit var btnEditRecipeDetails: ImageButton
    private lateinit var btnEditRecipeIngredients: ImageButton
    private lateinit var btnEditRecipeSteps: ImageButton
    private lateinit var btnDelete: Button
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        initView()
        dbHelper = DatabaseHelper(this)
        val id = intent.getIntExtra("id", 0)
        recipe = dbHelper.getSpecificRecipe(id)!!

        setView()

        btnEditRecipeDetails.setOnClickListener{
            val intent = Intent(this, EditDetailsActivity::class.java)
            intent.putExtra("id", recipe.recipeId)
            startActivity(intent)
        }

        btnEditRecipeIngredients.setOnClickListener{
            val intent = Intent(this, EditIngredientsActivity::class.java)
            intent.putExtra("id", recipe.recipeId)
            startActivity(intent)
        }

        btnEditRecipeSteps.setOnClickListener{
            val intent = Intent(this, EditStepsActivity::class.java)
            intent.putExtra("id", recipe.recipeId)
            startActivity(intent)
        }

        btnDelete.setOnClickListener{deleteRecipe(recipe.recipeId)}

        btnEditRecipeImage.setOnClickListener{addImage()}
    }

    private fun initView(){
        ivRecipeImg = findViewById(R.id.ivRecipeImage)
        tvRecipeName = findViewById(R.id.tvRecipeName)
        tvRecipeType = findViewById(R.id.tvRecipeType)
        tvRecipeTime = findViewById(R.id.tvRecipeTime)
        tvRecipePax = findViewById(R.id.tvRecipePax)
        tvRecipeIngredients = findViewById(R.id.tvIngredients)
        tvRecipeSteps = findViewById(R.id.tvSteps)
        btnEditRecipeImage = findViewById(R.id.edit_recipeImage)
        btnEditRecipeDetails = findViewById(R.id.edit_recipeDetails)
        btnEditRecipeIngredients = findViewById(R.id.edit_ingredients)
        btnEditRecipeSteps= findViewById(R.id.edit_steps)
        btnDelete = findViewById(R.id.btnDelete)
    }

    private fun setView(){
        ivRecipeImg.setImageBitmap(BitmapFactory.decodeByteArray(recipe.recipeImage, 0, recipe.recipeImage.size))
        tvRecipeName.text = recipe.recipeName
        tvRecipeType.text = recipe.recipeType
        tvRecipeTime.text = recipe.recipeTime
        tvRecipePax.text = recipe.recipePax
        tvRecipeIngredients.text = recipe.recipeIngredients
        tvRecipeSteps.text = recipe.recipeSteps
    }

    private fun deleteRecipe(id:Int?){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage("Are you sure to delete?")
        alertDialog.setCancelable(true)
        alertDialog.setPositiveButton("Yes"){ dialog, _ ->
            dbHelper.deleteRecipe(id)
            dialog.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }
        alertDialog.setNegativeButton("No"){ dialog, _ ->
            dialog.dismiss()
        }

        val alert = alertDialog.create()
        alert.show()
    }

    private fun updateRecipeImage(img: Bitmap){
        val stream = ByteArrayOutputStream()
        img.compress(Bitmap.CompressFormat.PNG, 100, stream)
        recipe.recipeImage = stream.toByteArray()
        dbHelper.updateRecipe(recipe)
    }

    private fun openGallery(){
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
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
                val recipeThumbnail = data!!.extras!!.get("data") as Bitmap
                ivRecipeImg.setImageBitmap(recipeThumbnail)
                updateRecipeImage(recipeThumbnail)
            }
            if (requestCode == PICK_IMAGE) {
                imageUri = data?.data
                val recipeThumbnail = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                ivRecipeImg.setImageBitmap(recipeThumbnail)
                updateRecipeImage(recipeThumbnail)
            }
        }
    }
}