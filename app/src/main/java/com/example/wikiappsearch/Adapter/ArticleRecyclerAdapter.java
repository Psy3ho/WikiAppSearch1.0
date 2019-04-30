package com.example.wikiappsearch.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.wikiappsearch.Cards.Article;
import com.example.wikiappsearch.DetailOfArticle;
import com.example.wikiappsearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ArticleRecyclerAdapter extends RecyclerView.Adapter<ArticleRecyclerAdapter.ViewHolder> {
    private List<Article> article_list;
    private Context context;
    private static final String filename = "articleJS";



    public ArticleRecyclerAdapter(List<Article> article_list){

        this.article_list = article_list;

    }

    @NonNull
    @Override
    public ArticleRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ArticleRecyclerAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final int idArticle = article_list.get(position).getId();
        String title = article_list.get(position).getTitle();
        String content = article_list.get(position).getContent();
        final String language = article_list.get(position).getLanguage();

        holder.setDataArticle(idArticle, title, content,language);

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return article_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView title;
        private TextView snippet;
        private TextView information;
        private Button delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        //nastavenie informacie o clanku ci je alebo nieje ulozeny podla togo tiez zobrazuje tlacidlo amzanie alebo povolime blizšie zobrazenie clanku
        public void setDataArticle(final int paIdArticle, String paTitle, String paSnippet , final String paLanguage) {

            title = mView.findViewById(R.id.title);
            snippet = mView.findViewById(R.id.snippet);
            information = mView.findViewById(R.id.information);
            delete  = mView.findViewById(R.id.removeB);

            if(setInformation(paIdArticle)){
                information.setText(R.string.saved_article);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteArticle(paIdArticle);
                    }
                });
            } else {
                information.setText(R.string.unsaved_article);
                setDeleteButton();
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(context,DetailOfArticle.class);
                        intent.putExtra("IdArticle",paIdArticle);
                        intent.putExtra("language",paLanguage);
                        context.startActivity(intent);
                    }
                });

            }

            title.setText(paTitle);
            snippet.setText(Html.fromHtml(paSnippet));



        }

        //zobrazenie tlacidla pre mazanie v recycleview
        public void setDeleteButton(){

            delete.setVisibility(View.GONE);

        }

        //overenie o clanku ci je ulozeny alebo nie
        public boolean setInformation(int articleid) {
            boolean saved = false;
            try {
                JSONObject jsonParent;
                JSONArray jsonArr;
                JSONObject jsonArticle;
                File f = new File(context.getFilesDir().getPath()+ "/"+filename+".json");
                if(f.exists()) {
                    jsonParent = new JSONObject(loadFile(f));
                    jsonArr = jsonParent.getJSONArray("ListArticles");

                    for (int i = 0; i < jsonArr.length(); i++) {
                        jsonArticle = jsonArr.getJSONObject(i);
                        if(jsonArticle.getString("id").equals(String.valueOf(articleid))) {
                            saved =true;
                        }

                    }
                }

            }
            catch(JSONException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return saved;

        }

    }

    //vymazanie jedneho clanku -- vzdy sa spet nahra subor json uz z ulozenym listom clankou
    public void deleteArticle(int id){
        List<String> listArticle =new ArrayList<>();

        String article;
        try {
            JSONObject jsonParent = new JSONObject();
            JSONArray jsonArr;
            JSONArray jsonArrCleared =new JSONArray();
            JSONObject jsonArticle;
            File f = new File(context.getFilesDir().getPath()+ "/"+filename+".json");
            if(f.exists()) {
                jsonParent = new JSONObject(loadFile(f));
                jsonArr = jsonParent.getJSONArray("ListArticles");

                for (int i = 0; i < jsonArr.length(); i++) {
                    jsonArticle = jsonArr.getJSONObject(i);
                    if(!jsonArticle.getString("id").equals(String.valueOf(id))) {
                        jsonArrCleared.put(jsonArticle);
                    }
                }
            }
            jsonParent.put("ListArticles", jsonArrCleared);
            saveFile(jsonParent);



        }
        catch(JSONException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();


    }
    //nacitanie suboru
    private String loadFile(File f) throws IOException {
        FileInputStream is = new FileInputStream(f);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String mResponse = new String(buffer);
        return mResponse;
    }

    //ulozenie suboru
    private void saveFile(JSONObject jsonObject) throws IOException {
        FileWriter file = new FileWriter (context.getFilesDir().getPath()+ "/"+filename+".json");
        file.write(String.valueOf(jsonObject));
        file.flush();
        file.close();
    }



}
