package com.example.reviewfood;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;

public class CreatePostActivity extends AppCompatActivity {

    private static final String[] status = new String[] {"Completed", "On Going", "Drop"};
    ArrayAdapter<String> adapter;
    // tao mot bien static de giu gia tri back up
    static Post post;
    FirebaseFirestore fireStore;
    FirebaseAuth fireAuth;
    StorageReference storageReference;
    EditText input_status;
    private ImageView imgPost;
    Uri imgPostUri;
    private ImageView imgAvatarUser;
    private TextView fullNameUser;
    private ImageView btnCreatePost;
    private ImageButton btnBack;
    String fullNameAuthor;
    String userIdAuthor;
    String generatedIDPost;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        progressDialog = new ProgressDialog(this);

        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();

        imgAvatarUser = findViewById(R.id.avatar_User);
        fullNameUser = findViewById(R.id.fullName_User);

        input_status = findViewById(R.id.edTxt_Status);
        imgPost = findViewById(R.id.imagePost);

        btnBack = findViewById(R.id.btn_BackCreatePost);
        btnCreatePost = findViewById(R.id.btn_createPost);

        showUserInformationCreatePost();
        setImagePost();

        get_fullNameAuthor();
        get_userIdAuthor();
        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if (!validateStatus()) {
                    return;
                }
                Timestamp timestamp = new Timestamp(new Date());
                post = new Post();
                post.setAuthor(fullNameAuthor);
                post.setUserID(userIdAuthor);
                post.setStatus(input_status.getText().toString());
                post.setImagePost("");
                post.setPostTime(timestamp);
                createPost(post);
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Post Created Successfully.", Toast.LENGTH_SHORT).show();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreatePostActivity.this, MainActivity.class));
                finish();
            }
        });

    }
    private void createPost(Post post) {
        fireStore.collection("Post").add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                generatedIDPost = documentReference.getId();
                writeFirebasePost();
            }
        });

    }

    private void writeFirebasePost(){

        String email = fireAuth.getCurrentUser().getEmail();
        String[] parts = email.split("@");
        String mail = parts[0];
        //Update image of user
        if (imgPostUri != null) {
            String userFolder = mail + "_post";
            String fileName = generatedIDPost + ".jpg";
            StorageReference imgPostRef = storageReference.child("image_post").child(userFolder).child(fileName);
            imgPostRef.putFile(imgPostUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        updateImage(task,imgPostRef);
                        progressDialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 2000);
                    } else {
                        Toast.makeText(CreatePostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateImage(Task<UploadTask.TaskSnapshot> task, StorageReference imgPostRef) {
        imgPostRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri uri) {

                DocumentReference docRef = fireStore.collection("Post").document(generatedIDPost);
                docRef
                        .update("imagePost", uri.toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CreatePostActivity.this, "Upload image successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
            }
        });}


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgPostUri = result.getUri();
                imgPost.setImageURI(imgPostUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(CreatePostActivity.this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setImagePost() {
        imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (ContextCompat.checkSelfPermission(CreatePostActivity.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreatePostActivity.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
                    } else {
                        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(CreatePostActivity.this);
                    }
                }
            }
        });
    }

    public void showUserInformationCreatePost(){
        readInformation();
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

                        if(fullName == null){
                            fullNameUser.setVisibility(View.GONE);
                        }
                        else{
                            fullNameUser.setVisibility(View.VISIBLE);
                            fullNameUser.setText(fullName);
                        }
                        fullNameUser.setText(fullName);

                        if (imgAvt != null) {
                            Uri myUri = Uri.parse(imgAvt);
                            Glide.with(CreatePostActivity.this).load(myUri.toString()).error(R.drawable.avatar_default).into(imgAvatarUser);
                        } else {
                            Glide.with(CreatePostActivity.this).load(R.drawable.avatar_default).into(imgAvatarUser);
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

    private void get_fullNameAuthor() {
        String userId = fireAuth.getCurrentUser().getUid();
        fireStore.collection("User").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                    if (task.getResult() != null) {
                        User user;
                        user = task.getResult().toObject(User.class);
                        fullNameAuthor = user.getFullname();
                    }
            }
        });
    }

    private void get_userIdAuthor() {
        String userId = fireAuth.getCurrentUser().getUid();
        userIdAuthor = userId;
    }

    private boolean validateStatus() {
        String input = input_status.getText().toString().trim();
        if (input.isEmpty()) {
            input_status.setError("Status must not be empty");
            return false;
        }
        else {
            input_status.setError(null);
            return true;
        }
    }
}