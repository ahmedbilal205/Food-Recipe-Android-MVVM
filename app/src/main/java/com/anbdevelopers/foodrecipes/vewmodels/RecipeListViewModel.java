package com.anbdevelopers.foodrecipes.vewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.anbdevelopers.foodrecipes.models.Recipe;
import com.anbdevelopers.foodrecipes.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends ViewModel
{
    RecipeRepository mRecipeRepository;
    private boolean mIsViewingRecipes;
    private boolean mIsPerformingQuery;
    public RecipeListViewModel()
    {
//        mIsViewingRecipes=false;
        mRecipeRepository = RecipeRepository.getInstance();
        mIsPerformingQuery = false;
    }

    public LiveData<List<Recipe>> getRecipes()
    {
        return mRecipeRepository.getRecipes();
    }
    public LiveData<Boolean> isQueryExhausted()
    {
        return mRecipeRepository.isQueryExhausted();
    }
    public void searchRecipesApi(String query, int pageNumber)
    {
        mIsViewingRecipes = true;
        mRecipeRepository.searchRecipesApi(query,pageNumber);
        setIsPerformingQuery(true);
    }

    public boolean isViewingRecipes()
    {
        return mIsViewingRecipes;
    }

    public void setIsViewingRecipes(boolean mIsViewingRecipes) {
        this.mIsViewingRecipes = mIsViewingRecipes;
    }

    public boolean isPerformingQuery() {
        return mIsPerformingQuery;
    }

    public void setIsPerformingQuery(boolean mIsPerformingQuery) {
        this.mIsPerformingQuery = mIsPerformingQuery;
    }

    public void searchNextPage()
    {
        if (!mIsPerformingQuery&&mIsViewingRecipes&&!isQueryExhausted().getValue())
        {
            mRecipeRepository.searchNextPage();
        }
    }
    public boolean onBackPressed()
    {
        if (mIsPerformingQuery)
        {
            mRecipeRepository.cancelRequest();
            mIsPerformingQuery = false;
            //send message to repository to cancel query
        }
        if (mIsViewingRecipes)
        {
            mIsViewingRecipes=false;
            return false;
        }

    return true;
    }
}
