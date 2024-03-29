package com.example.reviewfood;

import com.google.firebase.Timestamp;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Post extends PostId implements Serializable {

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
    private List<String> tagList;
    private List<Report> reportList;

    public Post() {
        this.likeNumber = 0;
        this.dislikeNumber = 0;
        this.likeIDList = new ArrayList<>();
        this.dislikeIDList = new ArrayList<>();
        isLiked = false;
        isDisLiked = false;
        this.commentNumber = 0;
        this.commentList = new ArrayList<>();
        this.tagList = new ArrayList<>();
        this.reportList = new ArrayList<>();
    }

    public Post(String author, String userID, String status, String imagePost, Timestamp postTime) {
        this.author = author;
        this.userID = userID;
        this.status = status;
        this.imagePost = imagePost;
        this.postTime = postTime;
        this.likeNumber = 0;
        this.dislikeNumber = 0;
        this.likeIDList = new ArrayList<>();
        this.dislikeIDList = new ArrayList<>();
        isLiked = false;
        isDisLiked = false;
        this.commentNumber = 0;
        this.commentList = new ArrayList<>();
        this.tagList = new ArrayList<>();
        this.reportList = new ArrayList<>();
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

    public List<String> getTagList() {
        return tagList;
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

    public void setTagList(List<String> tagList) {
        this.tagList.clear();
        this.tagList.addAll(tagList);
    }
    public void setReportList(List<Report> reportList) {
        this.reportList.clear();
        this.reportList.addAll(reportList);
    }

    public boolean addReport(Report report){
        boolean isNew = true;
        for (int i = 0; i < reportList.size(); i++){
            if (Objects.equals(report.getReporterID(), reportList.get(i).getReporterID())){
                isNew = false;
                break;
            }
        }
        if (isNew) {
            reportList.add(report);
        } else {
            // người này đã gửi report
        }

        return isNew;
    }

}

