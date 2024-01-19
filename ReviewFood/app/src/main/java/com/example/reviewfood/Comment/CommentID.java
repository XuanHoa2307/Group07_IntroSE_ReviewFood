package com.example.reviewfood.Comment;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class CommentID {

    @Exclude
    public String commentId;

    public <T extends CommentID> T withId(@NonNull final String id) {
        this.commentId = id;
        return (T) this;
    }

}
