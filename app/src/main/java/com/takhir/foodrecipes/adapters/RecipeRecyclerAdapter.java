package com.takhir.foodrecipes.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.takhir.foodrecipes.R;
import com.takhir.foodrecipes.models.Recipe;
import com.takhir.foodrecipes.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int RECIPE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORY_TYPE = 3;
    private static final int EXHAUSTED_TYPE = 4;

    private List<Recipe> recipes;
    private OnRecipeListener onRecipeListener;

    public RecipeRecyclerAdapter(OnRecipeListener onRecipeListener) {
        this.onRecipeListener = onRecipeListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = null;
        switch (i) {
            case RECIPE_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false);
                return new RecipeViewHolder(view, onRecipeListener);
            }
            case LOADING_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_list_item, viewGroup, false);
                return new RecipeViewHolder(view, onRecipeListener);
            }
            case EXHAUSTED_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_exhausted, viewGroup, false);
                return new SearchExhaustedViewHolder(view);
            }
            case CATEGORY_TYPE: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_category_list_item, viewGroup, false);
                return new CategoryViewHolder(view, onRecipeListener);
            }
            default: {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false);
                return new RecipeViewHolder(view, onRecipeListener);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int itemViewType = getItemViewType(i);
        if (itemViewType == RECIPE_TYPE) {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);

            Glide.with(viewHolder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(recipes.get(i).getImage_url())
                    .into(((RecipeViewHolder)viewHolder).image);

            ((RecipeViewHolder)viewHolder).title.setText(recipes.get(i).getTitle());
            ((RecipeViewHolder)viewHolder).publisher.setText(recipes.get(i).getPublisher());
            ((RecipeViewHolder)viewHolder)
                    .socialScore.setText(String.valueOf(Math.round(recipes.get(i).getSocial_rank())));
        } else if (itemViewType == CATEGORY_TYPE) {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);

            Uri path = Uri.parse("android.resource://com.takhir.foodrecipes/drawable/" + recipes.get(i).getImage_url());
            Glide.with(viewHolder.itemView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(path)
                    .into(((CategoryViewHolder)viewHolder).categoryImage);

            ((CategoryViewHolder)viewHolder).categoryTitle.setText(recipes.get(i).getTitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(recipes.get(position).getSocial_rank() == -1){
            return CATEGORY_TYPE;
        }
        else if(recipes.get(position).getTitle().equals("LOADING...")){
            return LOADING_TYPE;
        }
        else if(recipes.get(position).getTitle().equals("EXHAUSTED...")){
            return EXHAUSTED_TYPE;
        }
        else if(position == recipes.size() - 1
                && position != 0
                && !recipes.get(position).getTitle().equals("EXHAUSTED...")){
            return LOADING_TYPE;
        }
        else{
            return RECIPE_TYPE;
        }
    }

    public void setQueryExhausted(){
        hideLoading();
        Recipe exhaustedRecipe = new Recipe();
        exhaustedRecipe.setTitle("EXHAUSTED...");
        recipes.add(exhaustedRecipe);
        notifyDataSetChanged();
    }

    private boolean isLoading() {
        if (recipes != null && recipes.size() > 0) {
            if (recipes.get(recipes.size() - 1).getTitle().equals("LOADING...")) {
                return true;
            }
        }
        return false;
    }

    private void hideLoading(){
        if(isLoading()){
            for(Recipe recipe: recipes){
                if(recipe.getTitle().equals("LOADING...")){
                    recipes.remove(recipe);
                }
            }
            notifyDataSetChanged();
        }
    }

    public void displayLoading(){
        if (!isLoading()) {
            Recipe recipe = new Recipe();
            recipe.setTitle("LOADING...");
            List<Recipe> loadingList = new ArrayList<>();
            loadingList.add(recipe);
            recipes = loadingList;
            notifyDataSetChanged();
        }
    }

    public void displaySearchCategories(){
        List<Recipe> categories = new ArrayList<>();
        for(int i = 0; i< Constants.DEFAULT_SEARCH_CATEGORIES.length; i++){
            Recipe recipe = new Recipe();
            recipe.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            recipe.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            recipe.setSocial_rank(-1);
            categories.add(recipe);
        }
        recipes = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (recipes != null) {
            return recipes.size();
        }
        return 0;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    public Recipe getSelectedRecipe(int position){
        if(recipes != null){
            if(recipes.size() > 0){
                return recipes.get(position);
            }
        }
        return null;
    }

}
