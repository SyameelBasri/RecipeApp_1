package com.example.recipeapp_1

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp_1.model.RecipeModel

class RecipeListAdapter: RecyclerView.Adapter<RecipeListAdapter.ViewHolder>() {
    private var recipeList:ArrayList<RecipeModel> = ArrayList()
    private var onClickItem:((RecipeModel) -> Unit)? = null

    fun addItems(items: ArrayList<RecipeModel>){
        this.recipeList = items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback:(RecipeModel) -> Unit){
        this.onClickItem = callback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = RecipeListAdapter.ViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.recipe_card, parent, false)
    )

    override fun onBindViewHolder(holder: RecipeListAdapter.ViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.bindView(recipe)
        holder.itemView.setOnClickListener{onClickItem?.invoke(recipe)}
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private var img = view.findViewById<ImageView>(R.id.cardRecipeImage)
        private var name = view.findViewById<TextView>(R.id.cardRecipeName)
        private var type = view.findViewById<TextView>(R.id.cardRecipeType)
        private var time = view.findViewById<TextView>(R.id.cardRecipeTime)
        private var pax = view.findViewById<TextView>(R.id.cardRecipePax)

        fun bindView(recipe: RecipeModel){
            img.setImageBitmap(BitmapFactory.decodeByteArray(recipe.recipeImage, 0, recipe.recipeImage.size))
            name.text = recipe.recipeName
            type.text = recipe.recipeType
            time.text = recipe.recipeTime
            pax.text = recipe.recipePax
        }
    }
}