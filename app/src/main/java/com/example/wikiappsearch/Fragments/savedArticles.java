package com.example.wikiappsearch.Fragments;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class savedArticles extends Fragment implements Serializable {

    private List<Article> article_list;
    private RecyclerView article_view_list;
    private ArticleRecyclerAdapter articleRecyclerAdapter;
    Button delete;

    public savedArticles() {
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

        String parsedTitle;
        String parsedSnippet;
        String parsedInformation;
        int parsedId;


        try {


            //zobrazenie ulozenycch clankou pre adapter
            File f = new File(Objects.requireNonNull(getContext()).getFilesDir().getPath()+ "/articleJS.json");
            if(f.exists()){
                JSONObject jsonParent = new JSONObject(loadFile(f));
                JSONArray jsonArr = jsonParent.getJSONArray("ListArticles");
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonArticle = jsonArr.getJSONObject(i);
                    parsedSnippet = (jsonArticle.getString("content"));
                    parsedInformation = (jsonArticle.getString("information"));
                    parsedTitle = (jsonArticle.getString("title"));
                    parsedId = (jsonArticle.getInt("id"));
                    String language = "";

                    Article article = new Article(parsedId, parsedTitle, parsedSnippet, parsedInformation, language);
                    article_list.add(article);
                }



                Log.d("saved vypis : ",jsonParent.toString());

            }


        }
        catch(JSONException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        articleRecyclerAdapter.notifyDataSetChanged();
        return view;
    }


    //nacitanie suboru json
    private String loadFile(File f) throws IOException {
        FileInputStream is = new FileInputStream(f);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String mResponse = new String(buffer);
        return mResponse;
    }


}
