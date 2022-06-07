package com.anbdevelopers.foodrecipes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.anbdevelopers.foodrecipes.models.Recipe;
import com.anbdevelopers.foodrecipes.vewmodels.RecipeViewModel;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

public class RecipeActivity extends BaseActivity {

    private static final String TAG = "RecipeActivity";
    private ImageView mRecipeImage;
    private TextView mRecipeTitle, mRecipeRank;
    private LinearLayout mRecipeIngredientsContainer;
    private ScrollView mScrollView;
    private RecipeViewModel mRecipeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        mRecipeImage = findViewById(R.id.recipe_image);
        mRecipeTitle = findViewById(R.id.recipe_title);
        mRecipeRank = findViewById(R.id.recipe_social_score);
        mRecipeIngredientsContainer = findViewById(R.id.ingredients_container);
        mScrollView = findViewById(R.id.parent);
        mRecipeViewModel= new ViewModelProvider(this).get(RecipeViewModel.class);
        showProgressBar(true);
        subscribeObservers();
        getIncomingIntent();

    }
    private void getIncomingIntent()
    {
        if (getIntent().hasExtra("recipe"))
        {
            Recipe recipe = getIntent().getParcelableExtra("recipe");
            Log.d(TAG, "getIncomingIntent: "+recipe.getTitle());
            mRecipeViewModel.searchRecipeById(recipe.getRecipe_id());
        }
    }
    private void subscribeObservers()
    {
        mRecipeViewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                if (recipe!=null)
                {
                    if (recipe.getRecipe_id().equals(mRecipeViewModel.getRecipeId()))
                    {
                        setRecipeProperties(recipe);
                        mRecipeViewModel.setDidRetrieveRecipe(true);
                    }

                }
            }
        });
        mRecipeViewModel.isRecipeRequestTimedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean && !mRecipeViewModel.getDidRetrieveRecipe())
                {
                    Log.d(TAG, "onChanged: timed out..");
                    displayErrorScreen("Error Retrieving Data, check network connection");
                }
            }
        });
    }
    private void setRecipeProperties(Recipe recipe)
    {
        if (recipe!=null)
        {
            Glide.with(this).load(recipe.getImage_url())
                    .placeholder(R.drawable.downloading_icon).into(mRecipeImage);
            mRecipeTitle.setText(recipe.getTitle());
            mRecipeRank.setText(String.valueOf(Math.round(recipe.getSocial_rank())));
            mRecipeIngredientsContainer.removeAllViews();
            for (String ingredient: recipe.getIngredients())
            {
                TextView textView = new TextView(this);
                textView.setText(ingredient);
                textView.setTextSize(15);
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                mRecipeIngredientsContainer.addView(textView);
            }
        }
        showParent();
        showProgressBar(false);
    }
    private void displayErrorScreen(String errorMessage)
    {
        mRecipeTitle.setText("Error Receiving recipe...");
        mRecipeRank.setText("");
        TextView textView = new TextView(this);
        textView.setTextSize(15);
        if (errorMessage.equals(""))
        {
            textView.setText(errorMessage);
        }else {
             textView.setText("Error");
        }

        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        mRecipeIngredientsContainer.addView(textView);
        Glide.with(this).load(R.drawable.img_failed)
                .placeholder(R.drawable.downloading_icon).into(mRecipeImage);
        showParent();
        showProgressBar(false);
    }
    private void showParent()
    {
        mScrollView.setVisibility(View.VISIBLE);
    }
}