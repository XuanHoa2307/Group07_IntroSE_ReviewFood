package com.example.reviewfood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailUserActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    User user;
    ImageButton btnMore;
    CircleImageView avatar;
    TextView fullname;
    String userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        SharedPreferences preferences = this.getSharedPreferences("AdminPreferences", Context.MODE_PRIVATE);
        boolean isAdmin = preferences.getBoolean("isAdmin", false);

        btnMore = findViewById(R.id.btnMoreDetailUser);
        avatar = findViewById(R.id.avatarDetailUser);
        fullname = findViewById(R.id.fullnameDetailUser);



        Intent intent = getIntent();
        String userID = null;
        if (intent.hasExtra("authorID")) {
            userID = intent.getStringExtra("authorID");
        }
        firestore = FirebaseFirestore.getInstance();

        /*// Lấy đối tượng Firestore DocumentReference cho user cần lấy
        DocumentReference userRef = firestore.collection("User").document(userID);*/

// Thực hiện truy vấn
        /*userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Chuyển đổi dữ liệu từ DocumentSnapshot thành đối tượng User
                        User user1 = document.toObject(User.class);

                        // Bạn có thể sử dụng đối tượng User ở đây
                        user = user1;

                        // Nếu cần thực hiện một hành động cụ thể sau khi lấy được người dùng
                    } else {
                        // Người dùng không tồn tại trong Firestore
                    }
                } else {
                    // Đã xảy ra lỗi trong quá trình lấy dữ liệu
                    Log.e("Firestore", "Error getting user document", task.getException());
                }
            }
        });*/
        get_fullNameUser(userID);

        fullname.setText(userName);
        if(userID != null){
            firestore.collection("User").document(userID).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String avatarUri = documentSnapshot.getString("imageUri");
                    Glide.with(this).load(avatarUri).placeholder(R.drawable.avatar_default).into(avatar);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Glide.with(DetailUserActivity.this).load(R.drawable.avatar_default).into(avatar);
                }
            });
        }

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin){
                    showPopupMenuAdmin(v);
                }
            }
        });

    }


    private void showPopupMenuAdmin(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.block_context_menu_admin, popupMenu.getMenu());

        // Đăng ký sự kiện cho các mục menu
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_block_admin){

                    return true;
                }
                return false;
            }
        });

        // Hiển thị PopupMenu
        popupMenu.show();
    }


    private void get_fullNameUser(String userId) {
        firestore.collection("User").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                    if (task.getResult() != null) {
                        User user;
                        user = task.getResult().toObject(User.class);
                        if (user.getFullname() == null || user.getFullname().length() < 1){
                            FirebaseAuth fireAuth = FirebaseAuth.getInstance();
                            String email = fireAuth.getCurrentUser().getEmail();
                            String[] parts = email.split("@");
                            String displayName = parts[0];
                            userName = displayName;
                        }
                        else {
                            userName = user.getFullname();
                        }

                    }
            }
        });
    }

}
