package com.anbdevelopers.foodrecipes.requests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.anbdevelopers.foodrecipes.AppExecutors;
import com.anbdevelopers.foodrecipes.models.Recipe;
import com.anbdevelopers.foodrecipes.requests.responses.RecipeResponse;
import com.anbdevelopers.foodrecipes.requests.responses.RecipeSearchResponse;
import com.anbdevelopers.foodrecipes.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeApiClient
{
    private static final String TAG = "RecipeApiClient";
    private static RecipeApiClient instance;
    private MutableLiveData<List<Recipe>> mRecipes ;
    public RetrieveRecipesRunnable mRetrieveRecipesRunnable;
    private MutableLiveData<Recipe> mRecipe;
    private RetrieveRecipeRunnable mRetrieveRecipeRunnable;
    private MutableLiveData<Boolean> mRecipeRequestTimeout = new MutableLiveData<>();;
    public static RecipeApiClient getInstance()
    {
        if (instance==null)
        {
            instance = new RecipeApiClient();
        }
        return instance;
    }

    public RecipeApiClient()
    {
        mRecipes = new MutableLiveData<>();
        mRecipe = new MutableLiveData<>();

    }

    public LiveData<List<Recipe>> getRecipes()
    {
        return mRecipes;
    }
    public LiveData<Recipe> getRecipe()
    {
        return mRecipe;
    }
    public LiveData<Boolean> isRecipeRequestTimedOut()
    {
        return mRecipeRequestTimeout;
    }

    public void searchRecipesApi(String query, int pageNumber)
    {
        if (mRetrieveRecipesRunnable!=null)
        {
            mRetrieveRecipesRunnable=null;
        }
        mRetrieveRecipesRunnable = new RetrieveRecipesRunnable(query, pageNumber);
        final Future<?> handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipesRunnable);

        //below lines Constants.NETWORK_TIMEOUT k baad timeout
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                handler.cancel(true);
                //also inform user about timeout / no connection
            }
        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }
    public void searchRecipeById(String recipeId)
    {
        if (mRetrieveRecipeRunnable!=null)
        {
            mRetrieveRecipeRunnable=null;
        }
        mRetrieveRecipeRunnable = new RetrieveRecipeRunnable(recipeId);
        final Future<?> handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipeRunnable);
        mRecipeRequestTimeout.setValue(false);
        //below lines Constants.NETWORK_TIMEOUT k baad timeout
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                handler.cancel(true);
                mRecipeRequestTimeout.postValue(true);
                //also inform user about timeout / no connection
            }
        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }
    private class RetrieveRecipesRunnable implements Runnable
    {
        private String query;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveRecipesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run()
        {
            try {
                Response<RecipeSearchResponse> response = getRecipes(query,pageNumber).execute(); //Retrofit
                if (cancelRequest){return;}
                if (response.code()==200)
                {
                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse)response.body()).getRecipes());
                    if (pageNumber==1)
                    {
                        mRecipes.postValue(list);
                    }else
                        {
                            List<Recipe> currentRecipes = mRecipes.getValue();//getting old values from prev page
                            currentRecipes.addAll(list); //appending them with new values next page
                            mRecipes.postValue(currentRecipes);//passing it mRecipes (live data)
                        }
                }else
                {
                    Log.d(TAG, "run: "+response.errorBody().string());
                    mRecipes.postValue(null); //Later on view me check kronga agr null ha
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipes.postValue(null); //Later on view me check kronga agr null ha
            }


        }
        private Call<RecipeSearchResponse> getRecipes(String query, int pageNumber)
        {
            return ServiceGenerator.getRecipeApi().searchRecipe(
                    Constants.API_KEY,
                    query,
                    String.valueOf(pageNumber)
                    );
        }
        private void cancelRequest()
        {
            Log.d(TAG, "cancelRequest: Canceling api request");
            cancelRequest=true;

        }
    }
    private class RetrieveRecipeRunnable implements Runnable
    {
        private String recipeId;

        boolean cancelRequest;

        public RetrieveRecipeRunnable(String recipeId) {
            this.recipeId = recipeId;
            cancelRequest = false;
        }

        @Override
        public void run()
        {
            try {
                Response<RecipeResponse> response = getRecipe(recipeId).execute(); //Retrofit
                if (cancelRequest){return;}
                if (response.code()==200)
                {
                    Recipe recipe = ((RecipeResponse)response.body()).getRecipe();
                    mRecipe.postValue(recipe);
                }else
                {
                    Log.d(TAG, "run: "+response.errorBody().string());
                    mRecipe.postValue(null); //Later on view me check kronga agr null ha
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipe.postValue(null); //Later on view me check kronga agr null ha
            }


        }
        private Call<RecipeResponse> getRecipe(String recipeId)
        {
            return ServiceGenerator.getRecipeApi().getRecipe(
                    Constants.API_KEY,
                    recipeId
            );
        }
        private void cancelRequest()
        {
            Log.d(TAG, "cancelRequest: Canceling api request");
            cancelRequest=true;

        }
    }
    public void cancelRequest()
    {
        if (mRetrieveRecipesRunnable!=null)
        {
            mRetrieveRecipesRunnable.cancelRequest();
        }
        if (mRetrieveRecipeRunnable!=null)
        {
            mRetrieveRecipeRunnable.cancelRequest();

        }    }

}
