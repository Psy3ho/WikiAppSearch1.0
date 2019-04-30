package com.example.wikiappsearch.Cards;


public class Article {
    public int id;
    public String title;
    public String content;
    public String information;
    public String language;

    public Article(int id, String title, String content, String information, String language) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.information = information;
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
