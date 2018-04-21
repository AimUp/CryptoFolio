package com.aimarugarte.cryptofolio.Fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.aimarugarte.cryptofolio.Main2Activity;
import com.aimarugarte.cryptofolio.R;
import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.LineChartView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    SwipeRefreshLayout swipeRefreshLayout;
    TextView btcText;
    TextView ethText;
    TextView ltcText;
    LineChartView lchart;

    ProgressDialog pd;
    View view;

    boolean ethCharCreated;
    String BTCChartData;
    String ETHChartData;
    String[] st;
    float[] ft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ((Main2Activity) getActivity()).getSupportActionBar().setTitle("Home");

        btcText = (TextView) view.findViewById(R.id.btcPrice);
        ethText = (TextView) view.findViewById(R.id.ethPrice);
        ltcText = (TextView) view.findViewById(R.id.ltcPrice);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        //TODO comprobar conexion para que no crashee cuando no hay
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Please wait");
        pd.setCancelable(true);
        loadPrice();

        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        BTCChartData = "http://api.coindesk.com/charts/data?output=csv&data=close&index=USD&startdate=2016-07-06&enddate="+fDate+"&exchanges=bpi&dev=1";
        ETHChartData = "http://api.coindesk.com/charts/data?output=csv&data=close&index=ETH&startdate=2016-07-06&enddate="+fDate+"&exchanges=bpi&dev=1";

        lchart = (LineChartView) view.findViewById(R.id.linechartBTC);
        new CsvTask().execute(BTCChartData);

        ethCharCreated = false;

        Switch homeSwitch = (Switch) view.findViewById(R.id.homeSwitch);
        homeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    lchart.setVisibility(View.GONE);
                    lchart = (LineChartView) view.findViewById(R.id.linechartETH);
                    if(!ethCharCreated){
                        ethCharCreated = true;
                        new CsvTask().execute(ETHChartData);
                    }
                    lchart.setVisibility(View.VISIBLE);
                }
                else{
                    lchart.setVisibility(View.GONE);
                    lchart = (LineChartView) view.findViewById(R.id.linechartBTC);
                    lchart.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    public float getBitcoin(){
        String price = btcText.getText().toString();
        return Float.parseFloat(price.substring(0, price.length() - 1));
    }

    public float getEthereum(){
        String price = ethText.getText().toString();
        return Float.parseFloat(price.substring(0, price.length() - 1));
    }

    public float getLitecoin(){
        String price = ltcText.getText().toString();
        return Float.parseFloat(price.substring(0, price.length() - 1));
    }

    private void loadPrice(){
        pd.show();
        actualizePrize();
    }

    private void actualizePrize(){
        new JsonTask().execute("bitcoin","eur");
        new JsonTask().execute("ethereum","eur");
        new JsonTask().execute("litecoin","eur");
    }

    @Override
    public void onRefresh() {
        actualizePrize();
    }

    private int minValue(float[] ft){
        int min = Math.round(ft[0]);
        for (float i : ft){
            if(i < min) min = Math.round(i);
        }
        return min;
    }

    private int maxValue(float[] ft){
        int max = 0;
        for (float i : ft){
            if(i > max) max = Math.round(i);
        }
        return max;
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;
        String currency;
        String coin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            coin = args[0];
            currency = args[1];

            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("https://api.coinmarketcap.com/v1/ticker/"+args[0]+"/?convert="+args[1]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jObject;
            JSONArray jArray;
            try {
                jArray = new JSONArray(result);
                jObject = jArray.getJSONObject(0);
                String amount = jObject.getString("price_"+currency);
                amount = round(Float.parseFloat(amount)).toString() + "€";
                switch (coin){
                    case "bitcoin":
                        btcText.setText(amount);
                        break;
                    case "ethereum":
                        ethText.setText(amount);
                        break;
                    case "litecoin":
                        ltcText.setText(amount);
                        if (pd.isShowing()){
                            pd.dismiss();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class CsvTask extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;
        ArrayList<String> arrayString;
        ArrayList<Float> arrayFloat;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            arrayString = new ArrayList<String>();
            arrayFloat = new ArrayList<Float>();
        }

        @Override
            protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(args[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                try {
                    yearData(reader);
                }
                catch (IOException ex) {ex.printStackTrace();}
                finally {
                    try {is.close();}
                    catch (IOException e) { e.printStackTrace();}
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return "";
        }

        private void yearData(BufferedReader reader) throws IOException {
            String line;
            String actualMonth = "";
            boolean p = false;
            while ((line = reader.readLine()) != null) {
                if(p){
                    StringTokenizer tokens = new StringTokenizer(line, ",");
                    if (tokens.countTokens() == 2){
                        String date = tokens.nextToken();
                        String price = tokens.nextToken();
                        StringTokenizer dataToken = new StringTokenizer(date, "-");
                        String year = dataToken.nextToken();
                        String month = dataToken.nextToken();
                        String dayHour = dataToken.nextToken();
                        StringTokenizer dayToken = new StringTokenizer(dayHour, " ");
                        String day = dayToken.nextToken();
                        if(day.equals("15") && !month.equals(actualMonth)){
                            actualMonth = month;
                            arrayString.add(year.substring(Math.max(year.length() - 2, 0)) + "/" +actualMonth);
                        }
                        else arrayString.add("");
                        arrayFloat.add(Float.parseFloat(price));
                    }
                }
                else p = true;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            st = new String[arrayString.size()];
            st = arrayString.toArray(st);

            ft = new float[arrayFloat.size()];
            int i = 0;
            for (Float f : arrayFloat) {
                ft[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
            }

            LineSet dataset = new LineSet(st,ft);
            dataset.setColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            //dataset.setFill(ResourcesCompat.getColor(getResources(), R.color.grapFill, null));
            dataset.setThickness(5);

            lchart.addData(dataset);
            lchart.setFontSize(20);
            lchart.setXAxis(false);
            lchart.setYAxis(false);
            lchart.setAxisColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            lchart.setLabelsColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            lchart.setAxisBorderValues(minValue(ft), maxValue(ft));
            lchart.setYLabels(AxisRenderer.LabelPosition.INSIDE);
            Animation anim = new Animation(1000);
            lchart.show(anim);
        }
    }

    private Float round(Float number){
        DecimalFormat df = new DecimalFormat("#.##");
        return Float.parseFloat(df.format(number));
    }
}