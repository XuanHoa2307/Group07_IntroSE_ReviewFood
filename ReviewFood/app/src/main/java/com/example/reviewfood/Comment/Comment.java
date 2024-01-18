package com.example.reviewfood.Comment;

import com.google.firebase.Timestamp;

public class Comment extends CommentID {

    private String userID;
    private String content;
    private String userName;
    private Timestamp cmtTime;
    private String postId;

    public Comment(){}
    public Comment(String userID, String content, String userName, Timestamp cmtTime, String postId){
        this.userID = userID;
        this.content = content;
        this.userName = userName;
        this.cmtTime = cmtTime;
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }
    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public Timestamp getCmtTime() {
        return cmtTime;
    }

    public String getPostId() {
        return postId;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCmtTime(Timestamp cmtTime) {
        this.cmtTime = cmtTime;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
