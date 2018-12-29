package com.pletely.insane.mybakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pletely.insane.mybakingapp.Pojos.Ingredient;
import com.pletely.insane.mybakingapp.Widget.UpdateIngredientsService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pletely.insane.mybakingapp.Utils.KEY_INGREDIENTS;


public class IngredientsFragment extends Fragment {

    @BindView(R.id.ingredients_recyclerview)
    RecyclerView mRecyclerView;

    private ArrayList<Ingredient> ingredients;

    public IngredientsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);
        ButterKnife.bind(this, rootView);

        RecyclerViewAdapter mRecyclerViewAdapter = new RecyclerViewAdapter(getActivity());

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        Bundle bundle = getArguments();
        if (bundle != null) {
            ingredients = bundle.getParcelableArrayList(KEY_INGREDIENTS);
            mRecyclerViewAdapter.setIngredientData(getContext(), ingredients);

            ArrayList<String> ingredientsForWidget =new ArrayList<>();
            for(Ingredient ingr : ingredients) {
                ingredientsForWidget.add(ingr.getIngredient() + "\n" +
                "Quantity: " +ingr.getQuantity() + "\n" +
                "Unit: " + ingr.getMeasure() + "\n");
            }
            UpdateIngredientsService.startIngredientsService(getContext(), ingredientsForWidget);
        }

        return rootView;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

        Context context;
        View view1;
        ViewHolder viewHolder1;
        ArrayList<Ingredient> ingredients;

        private RecyclerViewAdapter(Context context) {
            this.context = context;
        }

        private void setIngredientData(Context context, ArrayList<Ingredient> ingredients) {
            this.context = context;
            this.ingredients = ingredients;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            view1 = LayoutInflater.from(context).inflate(R.layout.recipe_ingredient_item, parent, false);
            viewHolder1 = new ViewHolder(view1);
            return viewHolder1;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            String title = ingredients.get(position).getIngredient();
            String quantity = ingredients.get(position).getQuantity();
            String measurement = ingredients.get(position).getMeasure();

            String ingredientNumber = String.valueOf(position + 1) + ". ";
            holder.mTitleNumber.setText(ingredientNumber);
            holder.mTitles.setText(title);
            holder.mQuantity.setText(quantity);
            holder.mmeasurement.setText(measurement);
        }

        @Override
        public int getItemCount() {

            if (ingredients != null && ingredients.size() > 0) {
                return ingredients.size();
            } else {
                return 0;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleNumber;
        public TextView mTitles;
        public TextView mQuantity;
        public TextView mmeasurement;

        public ViewHolder(View v) {
            super(v);

            mTitleNumber = v.findViewById(R.id.ingredient_number);
            mTitles = v.findViewById(R.id.ingredient_title);
            mQuantity = v.findViewById(R.id.ingredient_quantity);
            mmeasurement = v.findViewById(R.id.ingredient_measurement);
        }
    }
}
