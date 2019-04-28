package com.example.wikiappsearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //menu dole
        mainbottomNav = findViewById(R.id.mainBottomNav);

        //fragmenty
        savedArticles = new savedArticles();
        showArticles = new showArticles();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        initializeFragments();


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



    //hlavne menu + vyhladavanie v menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;

            }

            @Override
            public boolean onQueryTextChange(String s) {

                Bundle bundle = new Bundle();
                Log.d("nieco co hladame : ",s);
                bundle.putString("hladat", s);
                showArticles.setArguments(bundle);
                initializeFragmentSearch();
                return false;
            }

        });
        return true;
    }

    //obnovenie fragmentu hladania
    private void initializeFragmentSearch() {
        fragmentTransaction.detach(showArticles);
        fragmentTransaction.attach(showArticles);
        fragmentTransaction.show(showArticles);
        fragmentTransaction.hide(savedArticles);
    }

    //spustenie fragmentov
    private void initializeFragments() {
        fragmentTransaction.add(R.id.articles_container, showArticles);
        fragmentTransaction.add(R.id.articles_container, savedArticles);
        fragmentTransaction.hide(savedArticles);
        fragmentTransaction.commit();
    }

    //prehodenie zobrazovaneho fragmentu
    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        if(fragment == savedArticles){
            fragmentTransaction.hide(showArticles);
        }
        if(fragment == showArticles){
            fragmentTransaction.hide(savedArticles);
        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

}
