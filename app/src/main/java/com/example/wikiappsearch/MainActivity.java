package com.example.wikiappsearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.SearchView;

import com.example.wikiappsearch.Fragments.savedArticles;
import com.example.wikiappsearch.Fragments.showArticles;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    //ovladacie menu
    private BottomNavigationView mainbottomNav;
    //fragmenty
    private savedArticles savedArticles;
    private showArticles showArticles;
    private FragmentTransaction fragmentTransaction;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //internetove pripojenie
        if(!isConnected(MainActivity.this))buildDialog(MainActivity.this).show();

        //menu dole
        mainbottomNav = findViewById(R.id.mainBottomNav);

        //fragmenty
        savedArticles = new savedArticles();
        showArticles = new showArticles();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        initializeFragments();
        bundle = new Bundle();

        //ovladanie menu dole
        mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.articles_container);

                switch (item.getItemId()) {

                    case R.id.main:

                        replaceFragment(showArticles, currentFragment);
                        return true;

                    case R.id.favourite:

                        replaceFragment(savedArticles, currentFragment);
                        return true;

                    default:
                        return false;

                }

            }
        });



    }



    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //hlavne menu + vyhladavanie v menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if(!isConnected(MainActivity.this))searchView.setVisibility(View.GONE);
                else {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String s) {
                    sendBundle(s);
                    return false;

                }

                @Override
                public boolean onQueryTextChange(String s) {
                    sendBundle(s);
                    return false;
                }

            });
        }

        return true;
    }

    //metody ci sme pripojeny na internet
    public boolean isConnected(MainActivity context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {

            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) {
                return true;
            } else return false;
        } else
            return false;
    }

    //textovy retazec pre hladanie
    public void sendBundle(String s){
        bundle.putString("hladat", s);
        showArticles.setArguments(bundle);
        initializeFragmentSearch();
    }

    //dialogove okno pre internet
    public AlertDialog.Builder buildDialog(MainActivity c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(R.string.no_internet);
        builder.setMessage(R.string.no_interner1);

        builder.setPositiveButton(R.string.no_internet_button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        return builder;
    }


    //polozka vybrata z menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.cz:
                setBundle("cz");
                return true;
            case R.id.sk:
                setBundle("sk");
                return true;
            case R.id.pl:
                setBundle("pl");
                return true;
            case R.id.en:
                setBundle("en");
                return true;
            case R.id.de:
                setBundle("de");
                return true;
            default:

                return false;
        }
    }

    //bundle pre jazyk
    private void setBundle(String language){
        bundle.putString("language", language);
    }

    //obnovenie fragmentu hladania
    private void initializeFragmentSearch() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(showArticles);
        fragmentTransaction.attach(showArticles);
        fragmentTransaction.show(showArticles);
        fragmentTransaction.hide(savedArticles);
        fragmentTransaction.commit();
    }

    //spustenie fragmentov
    private void initializeFragments() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.articles_container, showArticles);
        fragmentTransaction.add(R.id.articles_container, savedArticles);
        fragmentTransaction.detach(savedArticles);
        fragmentTransaction.attach(savedArticles);
        fragmentTransaction.hide(showArticles);
        fragmentTransaction.commit();
    }

    //prehodenie zobrazovaneho fragmentu
    private void replaceFragment(Fragment fragment, Fragment currentFragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == savedArticles){
            fragmentTransaction.detach(savedArticles);
            fragmentTransaction.attach(savedArticles);
            fragmentTransaction.hide(showArticles);
        }
        if(fragment == showArticles){
            fragmentTransaction.hide(savedArticles);
        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

}
