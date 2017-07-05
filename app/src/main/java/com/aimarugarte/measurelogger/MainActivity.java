package com.aimarugarte.measurelogger;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.aimarugarte.measurelogger.Fragments.HomeFragment;

public class MainActivity extends AppCompatActivity{

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FONT OF THE SPLASH TEXT
        /*TextView t = (TextView) findViewById(R.id.splashtitle);
        Typeface tf = Typeface.createFromAsset(getAssets(), "DitR.ttf");
        t.setTypeface(tf);*/

        Main2Activity m2 = Main2Activity.getMyMain2();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(MainActivity.this,Main2Activity.class);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}