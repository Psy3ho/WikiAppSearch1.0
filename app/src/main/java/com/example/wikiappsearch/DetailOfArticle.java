package com.example.wikiappsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.wikiappsearch.Cards.Article;
import com.example.wikiappsearch.Fragments.showArticles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DetailOfArticle extends AppCompatActivity {


    private TextView title;
    private TextView content;
    private TextView information;
    private int idArticle;
    private Article article;
    private String language;
    private static final String filename = "articleJS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_of_article);

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        information = findViewById(R.id.information);



        //zobrazenie detailu clanku
        try {
            idArticle = getIntent().getIntExtra("IdArticle",0);
            language = getIntent().getStringExtra("language");
            Log.d("parsovane id 3  : ",language);
            fetchData fetchData = new fetchData();
            fetchData.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    //pripojenie sa na api Wiki pre jeden clanok podla id
    public class fetchData extends AsyncTask<Void, Void, Article> {

        String data = "";
        String parsedTitle = "";
        String parsedContent = "";
        String parsedInformation = "";

        @Override
        protected Article doInBackground(Void... voids) {
            try {

                //api na jeden clanok
                URL url = new URL("https://"+ language +".wikipedia.org/w/api.php?action=query&prop=extracts&exintro=&format=json&pageids=" + idArticle);

                Log.d("url  : ",url.toString());
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
                JSONObject pages = query.getJSONObject("pages");
                JSONObject JO =  pages.getJSONObject(String.valueOf(idArticle));


                parsedTitle = (JO.getString("title"));
                parsedContent = (JO.getString("extract"));

                article = new Article(idArticle, parsedTitle, parsedContent, parsedInformation, language);



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return article;
        }

        @Override
        protected void onPostExecute(Article article) {

            if (article != null) {
                title.setText(article.getTitle());
                content.setText(Html.fromHtml(article.getContent()));

            }
            else {
                Log.d(TAG, "Zlyhalo parsovanie");
            }

        }
    }


    //ulozenie clanku do json suboru -- osetrene pre kopie
    public void save(View view) {

        try {

            JSONObject jsonParent = new JSONObject();
            JSONArray jsonArr = new JSONArray();
            JSONObject jsonArticle = new JSONObject();
            jsonArticle.put("title", article.getTitle());
            jsonArticle.put("content", article.getContent());
            jsonArticle.put("information", article.getInformation());
            jsonArticle.put("id", article.getId());

            File f = new File(this.getFilesDir().getPath()+ "/"+filename+".json");

            if(f.exists()){
                jsonParent = new JSONObject(loadFile(f));
                jsonArr = jsonParent.getJSONArray("ListArticles");

                JSONObject jsonArticlesaved;

                boolean compare = false;

                for (int i = 0; i < jsonArr.length(); i++) {
                    jsonArticlesaved = jsonArr.getJSONObject(i);
                    if(jsonArticle.getString("id").equals(jsonArticlesaved.getString("id"))){
                        compare =true;
                    }

                }
                if(!compare){
                    jsonArr.put(jsonArticle);
                }

                saveFile(jsonParent);

            } else {
                jsonParent.put("ListArticles", jsonArr);
                jsonArr.put(jsonArticle);

                saveFile(jsonParent);

            }

        }
        catch(JSONException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    //option menu spat tlacidlo
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_button:
                startActivity(new Intent(DetailOfArticle.this, MainActivity.class));
                return true;
            default:
                return false;
        }
    }


    //nacitenie json suboru
    private String loadFile(File f) throws IOException {
        FileInputStream is = new FileInputStream(f);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String mResponse = new String(buffer);
        return mResponse;
    }


    //ulozenie json suboru
    private void saveFile(JSONObject jsonObject) throws IOException {
        FileWriter  file = new FileWriter (this.getFilesDir().getPath()+ "/"+filename+".json");
        file.write(String.valueOf(jsonObject));
        file.flush();
        file.close();
    }

}
