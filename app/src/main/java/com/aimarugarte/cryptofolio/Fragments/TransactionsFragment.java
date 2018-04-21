package com.aimarugarte.cryptofolio.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aimarugarte.cryptofolio.Fragments.Coins.SwipeFrames;
import com.aimarugarte.cryptofolio.Listeners.OnSwipeTouchListener;
import com.aimarugarte.cryptofolio.Main2Activity;
import com.aimarugarte.cryptofolio.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class TransactionsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private LinearLayout ll;
    private String dataFilename = "coinsDataFile";
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_transactions, container, false);
        ((Main2Activity) getActivity()).getSupportActionBar().setTitle("Coins");

        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent k = new Intent(getActivity(), SwipeFrames.class);
                startActivity(k);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeRefreshLayoutCoins);
        swipeRefreshLayout.setOnRefreshListener(this);

        ll = (LinearLayout) view.findViewById(R.id.coinsHolder);
        //Check the dataFile exist and create it if it dont exist
        File f = getContext().getApplicationContext().getFileStreamPath(dataFilename);
        if(f == null || !f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            readCoinData();
        }

        loadPrices();

        return view;
    }

    @Override
    public void onRefresh() {
        loadPrices();
        swipeRefreshLayout.setRefreshing(false);
    }

    //ADDING COINS
    public void addACoin(String coin, String cuantity, String site){
        String number = String.valueOf(fileLines(dataFilename));
        addCoin(number ,coin, cuantity, site);
        addCoinInternally(number ,coin, cuantity, site);
    }

    private void addCoin(String number, String coin, String quantity, String site){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, Math.round(dpToPixel(-2)), 0, 0);

        LinearLayout l1 = new LinearLayout(getContext());
        l1.setOrientation(LinearLayout.VERTICAL);
        l1.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        int p = Math.round(dpToPixel(10));
        l1.setPadding(p,p,p,p);
        l1.setTag(number);
        l1.setOnTouchListener(new OnSwipeTouchListener(getContext()));

        TextView t1 = new TextView(getContext());
        t1.setGravity(Gravity.CENTER);
        t1.setTextSize(30);
        t1.setTextColor(getResources().getColor(R.color.Background));
        String text1 = quantity+ " " + coin;
        t1.setText(text1);

        TextView t2 = new TextView(getContext());
        t2.setGravity(Gravity.CENTER);
        t2.setTextSize(15);
        t2.setTextColor(getResources().getColor(R.color.Background));
        float eurPrice = calculatePrice(coin, Float.parseFloat(quantity));
        String text2 = site + " " + eurPrice + "€";
        t2.setText(text2);

        l1.addView(t1);
        l1.addView(t2);
        ll.addView(l1,params);

        loadPrices();
    }

    private void addCoinInternally(String number, String coin, String cuantity, String site){
        FileOutputStream outputStream;
        try {
            outputStream = getContext().getApplicationContext().openFileOutput(dataFilename, Context.MODE_APPEND);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String date = sdf.format(Calendar.getInstance().getTime());
            //System.out.println(date);
            String string =  number + "|" + coin + "|" + cuantity + "|" + site + "|" + date + "\n";
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //DELETING COINS
    public void deleteAcoin(View v){
        try {
            deleteCoin(v);
            deleteCoinInternally((String) v.getTag());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteCoin(View view){
        LinearLayout parent = (LinearLayout) view.getParent();
        parent.removeView(view);
        loadPrices();
    }

    private void deleteCoinInternally(String number) throws IOException {
        List<String> lines = new LinkedList<>();
        FileInputStream inputStream;
        FileOutputStream outputStream;
        try {
            inputStream = getContext().getApplicationContext().openFileInput(dataFilename);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                StringTokenizer tokens = new StringTokenizer(line, "|");
                if (!tokens.nextToken().equals(number)){
                    lines.add(line);
                }
            }
            inputStream.close();

            outputStream = getContext().getApplicationContext().openFileOutput(dataFilename, Context.MODE_PRIVATE);
            if (lines.isEmpty()){
                outputStream.write("".getBytes());
            }
            else {
                for (String l : lines) {
                    outputStream.write((l+"\n").getBytes());
                }
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //LOADING COINS
    private void readCoinData(){
        FileInputStream inputStream;
        try {
            inputStream = getContext().getApplicationContext().openFileInput(dataFilename);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
               //System.out.pfrintln(line);
                StringTokenizer tokens = new StringTokenizer(line, "|");
                addCoin(tokens.nextToken(), tokens.nextToken(), tokens.nextToken(), tokens.nextToken());
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPrices(){
        float total = 0;
        LinearLayout laux;
        for(int i=0; i<ll.getChildCount(); i++){
            laux = (LinearLayout) ll.getChildAt(i);
            TextView taux1 = (TextView) laux.getChildAt(0);
            StringTokenizer tokens = new StringTokenizer(taux1.getText().toString(), " ");
            String quantity = tokens.nextToken();
            String coin = tokens.nextToken();
            TextView taux2 = (TextView) laux.getChildAt(1);
            float coinTotal = 0;
            coinTotal = calculatePrice(coin, Float.parseFloat(quantity));
            total += coinTotal;
            String text2 = "coinbase " + coinTotal +"€";
            taux2.setText(text2);
        }
        TextView totalTV = (TextView) view.findViewById(R.id.totalCoinsValue);
        totalTV.setText(round(total) + "€");
    }

    private float calculatePrice(String coin, float quantity){
        float coinTotal = 0;
        switch (coin){
            case "BTC": coinTotal = Main2Activity.getMyMain2().getBitcoinPrice()*quantity;
                break;
            case "ETH": coinTotal = Main2Activity.getMyMain2().getEthereumPrice()*quantity;
                break;
            case "LTC": coinTotal = Main2Activity.getMyMain2().getLitecoinPrice()*quantity;
                break;
        }
        return round(coinTotal);
    }

    //HELPING METHODS
    private int fileLines(String filename){
        int lines = 0;
        try{
            LineNumberReader lnr = new LineNumberReader(new InputStreamReader(getContext().getApplicationContext().openFileInput(filename)));
            lnr.skip(Long.MAX_VALUE);
            lines = lnr.getLineNumber();
            lnr.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return lines;
    }

    private float round(float number){
        DecimalFormat df = new DecimalFormat("#.##");
        return Float.parseFloat(df.format(number));
    }

    private float dpToPixel(int dp){
        float px;
        Resources r = getResources();
        px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }
}