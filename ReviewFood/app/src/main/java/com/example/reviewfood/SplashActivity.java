package com.example.reviewfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private List<String> mailAdmin = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getMailAdminList();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 2000);
    }

    private void nextActivity(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // còn check account đã xóa không tồn tại nữa nhưng vẫn đang sign-in chưa sign out

        if(user == null){
            // chu login, nhay vao trang login
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
        else{
            // da login chuyen vao man hinh chinh
            if (!mailAdmin.contains(user.getEmail())) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, MainActivityAdmin.class);
                startActivity(intent);
            }
        }
        finish();
    }


    private void getMailAdminList(){
        firestore = FirebaseFirestore.getInstance();
        firestore.collection("AdminAuthentication").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> emailList = new ArrayList<>();

                    // Lặp qua tất cả các tài liệu trong collection "admin"
                    for (DocumentSnapshot document : task.getResult()) {
                        // Kiểm tra xem tài liệu có chứa field "email" hay không
                        if (document.contains("email")) {
                            String email = document.getString("email");
                            emailList.add(email);
                        }
                    }

                    // In ra danh sách email hoặc thực hiện các thao tác khác với danh sách email
                    for (String email : emailList) {
                        mailAdmin.add(email);
                    }
                }
            }
        });
    }
}