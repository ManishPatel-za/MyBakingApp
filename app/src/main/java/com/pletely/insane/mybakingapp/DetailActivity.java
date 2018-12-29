package com.pletely.insane.mybakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.Group;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pletely.insane.mybakingapp.Adapters.StepsRecyclerAdapter;
import com.pletely.insane.mybakingapp.Pojos.Ingredient;
import com.pletely.insane.mybakingapp.Pojos.Recipe;
import com.pletely.insane.mybakingapp.Pojos.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pletely.insane.mybakingapp.Utils.*;

public class DetailActivity extends AppCompatActivity implements StepsRecyclerAdapter.OnStepClickListener {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private boolean mTwoPane;
    private ArrayList<Recipe> recipes;
    private int recipeId;
    private Bundle ingredientsBundle;
    private Bundle stepsBundle;
    private int stepPosition = 0;

    private TextView textView;

    @BindView(R.id.details_group)
    Group detailsGroup;

    @BindView(R.id.media_group)
    Group mediaGroupContainer;

    @BindView(R.id.nav_left)
    ImageView navLeft;

    @BindView(R.id.nav_right)
    ImageView navRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);


        Intent intent = getIntent();
        if (intent != null) {
            recipes = intent.getParcelableArrayListExtra(KEY_RECIPES);
            recipeId = intent.getIntExtra(KEY_RECIPE_ID, 0);

            ArrayList<Ingredient> ingredients = recipes.get(recipeId).getIngredients();
            ArrayList<Step> steps = recipes.get(recipeId).getSteps();

            ingredientsBundle = new Bundle();
            ingredientsBundle.putParcelableArrayList(KEY_INGREDIENTS, ingredients);
            stepsBundle = new Bundle();
            stepsBundle.putParcelableArrayList(KEY_STEPS, steps);
        }

        //Larger screen mode
        if (findViewById(R.id.two_pane) != null) {
            mTwoPane = true;

            FragmentManager largerFragmentManager = getSupportFragmentManager();
            IngredientsFragment ingredientsFragment = new IngredientsFragment();
            DirectionsFragment directionsFragment = new DirectionsFragment();
            MediaFragment mediaFragment = new MediaFragment();

            ingredientsFragment.setArguments(ingredientsBundle);
            directionsFragment.setArguments(stepsBundle);

            largerFragmentManager.beginTransaction()
                    .add(R.id.ingredients_container, ingredientsFragment)
                    .add(R.id.directions_container, directionsFragment)
                    .add(R.id.media_container,mediaFragment)
                    .commit();

        } else {
            mTwoPane = false;

            IngredientsFragment ingredientsFragment = new IngredientsFragment();
            DirectionsFragment directionsFragment = new DirectionsFragment();

            ingredientsFragment.setArguments(ingredientsBundle);
            directionsFragment.setArguments(stepsBundle);

            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.ingredients_container, ingredientsFragment)
                    .add(R.id.directions_container, directionsFragment)
                    .commit();

            navLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(stepPosition == 0) {
                        Toast.makeText(getApplicationContext(), "You are on the first Step", Toast.LENGTH_LONG).show();
                    } else {
                        stepPosition = stepPosition-1;
                        navigateToMediaScreen(stepPosition);
                    }
                }
            });

            navRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ArrayList<Step> steps = recipes.get(recipeId).getSteps();
                    if(stepPosition == steps.size()-1) {
                        Toast.makeText(getApplicationContext(), "You are on the last Step", Toast.LENGTH_LONG).show();
                    } else {
                        stepPosition = stepPosition +1;
                        navigateToMediaScreen(stepPosition);
                    }
                }
            });
        }

    }

    @Override
    public void onStepClicked(int stepPosition) {

        this.stepPosition = stepPosition;
        navigateToMediaScreen(stepPosition);
    }

    private void navigateToMediaScreen(int stepPosition) {
        Step step = recipes.get(recipeId).getSteps().get(stepPosition);

        FragmentManager fragmentManager = getSupportFragmentManager();
        MediaFragment mediaFragment = new MediaFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_NEW_POSITION, stepPosition);
        bundle.putString(KEY_VIDEO_URI, step.getVideoURL());
        bundle.putString(KEY_THUMBNAIL_URI, step.getThumbnailURL());
        mediaFragment.setArguments(bundle);

        if (mTwoPane ) {

            TextView textView = (TextView) findViewById(R.id.step_description_text);
            textView.setText(step.getDescription());
            fragmentManager.beginTransaction()
                    .replace(R.id.media_container,mediaFragment)
                    .commit();
        } else {

            mediaGroupContainer.setVisibility(View.VISIBLE);
            TextView textView = (TextView) findViewById(R.id.step_description_text);
            textView.setText(step.getDescription());
            fragmentManager.beginTransaction()
                    .replace(R.id.media_container, mediaFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
