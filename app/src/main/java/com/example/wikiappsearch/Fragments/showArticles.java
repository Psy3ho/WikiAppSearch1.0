package com.example.wikiappsearch.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.wikiappsearch.Adapter.ArticleRecyclerAdapter;
import com.example.wikiappsearch.Cards.Article;
import com.example.wikiappsearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class showArticles extends Fragment {

    private List<Article> article_list;
    private RecyclerView article_view_list;
    private ArticleRecyclerAdapter articleRecyclerAdapter;
    private String hladane = "";
    private String language = "sk";
    private int sroffset = 0;

    Button delete;

    public showArticles() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_articles, container, false);
        delete = (Button) view.findViewById(R.id.removeB);

        article_list = new ArrayList<>();
        article_view_list = view.findViewById(R.id.articleslist);
        articleRecyclerAdapter = new ArticleRecyclerAdapter(article_list);
        article_view_list.setLayoutManager(new LinearLayoutManager(container.getContext()));
        article_view_list.setAdapter(articleRecyclerAdapter);
        article_view_list.setHasFixedSize(true);
        Bundle bundle = this.getArguments();


        ///ak nascrolujeme dou na posledny clanok offset sa nam pripocita o 10
        // takze dalsie clanky dynamicky nacitana ked znova spustime fetching
        article_view_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    sroffset +=10;
                    fetchData fetchData = new fetchData();
                    fetchData.execute();
                }
            }
        });

        //znaky podla ktorych hladame a jazyk v ktorom hladame v api wiki
        try {
            if (bundle != null && bundle.getString("hladat") != null) {
                hladane = bundle.getString("hladat");
                fetchData fetchData = new fetchData();
                fetchData.execute();
            }
            if (bundle != null && bundle.getString("language") != null) {
                language = bundle.getString("language");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }



    //nacitanie dat z api wiki podla parametrov
    public class fetchData extends AsyncTask<Void, Void, List<Article>> {

        String data = "";
        String parsedTitle = "";
        String parsedSnippet = "";
        String parsedInformation = "";
        int parsedId;

        @Override
        protected List<Article> doInBackground(Void... voids) {
            try {


                URL url = new URL("https://"+ language + ".wikipedia.org/w/api.php?action=query&list=search&utf8=&sroffset="+sroffset+"&srlimit=10&format=json&srsearch=" + hladane.replaceAll(" ","%20"));

                Log.d("url hladanie  : ",url.toString());
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    data += line;
                }

                JSONObject obj = new JSONObject(data);
                JSONObject query = obj.getJSONObject("query");
                JSONArray JA =  query.getJSONArray("search");

                for (int i = 0; i < JA.length(); i++) {
                    JSONObject JO = JA.getJSONObject(i);
                    parsedTitle = (JO.getString("title"));
                    parsedSnippet = (JO.getString("snippet"));
                    parsedInformation = (JO.getString("timestamp"));
                    parsedId = (JO.getInt("pageid"));
                    Log.d("parsovane id 1  : ",String.valueOf(parsedId));

                    Article article = new Article(parsedId, parsedTitle, parsedSnippet, parsedInformation, language);
                    article_list.add(article);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return article_list;
        }

        @Override
        protected void onPostExecute(List<Article> articleList) {

            if (articleList != null) {

                articleRecyclerAdapter.notifyDataSetChanged();
            }
            else {
                Log.d(TAG, "Zlyhalo parsovanie");
            }

        }
    }




}
