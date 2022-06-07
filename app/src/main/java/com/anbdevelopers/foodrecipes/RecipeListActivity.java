package com.anbdevelopers.foodrecipes;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anbdevelopers.foodrecipes.adapters.OnRecipeListener;
import com.anbdevelopers.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.anbdevelopers.foodrecipes.models.Recipe;
import com.anbdevelopers.foodrecipes.util.Testing;
import com.anbdevelopers.foodrecipes.vewmodels.RecipeListViewModel;

import java.util.List;

public class RecipeListActivity extends BaseActivity implements OnRecipeListener {
    private static final String TAG = "RecipeListActivity";
    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mAdapter;
    SearchView searchView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        mRecyclerView = findViewById(R.id.recipe_list);
        searchView = findViewById(R.id.search_view);
        toolbar =(Toolbar) findViewById(R.id.toolbar);
        //mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        //Below Not Sure should work
        mRecipeListViewModel = new ViewModelProvider(this).get(RecipeListViewModel.class);
        initRecyclerView();
        subscribeObservers();
        //testRetrofitRequest();
        initSearchView();
        if (!mRecipeListViewModel.isViewingRecipes())
        {
            displaySearchCategories();
        }
        setActionBar(toolbar);
        toolbar.showOverflowMenu();
        toolbar.inflateMenu(R.menu.recipe_search_menu);
    }
    private void initRecyclerView()
    {
        mAdapter = new RecipeRecyclerAdapter(this);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = 25;
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!mRecyclerView.canScrollVertically(1))
                {
                    //End of recycler vew
                    mRecipeListViewModel.searchNextPage();
                }
            }
        });
    }
    private void subscribeObservers()
    {
        mRecipeListViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if (recipes!=null)
                {
                    if (mRecipeListViewModel.isViewingRecipes())
                    {
                        Testing.printRecipes(recipes,TAG);
                        mRecipeListViewModel.setIsPerformingQuery(false);
                        mAdapter.setRecipes(recipes);
                    }

                }

            }
        });
        mRecipeListViewModel.isQueryExhausted().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                {
                    Log.d(TAG, "onChanged: query is finishd");
                    mAdapter.setQueryExhausted();
                }
            }
        });
    }
    private void initSearchView()
    {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mAdapter.displayLoading();
                mRecipeListViewModel.searchRecipesApi(s,1);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
    private void searchRecipeApi(String query, int pageNumber)
    {
        mRecipeListViewModel.searchRecipesApi(query,pageNumber);
    }
    private void testRetrofitRequest()
    {
        searchRecipeApi("chicken",1);
    }

    @Override
    public void onRecipeClick(int position)
    {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe",mAdapter.getSelectedRecipe(position));
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(String category)
    {
        mAdapter.displayLoading();
        mRecipeListViewModel.searchRecipesApi(category,1);
        searchView.clearFocus();
    }
    private void displaySearchCategories()
    {
        mRecipeListViewModel.setIsViewingRecipes(false);
        mAdapter.displaySearchCategories();
    }

    @Override
    public void onBackPressed() {
        if (mRecipeListViewModel.onBackPressed())
        {
            super.onBackPressed();
        }else
        {
            displaySearchCategories();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_search_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.action_categories)
        {
            displaySearchCategories();
        }
        return super.onOptionsItemSelected(item);
    }
}