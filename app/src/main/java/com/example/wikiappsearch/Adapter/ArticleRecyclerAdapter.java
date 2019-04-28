package com.example.wikiappsearch.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wikiappsearch.Cards.Article;
import com.example.wikiappsearch.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArticleRecyclerAdapter extends RecyclerView.Adapter<ArticleRecyclerAdapter.ViewHolder>{
    public List<Article> article_list;
    public Context context;


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

        String title = article_list.get(position).getTitle();
        String content = article_list.get(position).getContent();
        String information = article_list.get(position).getInformation();

        //setter na nastavenie textu prispevku zo zoznamu
        holder.setDataArticle(title, content, information);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDataArticle(String paTitle, String paSnippet, String paInformation) {

            title = mView.findViewById(R.id.title);
            snippet = mView.findViewById(R.id.snippet);
            information = mView.findViewById(R.id.information);

        }
    }
}
