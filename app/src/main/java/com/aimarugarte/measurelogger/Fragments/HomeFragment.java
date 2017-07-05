package com.aimarugarte.measurelogger.Fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aimarugarte.measurelogger.Main2Activity;
import com.aimarugarte.measurelogger.R;
import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.LineChartView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    SwipeRefreshLayout swipeRefreshLayout;

    TextView btcText;
    TextView ethText;
    TextView ltcText;

    ProgressDialog pd;

    View view;

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

        LineChartView lchart = (LineChartView) view.findViewById(R.id.linechart);
        String[] st = {"17/01","17/02","17/03","17/04", "17/05", "17/06","17/07","17/08","17/09", "17/10", "17/11", "17/12"};
        float[] ft = {100, 120, 150,200, 300, 290, 300, 320, 500, 450,510,600};
        LineSet dataset = new LineSet(st,ft);
        dataset.setColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        //dataset.setFill(ResourcesCompat.getColor(getResources(), R.color.grapFill, null));
        dataset.setThickness(5);

        lchart.addData(dataset);
        lchart.setFontSize(25);
        lchart.setXAxis(false);
        lchart.setYAxis(false);
        lchart.setAxisColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        lchart.setLabelsColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        Animation anim = new Animation(1000);
        lchart.show(anim);

        return view;
    }

    private void loadPrice(){
        pd.show();
        actualizePrize();
    }

    private void actualizePrize(){
        new JsonTask().execute("BTC","EUR");
        new JsonTask().execute("ETH","EUR");
        new JsonTask().execute("LTC","EUR");
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        String coin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            coin = args[0];

            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL("https://api.coinbase.com/v2/prices/"+args[0]+"-"+args[1]+"/sell");
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
            try {
                jObject = new JSONObject(result);
                String aJsonString = jObject.getString("data");
                jObject = new JSONObject(aJsonString);

                String amount = jObject.getString("amount");
                amount= amount + "€";
                switch (coin){
                    case "BTC":
                        btcText.setText(amount);
                        break;
                    case "ETH":
                        ethText.setText(amount);
                        break;
                    case "LTC":
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

    @Override
    public void onRefresh() {
        actualizePrize();
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                System.out.println("Error" + e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if(result != null){
                result = Bitmap.createBitmap(result, 7, 44, result.getWidth()-14, result.getHeight()-88);
                bmImage.setImageBitmap(result);
            }
        }
    }
}