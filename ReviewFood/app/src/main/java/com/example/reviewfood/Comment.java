package com.example.reviewfood;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Comment extends CommentID {

    private String userID;
    private String content;
    private String userName;
    private Timestamp cmtTime;
    private String postId;
    private List<Report> reportList;

    public Comment(){
        this.reportList = new ArrayList<>();
    }
    public Comment(String userID, String content, String userName, Timestamp cmtTime, String postId){
        this.userID = userID;
        this.content = content;
        this.userName = userName;
        this.cmtTime = cmtTime;
        this.postId = postId;
        this.reportList = new ArrayList<>();
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
