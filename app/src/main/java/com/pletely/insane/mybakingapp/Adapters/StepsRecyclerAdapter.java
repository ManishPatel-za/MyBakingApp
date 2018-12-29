package com.pletely.insane.mybakingapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pletely.insane.mybakingapp.Pojos.Step;
import com.pletely.insane.mybakingapp.R;

import java.util.ArrayList;

public class StepsRecyclerAdapter extends RecyclerView.Adapter<StepsRecyclerAdapter.ViewHolder> {

    private ArrayList<Step> steps;
    private Context context;
    private View view1;

    final public OnStepClickListener onStepClickListener;

    public interface OnStepClickListener {
        void onStepClicked(int stepPosition);
    }

    public StepsRecyclerAdapter(Context context, OnStepClickListener stepClickListener) {
        this.context = context;
        this.onStepClickListener = stepClickListener;
    }

    public void setData(Context context, ArrayList<Step> steps) {
        this.context = context;
        this.steps = steps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view1 = LayoutInflater.from(context).inflate(R.layout.recipe_direction_item, parent, false);
        ViewHolder viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String recipe = steps.get(position).getShortDescription();
        String stepNumber = String.valueOf(position + 1) + ". ";

        holder.mStepsTextView.setText(recipe);
        holder.mNumberTextView.setText(stepNumber);
    }

    @Override
    public int getItemCount() {

        if (steps != null) {
            return steps.size();
        } else {
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mStepsTextView;
        public TextView mNumberTextView;

        public ViewHolder(View v) {
            super(v);

            mStepsTextView = v.findViewById(R.id.direction_textview);
            mNumberTextView = v.findViewById(R.id.steps_number);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickPosition = getAdapterPosition();
            onStepClickListener.onStepClicked(clickPosition);
        }
    }
}



