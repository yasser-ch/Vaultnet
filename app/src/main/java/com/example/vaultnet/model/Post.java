package com.example.vaultnet.model;

public class Post {
    private int    id;
    private int    userId;
    private String title;
    private String body;

    public int    getId()     { return id; }
    public int    getUserId() { return userId; }
    public String getTitle()  { return title; }
    public String getBody()   { return body; }

    @Override
    public String toString() {
        return "Post{id=" + id + ", title='" + title + "'}";
    }
}