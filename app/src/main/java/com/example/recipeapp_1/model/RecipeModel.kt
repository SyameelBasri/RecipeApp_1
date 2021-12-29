package com.example.recipeapp_1.model

import android.graphics.Bitmap

data class RecipeModel(
    var recipeId: Int? = null,
    var recipeName: String = "",
    var recipeType: String = "",
    var recipeTime: String = "",
    var recipePax: String = "",
    var recipeIngredients: String = "",
    var recipeSteps: String = "",
    var recipeImage: ByteArray
) {
}