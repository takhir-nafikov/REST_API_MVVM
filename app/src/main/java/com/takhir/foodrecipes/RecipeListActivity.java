package com.takhir.foodrecipes;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.takhir.foodrecipes.adapters.OnRecipeListener;
import com.takhir.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.takhir.foodrecipes.models.Recipe;
import com.takhir.foodrecipes.util.Testing;
import com.takhir.foodrecipes.util.VerticalSpacingItemDecorator;
import com.takhir.foodrecipes.viewmodels.RecipeListViewModel;

import java.util.List;

public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel recipeListViewModel;
    private RecyclerView recyclerView;
    private RecipeRecyclerAdapter recipeRecyclerAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        recyclerView = findViewById(R.id.recipe_list);
        searchView = findViewById(R.id.search_view);

        recipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        initRecyclerView();
        subscribeObserves();
        initSearchView();
        if (!recipeListViewModel.isViewingRecipes()) {
            displaySearchCategories();
        }
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    private void subscribeObserves() {
        recipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes != null) {
                    if (recipeListViewModel.isViewingRecipes()) {
                        Testing.prinnRecipes(recipes, "recipes test");
                        recipeListViewModel.setPerformingQuery(false);
                        recipeRecyclerAdapter.setRecipes(recipes);
                    }
                }
            }
        });

        recipeListViewModel.isQueryExhausted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    Log.d(TAG, "onChanged: the query is exhausted" );
                    recipeRecyclerAdapter.setQueryExhausted();
                }
            }
        });
    }

    private void  initRecyclerView() {
        recipeRecyclerAdapter = new RecipeRecyclerAdapter(this);
        recyclerView.setAdapter(recipeRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        recyclerView.addItemDecoration(itemDecorator);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (recyclerView.canScrollVertically(1)) {
                    recipeListViewModel.searchNextPage();
                }
            }
        });
    }


    private void initSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                recipeRecyclerAdapter.displayLoading();
                recipeListViewModel.searchRecipesApi(query, 1);
                searchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onRecipeClick(int position) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", recipeRecyclerAdapter.getSelectedRecipe(position));
        startActivity(intent);

    }

    @Override
    public void onCategoryClick(String category) {
        recipeRecyclerAdapter.displayLoading();
        recipeListViewModel.searchRecipesApi(category, 1);
        searchView.clearFocus();
    }

    private void  displaySearchCategories() {
        recipeListViewModel.setViewingRecipes(false);
        recipeRecyclerAdapter.displaySearchCategories();
    }

    @Override
    public void onBackPressed() {
        if (recipeListViewModel.onBackPressed()) {
            super.onBackPressed();
        } else {
            displaySearchCategories();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_categories) {
            displaySearchCategories();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
