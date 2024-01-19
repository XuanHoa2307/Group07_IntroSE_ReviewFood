package com.example.reviewfood;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ReportID {
    @Exclude
    public String reportID;

    public <T extends ReportID> T withId(@NonNull final String id) {
        this.reportID = id;
        return (T) this;
    }
}