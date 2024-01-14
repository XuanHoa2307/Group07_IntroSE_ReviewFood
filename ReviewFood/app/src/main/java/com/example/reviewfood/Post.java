package com.example.reviewfood;

import com.google.firebase.Timestamp;

public class Post extends PostId {
    private String author;
    private String status;
    private String imagePost;
    private Timestamp postTime;

    public Post() {}

    public Post(String author, String status, String imagePost, Timestamp postTime) {
        this.author = author;
        this.status = status;
        this.imagePost = imagePost;
        this.postTime = postTime;
    }

    public String getAuthor() {
        return author;
    }
    public String getStatus() {
        return status;
    }
    public String getImagePost() {
        return imagePost;
    }
    public Timestamp getPostTime() {
        return postTime;
    }


    public void setAuthor(String author) {
        this.author = author;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setImagePost(String content) {
        this.imagePost = content;
    }
    public void setPostTime(Timestamp postTime) {
        this.postTime = postTime;
    }
}

