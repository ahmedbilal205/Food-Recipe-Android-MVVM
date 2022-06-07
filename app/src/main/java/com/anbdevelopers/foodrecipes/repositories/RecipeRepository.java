package com.anbdevelopers.foodrecipes.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.anbdevelopers.foodrecipes.models.Recipe;
import com.anbdevelopers.foodrecipes.requests.RecipeApi;
import com.anbdevelopers.foodrecipes.requests.RecipeApiClient;

import java.util.List;

public class RecipeRepository
{
    private static RecipeRepository instance;
    private static final String TAG = "RecipeRepository";
    private RecipeApiClient mRecipeApiClient;
    private String mQuery;
    private int mPageNumber;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<Recipe>> mRecipes =  new MediatorLiveData<>();

    public static RecipeRepository getInstance()
    {
        if (instance==null)
        {
            instance= new RecipeRepository();
        }
        return instance;
    }
    public RecipeRepository()
    {
        mRecipeApiClient = RecipeApiClient.getInstance();
        initMediator();
    }
    private void initMediator()
    {
        LiveData<List<Recipe>> recipeListApiSource= mRecipeApiClient.getRecipes();
        mRecipes.addSource(recipeListApiSource, recipes -> {
            if (recipes!=null)
            {
                mRecipes.setValue(recipes);
                doneQuery(recipes);
            }else{
                doneQuery(null);
            }
        });
    }
    public LiveData<Boolean> isQueryExhausted()
    {
        return mIsQueryExhausted;
    }
    private void doneQuery(List<Recipe> list)
    {
        if (list!=null)
        {
            if (list.size()%30!=0)
            {
//                Log.d(TAG, "doneQuery: List is exausted");
                mIsQueryExhausted.setValue(true);
            }       
        }else
        {
            mIsQueryExhausted.setValue(true);
        }
    }
    public LiveData<List<Recipe>> getRecipes()
    {
        return mRecipes;
    }
    public LiveData<Recipe> getRecipe()
    {
        return mRecipeApiClient.getRecipe();
    }
    public LiveData<Boolean> isRecipeRequestTimedOut()
    {
        return mRecipeApiClient.isRecipeRequestTimedOut();
    }
    public void searchRecipesApi(String query, int pageNumber)
    {
        if (pageNumber==0){pageNumber=1;}
        mQuery=query;
        mPageNumber= pageNumber;
        mIsQueryExhausted.setValue(false);
        mRecipeApiClient.searchRecipesApi(query,pageNumber);
    }
    public void searchRecipeById(String recipeId)
    {
        mRecipeApiClient.searchRecipeById(recipeId);
    }
    public void searchNextPage()
    {
        searchRecipesApi(mQuery,mPageNumber+1);

    }
    public void cancelRequest()
    {
        mRecipeApiClient.cancelRequest();
    }
}
