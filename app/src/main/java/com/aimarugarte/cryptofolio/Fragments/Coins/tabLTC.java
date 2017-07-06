package com.aimarugarte.cryptofolio.Fragments.Coins;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.aimarugarte.cryptofolio.Main2Activity;
import com.aimarugarte.cryptofolio.R;

/**
 * Created by AIMAR on 15/6/17.
 */

public class tabLTC extends Fragment implements View.OnClickListener{

    EditText quantity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_ltc, container, false);

        Button ok = (Button) view.findViewById(R.id.ltcOK);
        ok.setOnClickListener(this);
        quantity = (EditText) view.findViewById(R.id.LTCquantity);


        return view;
    }

    @Override
    public void onClick(View view) {
        String quantityStr = quantity.getText().toString();
        if(!(quantityStr.equals("") || quantityStr.equals("0"))){
            Main2Activity.getMyMain2().addCoinToCoinFragment("LTC", quantity.getText().toString(), "Coinbase");
        }
    }
}
