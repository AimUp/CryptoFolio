package com.aimarugarte.cryptofolio;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aimarugarte.cryptofolio.Fragments.TransactionsFragment;
import com.aimarugarte.cryptofolio.Fragments.HomeFragment;
import com.aimarugarte.cryptofolio.Fragments.BalanceFragment;
import com.aimarugarte.cryptofolio.Fragments.SettingsFragment;

public class Main2Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static Main2Activity myMain2;
    private HomeFragment homeFragment;
    private TransactionsFragment transactionsFragment;
    private BalanceFragment measuresFragment;
    private SettingsFragment settingsFragment;

    public static Main2Activity getMyMain2(){
        if(myMain2==null){
            myMain2 = new Main2Activity();
        }
        return myMain2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        myMain2 = this;
        homeFragment = new HomeFragment();
        transactionsFragment = new TransactionsFragment();
        measuresFragment = new BalanceFragment();
        settingsFragment = new SettingsFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setFragment(0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //TODO
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setFragment(0);
        } else if (id == R.id.nav_coins) {
            setFragment(1);
        } else if (id == R.id.nav_measures) {
            setFragment(2);
        } else if (id == R.id.nav_settings) {
            setFragment(3);
        } else if (id == R.id.nav_support) {
            //TODO
        } else if (id == R.id.nav_about){
            //TODO
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, homeFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, transactionsFragment);
                fragmentTransaction.commit();
                break;
            case 2:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, measuresFragment);
                fragmentTransaction.commit();
                break;
            case 3:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, settingsFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    public void addCoinToCoinFragment(String coin, String cuantity, String site){
        transactionsFragment.addACoin(coin, cuantity, site);
    }

    public void deleteCoinFromCoinFragment(View v){
        transactionsFragment.deleteAcoin(v);
    }

    public float getBitcoinPrice(){
        return homeFragment.getBitcoin();
    }

    public float getEthereumPrice(){
        return homeFragment.getEthereum();
    }

    public float getLitecoinPrice(){
        return homeFragment.getLitecoin();
    }
}
