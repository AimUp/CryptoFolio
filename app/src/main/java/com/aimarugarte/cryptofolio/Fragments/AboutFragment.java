package com.aimarugarte.cryptofolio.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aimarugarte.cryptofolio.Main2Activity;
import com.aimarugarte.cryptofolio.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_about, container, false);
        ((Main2Activity) getActivity()).getSupportActionBar().setTitle("About");

        Button github_btn = (Button) view.findViewById(R.id.github_btn);
        github_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

}