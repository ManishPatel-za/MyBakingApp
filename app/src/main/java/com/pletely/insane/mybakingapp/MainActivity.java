package com.pletely.insane.mybakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pletely.insane.mybakingapp.Pojos.Recipe;
import com.pletely.insane.mybakingapp.Retrofit.RecipeInterface;
import com.pletely.insane.mybakingapp.Retrofit.RetrofitClientInstance;
import com.pletely.insane.mybakingapp.SimpleIdlingResource.SimpleIdlingResource;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pletely.insane.mybakingapp.Utils.KEY_RECIPES;
import static com.pletely.insane.mybakingapp.Utils.KEY_RECIPE_ID;

public class MainActivity extends AppCompatActivity {

    @Nullable
    private SimpleIdlingResource simpleIdlingResource;


    @BindView(R.id.recipes_recycler)
    RecyclerView mRecyclerView;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (findViewById(R.id.tablet_mode) != null) {

            GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 3, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (simpleIdlingResource != null) {
            simpleIdlingResource.setIdleState(false);
        }

        //Run call to get data from server after oncreated is called giving enough time for idle resource @begin to be called.
        final RecyclerViewAdapter mRecyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        RecipeInterface recipeInterface = RetrofitClientInstance.getRetrofitInstance().create(RecipeInterface.class);

        Call<ArrayList<Recipe>> recipe = recipeInterface.getRecipes();

        recipe.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call,
                                   Response<ArrayList<Recipe>> response) {

                Integer statusCode = response.code();
                Log.v("status code: ", statusCode.toString());

                ArrayList<Recipe> recipes = response.body();

                Bundle recipesBundle = new Bundle();
                recipesBundle.putParcelableArrayList(
                        KEY_RECIPES, recipes);

                int recipeCount = recipes.size();

                Log.d(TAG, "setting recipe data");

                mRecyclerViewAdapter.setRecipeData(getApplication(), recipes);

                if (simpleIdlingResource != null) {
                    simpleIdlingResource.setIdleState(true);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call,
                                  Throwable t) {
                Log.v("http fail: ", t.getMessage());
            }
        });
    }

    public IdlingResource getIdlingResource() {
        if (simpleIdlingResource == null) {
            simpleIdlingResource = new SimpleIdlingResource();
            return simpleIdlingResource;
        }
        return null;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

        Context context;
        View view;
        ViewHolder viewHolder;
        ArrayList<Recipe> recipes;

        private RecyclerViewAdapter(Context context) {

            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Log.d(TAG, "onCreateViewHolder");
            view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false);
            viewHolder = new ViewHolder(view);


            return viewHolder;
        }

        public void setRecipeData(Context context, ArrayList<Recipe> recipes) {
            this.context = context;
            this.recipes = recipes;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            String recipe = recipes.get(position).getName();
            Log.d(TAG, "OnBindViewHolder");

            holder.mTextView.setText(recipe);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                    if (recipes != null && recipes.size() > 0) {
                        intent.putParcelableArrayListExtra(KEY_RECIPES, recipes);
                        intent.putExtra(KEY_RECIPE_ID, position);
                    }

                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return recipes != null ? recipes.size() : 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);

            mTextView = v.findViewById(R.id.recipe_textview);
        }
    }
}
