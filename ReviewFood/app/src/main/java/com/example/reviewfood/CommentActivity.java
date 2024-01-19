package com.example.reviewfood;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// Khai báo Executor ở mức class

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
    private int commentNumber = 0;
    private List<String> commentIDList = new ArrayList<>();
    private OnCommentCreatedListener onCommentCreatedListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_post);
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        if (intent != null){
            postId = intent.getStringExtra("postId");
            commentIDList = intent.getStringArrayListExtra("commentIDList");
        }




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


        commentNumber = commentIDList.size();



        readInformation();
        listenDataChange();
        get_fullNameUser();

        handleButtonClick();
    }


    private int currentLongPressedCommentPosition = -1;

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (currentLongPressedCommentPosition != -1) {
            int id = item.getItemId();
            if (id == R.id.menu_delete_comment){
                    handleDeleteComment(currentLongPressedCommentPosition);
                    return true;
            }
        }

        return super.onContextItemSelected(item);
    }

    public void setCurrentLongPressedCommentPosition(int position) {
        currentLongPressedCommentPosition = position;
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
                setOnCommentCreatedListener(new OnCommentCreatedListener() {
                    @Override
                    public void onCommentCreated() {
                        content.setText("");
                        commentNumber++;

                        updateCommentToCloud(postId);
                    }
                });
                createComment(newCommnet);

            }
        });

    }

    private void updateCommentToCloud(String postId){
        // Tạo một HashMap để chứa dữ liệu cần cập nhật
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("commentNumber", commentNumber); //
        updateData.put("commentList", commentIDList);


// Đường dẫn của bài viết cần cập nhật
        String postDocumentPath = "Post/" + postId; // postId là id của bài viết cần cập nhật

// Thực hiện cập nhật dữ liệu lên Firestore chỉ cho các trường cần cập nhật
        fireStore.document(postDocumentPath)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    // Xử lý khi cập nhật thành công
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi cập nhật thất bại
                });
    }

    private void createComment(Comment comment) {
        fireStore.collection("Comment").add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                generatedIDComment = documentReference.getId();
                commentIDList.add(generatedIDComment);
                listenDataChange();
                if (onCommentCreatedListener != null){
                    onCommentCreatedListener.onCommentCreated();
                }
            }
        });

    }

    public interface OnCommentCreatedListener {
        void onCommentCreated();
    }
    public void setOnCommentCreatedListener(OnCommentCreatedListener listener) {
        this.onCommentCreatedListener = listener;
    }


    private void handleDeleteComment(int position) {
        // Kiểm tra xem vị trí có hợp lệ không
        if (position < 0 || position >= commentList.size()) {
            return;
        }

        // Lấy thông tin của bình luận từ danh sách
        Comment deletedComment = commentList.get(position);

        // Lấy DocumentReference của bình luận cần xóa
        DocumentReference commentRef = fireStore.collection("Comment").document(deletedComment.commentId);

        // Xóa bình luận từ Firestore
        commentRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Nếu xóa thành công, cập nhật RecyclerView bằng cách xóa khỏi danh sách và thông báo Adapter
                        commentList.remove(deletedComment);
                        commentIDList.remove(deletedComment.commentId);
                        commentNumber = commentIDList.size();
                        commentAdapter.notifyItemRemoved(position);
                        Toast.makeText(CommentActivity.this, "Bình luận đã được xóa", Toast.LENGTH_SHORT).show();
                        updateCommentToCloud(postId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi xóa thất bại
                        Toast.makeText(CommentActivity.this, "Xóa bình luận thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }


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


    Executor executor = Executors.newSingleThreadExecutor();
}
