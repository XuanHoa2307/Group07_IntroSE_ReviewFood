package com.example.reviewfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
        // check if account have been deleted but still sign in not sign out yet
        if(user == null){
            // chu login, nhay vao trang login
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
        else{
            // da login chuyen vao man hinh chinh
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}