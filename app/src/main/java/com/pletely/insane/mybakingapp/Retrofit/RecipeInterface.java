package com.pletely.insane.mybakingapp.Retrofit;

import com.pletely.insane.mybakingapp.Pojos.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeInterface {

    @GET("baking.json")
    Call<ArrayList<Recipe>> getRecipes();
}
