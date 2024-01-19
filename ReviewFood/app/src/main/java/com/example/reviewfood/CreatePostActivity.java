package com.example.reviewfood;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    ImageButton btnTag;
    Dialog tagDialog;
    RadioButton radio_Monman, radio_Monchay, radio_Chayman, radio_Monbanh, radio_Anvat, radio_Monkho, radio_Monnuoc, radio_Haisan, radio_Donuong;
    ImageButton btnCloseFilter;
    AppCompatButton btnOK;
    List<String> tagList;

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

        tagList = new ArrayList<>();

        showUserInformationCreatePost();
        setImagePost();

        get_fullNameAuthor();
        get_userIdAuthor();

        btnTag = findViewById(R.id.imgB_tag);
        tagDialog = new Dialog(CreatePostActivity.this);
        tagDialog.setContentView(R.layout.layout_tag_filter);
        tagDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tagDialog.setCancelable(false);
        btnCloseFilter = tagDialog.findViewById(R.id.btn_closeFilter);
        btnOK = tagDialog.findViewById(R.id.btn_Search);
        btnOK.setText("OK");
        radio_Anvat = tagDialog.findViewById(R.id.radio_Anvat);
        radio_Chayman = tagDialog.findViewById(R.id.radio_Chayman);
        radio_Haisan = tagDialog.findViewById(R.id.radio_Haisan);
        radio_Donuong = tagDialog.findViewById(R.id.radio_Donuong);
        radio_Monchay = tagDialog.findViewById(R.id.radio_Monchay);
        radio_Monnuoc = tagDialog.findViewById(R.id.radio_Monnuoc);
        radio_Monkho = tagDialog.findViewById(R.id.radio_Monkho);
        radio_Monbanh = tagDialog.findViewById(R.id.radio_Monbanh);
        radio_Monman = tagDialog.findViewById(R.id.radio_Monman);

        initRadioButton();
        clickToTag();
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
                finishAffinity();
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
                                finishAffinity();
                            }
                        }, 2000);
                    } else {
                        Toast.makeText(CreatePostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            progressDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }, 2000);
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

    private void initRadioButton(){
        radio_Monman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Monman.isSelected()) {
                    radio_Monman.setChecked(true);
                    radio_Monman.setSelected(true);
                } else {
                    radio_Monman.setChecked(false);
                    radio_Monman.setSelected(false);
                }
            }
        });

        radio_Monchay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Monchay.isSelected()) {
                    radio_Monchay.setChecked(true);
                    radio_Monchay.setSelected(true);
                } else {
                    radio_Monchay.setChecked(false);
                    radio_Monchay.setSelected(false);
                }
            }
        });

        radio_Chayman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Chayman.isSelected()) {
                    radio_Chayman.setChecked(true);
                    radio_Chayman.setSelected(true);
                } else {
                    radio_Chayman.setChecked(false);
                    radio_Chayman.setSelected(false);
                }
            }
        });

        radio_Monkho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Monkho.isSelected()) {
                    radio_Monkho.setChecked(true);
                    radio_Monkho.setSelected(true);
                } else {
                    radio_Monkho.setChecked(false);
                    radio_Monkho.setSelected(false);
                }
            }
        });

        radio_Monnuoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Monnuoc.isSelected()) {
                    radio_Monnuoc.setChecked(true);
                    radio_Monnuoc.setSelected(true);
                } else {
                    radio_Monnuoc.setChecked(false);
                    radio_Monnuoc.setSelected(false);
                }
            }
        });

        radio_Monbanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Monbanh.isSelected()) {
                    radio_Monbanh.setChecked(true);
                    radio_Monbanh.setSelected(true);
                } else {
                    radio_Monbanh.setChecked(false);
                    radio_Monbanh.setSelected(false);
                }
            }
        });

        radio_Haisan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Haisan.isSelected()) {
                    radio_Haisan.setChecked(true);
                    radio_Haisan.setSelected(true);
                } else {
                    radio_Haisan.setChecked(false);
                    radio_Haisan.setSelected(false);
                }
            }
        });

        radio_Donuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Donuong.isSelected()) {
                    radio_Donuong.setChecked(true);
                    radio_Donuong.setSelected(true);
                } else {
                    radio_Donuong.setChecked(false);
                    radio_Donuong.setSelected(false);
                }
            }
        });

        radio_Anvat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!radio_Anvat.isSelected()) {
                    radio_Anvat.setChecked(true);
                    radio_Anvat.setSelected(true);
                } else {
                    radio_Anvat.setChecked(false);
                    radio_Anvat.setSelected(false);
                }
            }
        });
    }
    private void clickToTag(){
        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagDialog.show();
            }
        });

        btnCloseFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagDialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagList.clear();

                String check = "";
                if (radio_Monman.isChecked()) {
                    check = check + radio_Monman.getText().toString();
                    tagList.add(radio_Monman.getText().toString());
                }
                if (radio_Monchay.isChecked()) {
                    check = check + radio_Monchay.getText().toString();
                    tagList.add(radio_Monchay.getText().toString());
                }
                if (radio_Chayman.isChecked()) {
                    check = check + radio_Chayman.getText().toString();
                    tagList.add(radio_Chayman.getText().toString());
                }
                if (radio_Monkho.isChecked()) {
                    check = check + radio_Monkho.getText().toString();
                    tagList.add(radio_Monkho.getText().toString());
                }
                if (radio_Monnuoc.isChecked()) {
                    check = check + radio_Monnuoc.getText().toString();
                    tagList.add(radio_Monnuoc.getText().toString());
                }
                if (radio_Monbanh.isChecked()) {
                    check = check + radio_Monbanh.getText().toString();
                    tagList.add(radio_Monbanh.getText().toString());
                }
                if (radio_Haisan.isChecked()) {
                    check = check + radio_Haisan.getText().toString();
                    tagList.add(radio_Haisan.getText().toString());
                }
                if (radio_Donuong.isChecked()) {
                    check = check + radio_Donuong.getText().toString();
                    tagList.add(radio_Donuong.getText().toString());
                }
                if (radio_Anvat.isChecked()) {
                    check = check + radio_Anvat.getText().toString();
                    tagList.add(radio_Anvat.getText().toString());
                }

                tagDialog.dismiss();
                Log.d("CheckedFilter", check);
            }
        });
    }
}