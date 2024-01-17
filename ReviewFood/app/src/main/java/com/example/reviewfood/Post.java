package com.example.reviewfood;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class Post extends PostId {
    private String author;
    private String status;
    private String imagePost;
    private Timestamp postTime;
    private int likeNumber;
    private int dislikeNumber;
    private List<String> likeIDList;
    private List<String> dislikeIDList;
    public boolean isLiked;
    public boolean isDisLiked;

    public Post() {
        this.likeNumber = 0;
        this.dislikeNumber = 0;
        this.likeIDList = new ArrayList<>();
        this.dislikeIDList = new ArrayList<>();
        isLiked = false;
        isDisLiked = false;
    }

    public Post(String author, String status, String imagePost, Timestamp postTime) {
        this.author = author;
        this.status = status;
        this.imagePost = imagePost;
        this.postTime = postTime;
        this.likeNumber = 0;
        this.dislikeNumber = 0;
        this.likeIDList = new ArrayList<>();
        this.dislikeIDList = new ArrayList<>();
        isLiked = false;
        isDisLiked = false;
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

    public int getLikeNumber() {
        return likeNumber;
    }

    public int getDislikeNumber() {
        return dislikeNumber;
    }

    public List<String> getLikeIDList() {
        return likeIDList;
    }

    public List<String> getDislikeIDList() {
        return dislikeIDList;
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

    public void setLikeNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }

    public void setDislikeNumber(int dislikeNumber) {
        this.dislikeNumber = dislikeNumber;
    }

    public void setLikeIDList(List<String> likeIDList) {
        this.likeIDList = likeIDList;
    }

    public void setDislikeIDList(List<String> dislikeIDList) {
        this.dislikeIDList = dislikeIDList;
    }
}

