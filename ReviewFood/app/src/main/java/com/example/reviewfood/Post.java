package com.example.reviewfood;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class Post extends PostId {
    private String author;
    private String userID;
    private String status;
    private String imagePost;
    private Timestamp postTime;

    private int likeNumber;
    private int dislikeNumber;
    private List<String> likeIDList;
    private List<String> dislikeIDList;
    public boolean isLiked;
    public boolean isDisLiked;

    private int commentNumber;
    private List<String> commentList;

    public Post() {
        this.likeNumber = 0;
        this.dislikeNumber = 0;
        this.likeIDList = new ArrayList<>();
        this.dislikeIDList = new ArrayList<>();
        isLiked = false;
        isDisLiked = false;
        this.commentNumber = 0;
        this.commentList = new ArrayList<>();
    }

    public Post(String author, String userID, String status, String imagePost, Timestamp postTime) {
        this.author = author;
        this.userID = userID;
        this.status = status;
        this.imagePost = imagePost;
        this.postTime = postTime;
    }



    public String getAuthor() {
        return author;
    }
    public String getUserID() { return userID; }
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

    public int getCommentNumber() {
        return commentNumber;
    }

    public List<String> getCommentList() {
        return commentList;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public void setUserID(String userID) { this.userID = userID; }
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
    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    public void setCommentList(List<String> commentList) {
        this.commentList = commentList;
    }

}

