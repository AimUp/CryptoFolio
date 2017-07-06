package com.aimarugarte.cryptofolio.Fragments;

/**
 * Created by AIMAR on 14/6/17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aimarugarte.cryptofolio.Main2Activity;
import com.aimarugarte.cryptofolio.R;

public class BalanceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_measures, container, false);
        ((Main2Activity) getActivity()).getSupportActionBar().setTitle("Balance");


        return view;
    }

}