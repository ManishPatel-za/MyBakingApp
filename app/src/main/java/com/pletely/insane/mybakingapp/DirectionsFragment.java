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

import com.pletely.insane.mybakingapp.Adapters.StepsRecyclerAdapter;
import com.pletely.insane.mybakingapp.Pojos.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pletely.insane.mybakingapp.Utils.KEY_STEPS;


public class DirectionsFragment extends Fragment {

    @BindView(R.id.directions_recyclerview)
    RecyclerView mRecyclerView;

    private ArrayList<Step> steps;

    public DirectionsFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_directions, container, false);
        ButterKnife.bind(this, rootView);

        StepsRecyclerAdapter mRecyclerViewAdapter = new StepsRecyclerAdapter(getActivity(), (DetailActivity) getActivity());

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        Bundle stepsBundle = getArguments();
        if (stepsBundle != null) {
            steps = stepsBundle.getParcelableArrayList(KEY_STEPS);
            mRecyclerViewAdapter.setData(getActivity(), steps);
        }
        return rootView;
    }


}
