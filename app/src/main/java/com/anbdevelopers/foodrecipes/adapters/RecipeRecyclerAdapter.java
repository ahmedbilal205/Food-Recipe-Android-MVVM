package com.anbdevelopers.foodrecipes.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anbdevelopers.foodrecipes.R;
import com.anbdevelopers.foodrecipes.models.Recipe;
import com.anbdevelopers.foodrecipes.util.Constants;
import com.anbdevelopers.foodrecipes.vewmodels.RecipeListViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String LOADING = "Loading...";
    private static final int RECIPE_TYPE= 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORY_TYPE = 3;
    private static final int EXHAUSTED_TYPE = 4;
    private static final String EXHAUSTED = "EXHAUSTED...";


    private List<Recipe> mRecipes;
    private OnRecipeListener mOnRecipeListener;

    public RecipeRecyclerAdapter(OnRecipeListener mOnRecipeListener)
    {
        this.mOnRecipeListener = mOnRecipeListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;
        switch (viewType)
        {

            case LOADING_TYPE:
                {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item,parent,false);
                    return new LoadingViewHolder(view);
                }
            case EXHAUSTED_TYPE:
            {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_exhausted,parent,false);
                return new SearchExhaustedViewHolder(view);
            }
            case CATEGORY_TYPE:
                {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list_item,parent,false);
                    return new CategoryViewHolder(view, mOnRecipeListener);
                }
            case RECIPE_TYPE:
            default:
                {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recipe_list_item,parent,false);
                    return new RecipeViewHolder(view, mOnRecipeListener);
                }
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        int itemViewType = getItemViewType(position);
        if (itemViewType==RECIPE_TYPE)
        {
            //Basically holder ko cast kr rhe ha apne custom "RecipeViewHolder" which extends normal recyclerviewHolder so it works
            ((RecipeViewHolder)holder).title.setText(mRecipes.get(position).getTitle());
            ((RecipeViewHolder)holder).publisher.setText(mRecipes.get(position).getPublisher());
            ((RecipeViewHolder)holder).socialScore.setText(String.valueOf(Math.round(mRecipes.get(position).getSocial_rank())));

            Glide.with(holder.itemView.getContext()).load(mRecipes.get(position).getImage_url())
                    .placeholder(R.drawable.downloading_icon).into(((RecipeViewHolder)holder).image);
        }else if (itemViewType==CATEGORY_TYPE)
        {
            //Basically holder ko cast kr rhe ha apne custom "RecipeViewHolder" which extends normal recyclerviewHolder so it works
            ((CategoryViewHolder)holder).categoryTitle.setText(mRecipes.get(position).getTitle());
            Uri path = Uri.parse("android.resource://com.anbdevelopers.foodrecipes/drawable/" + mRecipes.get(position).getImage_url());
            Glide.with(holder.itemView.getContext()).load(path)
                    .placeholder(R.drawable.downloading_icon).into(((CategoryViewHolder)holder).categoryImage);
        }
        //Loading wale ki zarorat hi nhi

    }

    @Override
    public int getItemViewType(int position)
    {
        if (mRecipes.get(position).getTitle().equals(LOADING))
        {
            return  LOADING_TYPE;
        }else if (mRecipes.get(position).getSocial_rank()==-1)
        {
            return CATEGORY_TYPE;
        }
        else if (position==mRecipes.size()-1
                &&mRecipes.get(position).getTitle().equals(EXHAUSTED))
        {
            return EXHAUSTED_TYPE;
        }
        else if (position==mRecipes.size()-1
        &&position!=0
        && !mRecipes.get(position).getTitle().equals(EXHAUSTED))
        {
            return LOADING_TYPE;
        }

        else return RECIPE_TYPE;
    }

    public void setQueryExhausted()
    {
        //First check if loading
        if (isLoading())
        {
            for (Recipe recipe: mRecipes)
            {
                if (recipe.getTitle().equals(LOADING))
                {
                    mRecipes.remove(recipe);
                }
            }
            notifyDataSetChanged();
        }
        //Now adding end of list view
        Recipe exhaustedRecipe = new Recipe();
        exhaustedRecipe.setTitle(EXHAUSTED);
        mRecipes.add(exhaustedRecipe);
        notifyDataSetChanged();

    }
    public void displayLoading()
    {
        if (!isLoading())
        {
            Recipe recipe = new Recipe();
            recipe.setTitle(LOADING);
            List<Recipe> loadingList = new ArrayList<>();
            loadingList.add(recipe);
            mRecipes = loadingList;
            notifyDataSetChanged();
        }
    }
    private boolean isLoading()
    {
        if (mRecipes!=null)
        {
            if (mRecipes.size()>0)
            {
                return mRecipes.get(mRecipes.size() - 1).getTitle().equals(LOADING);
            }else return false;
        }else return false;
    }

    public void displaySearchCategories()
    {
        List<Recipe> categories = new ArrayList<>();
        for (int i=0; i< Constants.DEFAULT_SEARCH_CATEGORIES.length;i++)
        {
            Recipe recipe = new Recipe();
            recipe.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            recipe.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            recipe.setSocial_rank(-1);
            categories.add(recipe);
        }
        mRecipes=categories;
        notifyDataSetChanged();

    }
    @Override
    public int getItemCount()
    {
        if (mRecipes!=null) {return mRecipes.size();}
        else {return 0;}
    }
    public void setRecipes(List<Recipe> recipes)
    {
        mRecipes = recipes;
        notifyDataSetChanged();
    }
    public Recipe getSelectedRecipe(int position)
    {
        if (mRecipes!=null)
        {
            if (mRecipes.size()>0)
            {
                return mRecipes.get(position);

            }
        }
    return null;
    }
}
