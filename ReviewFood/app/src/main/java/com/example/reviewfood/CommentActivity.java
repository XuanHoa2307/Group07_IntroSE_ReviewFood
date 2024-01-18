package com.example.reviewfood;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reviewfood.Comment.Comment;
import com.example.reviewfood.Comment.CommentAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private ImageButton btnBack, btnSend;
    private ImageView avatarUser;
    private EditText content;
    private List<Comment> commentList = new ArrayList<>();
    CommentAdapter commentAdapter;
    private String currentUserId;
    private String currentUserName;
    private FirebaseFirestore fireStore;
    private FirebaseAuth fireAuth;
    private RecyclerView commentRecyclerView;
    StorageReference storageReference;
    private String generatedIDComment;
    private String postId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_post);


        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.back_commentPost);
        btnSend = findViewById(R.id.btnSendCmt);

        avatarUser = findViewById(R.id.imgV_userImage_Comment);
        content = findViewById(R.id.comment_editText);
        commentRecyclerView = findViewById(R.id.recyclerV_commentPost);
        registerForContextMenu(commentRecyclerView);

        currentUserId = fireAuth.getCurrentUser().getUid();

        commentAdapter = new CommentAdapter(this, commentList, currentUserId);
        commentRecyclerView.setAdapter(commentAdapter);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        Intent intent = getIntent();
        if (intent != null){
            postId = intent.getStringExtra("postId");
        }




        readInformation();
        listenDataChange();
        get_fullNameUser();

        handleButtonClick();
    }


    void handleButtonClick(){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Timestamp timestamp = new Timestamp(new Date());
                Comment newCommnet = new Comment(currentUserId, String.valueOf(content.getText()), currentUserName, timestamp, postId);
                //commentList.add(newCommnet);
                createPost(newCommnet);
                //listenDataChange();
                content.setText("");
                //commentAdapter.notifyDataSetChanged();
            }
        });

        /*btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo bình luận mới
                Timestamp timestamp = new Timestamp(new Date());
                Comment newComment = new Comment(currentUserId, String.valueOf(content.getText()), currentUserName, timestamp, postId);

                // Thêm bình luận vào danh sách tạm thời
                List<Comment> temporaryList = new ArrayList<>();
                temporaryList.add(newComment);

                // Gọi hàm createPost với danh sách bình luận tạm thời
                createPost(temporaryList);

                // Xóa nội dung trong EditText
                content.setText("");
            }
        });*/

    }

    private void createPost(Comment comment) {
        fireStore.collection("Comment").add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                generatedIDComment = documentReference.getId();
                listenDataChange();
            }
        });

    }

/*    private void createPost(List<Comment> comments) {
        WriteBatch batch = fireStore.batch();

        for (Comment comment : comments) {
            DocumentReference documentReference = fireStore.collection("Comment").document();
            batch.set(documentReference, comment);
        }

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Xử lý khi thành công
                //listenDataChange(); // Gọi sau khi createPost thành công
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý khi gặp lỗi
                Toast.makeText(CommentActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }*/


    public void listenDataChange() {

        fireStore.collection("Comment")
                .whereEqualTo("postId", postId)
                .orderBy("cmtTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (!value.isEmpty()) {
                                List<Comment> newComments = new ArrayList<>();

                                for (DocumentChange doc : value.getDocumentChanges()) {
                                    String commentId = doc.getDocument().getId();
                                    Comment cmt = doc.getDocument().toObject(Comment.class).withId(commentId);

                                    switch (doc.getType()) {
                                        case ADDED:
                                        case MODIFIED:
                                            // Kiểm tra xem comment đã có trong danh sách chưa
                                            boolean isExist = false;
                                            int index = 0;
                                            for (int i = 0; i < commentList.size(); i++) {
                                                if (commentList.get(i).commentId.equals(commentId)) {
                                                    isExist = true;
                                                    index = i;
                                                    break;
                                                }
                                            }

                                            // Nếu comment chưa có trong danh sách, thêm vào
                                            if (!isExist) {
                                                newComments.add(0, cmt);
                                            } else {
                                                // Nếu comment đã có trong danh sách, cập nhật lại tại vị trí đã biết
                                                commentList.set(index, cmt);
                                            }
                                            break;

                                        case REMOVED:
                                            // Xóa comment khỏi danh sách nếu bị xóa từ Firestore
                                            for (int i = 0; i < commentList.size(); i++) {
                                                if (commentList.get(i).commentId.equals(commentId)) {
                                                    commentList.remove(i);
                                                    break;
                                                }
                                            }
                                            break;
                                    }
                                }

                                // Thêm tất cả bình luận mới vào danh sách hiện có
                                commentList.addAll(0, newComments);

                                // Cập nhật giao diện chỉ một lần sau khi thêm tất cả bình luận mới
                                commentAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }





    /*public void listenDataChange() {
        Toast.makeText(this, "listenDataChange", Toast.LENGTH_SHORT).show();
        fireStore.collection("Comment").whereEqualTo("postId",postId).orderBy("cmtTime", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (!value.isEmpty()) {
                        for (DocumentChange doc : value.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String commentId = doc.getDocument().getId();
                                Comment cmt = doc.getDocument().toObject(Comment.class).withId(commentId);
                                commentList.add(0, cmt);
                                commentAdapter.notifyItemInserted(0);
                            }
                        }
                    }
                }
            }
        });
    }*/

    /*public void listenDataChange() {
        fireStore.collection("Comment").whereEqualTo("postId", postId).orderBy("cmtTime", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (!value.isEmpty()) {
                        List<Comment> newComments = new ArrayList<>();

                        for (DocumentChange doc : value.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String commentId = doc.getDocument().getId();
                                Comment cmt = doc.getDocument().toObject(Comment.class).withId(commentId);

                                // Kiểm tra xem bình luận đã tồn tại trong danh sách chưa
                                if (!commentList.contains(cmt)) {
                                    newComments.add(0, cmt);
                                }
                            }
                        }

                        // Thêm tất cả bình luận mới vào danh sách hiện có
                        commentList.addAll(0, newComments);

                        // Cập nhật giao diện chỉ một lần sau khi thêm tất cả bình luận mới
                        commentAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }*/



    private void readInformation(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        String userUid = fireAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        DocumentReference docRef = fireStore.collection("User").document(userUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String imgAvt, fullName;

                        imgAvt = document.getString("imageUri");
                        fullName = document.getString("fullname");

                        if (imgAvt != null) {
                            Uri myUri = Uri.parse(imgAvt);
                            Glide.with(CommentActivity.this).load(myUri.toString()).error(R.drawable.avatar_default).into(avatarUser);
                        } else {
                            Glide.with(CommentActivity.this).load(R.drawable.avatar_default).into(avatarUser);
                        }
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void get_fullNameUser() {
        String userId = fireAuth.getCurrentUser().getUid();
        fireStore.collection("User").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                    if (task.getResult() != null) {
                        User user;
                        user = task.getResult().toObject(User.class);
                        if (user.getFullname() == null || user.getFullname().length() < 1){
                            String email = fireAuth.getCurrentUser().getEmail();
                            String[] parts = email.split("@");
                            String displayName = parts[0];
                            currentUserName = displayName;
                        }
                        else {
                            currentUserName = user.getFullname();
                        }

                    }
            }
        });
    }

    /*private void get_userIdUser() {
        String userId = fireAuth.getCurrentUser().getUid();
        userIdAuthor = userId;
    }*/






}
