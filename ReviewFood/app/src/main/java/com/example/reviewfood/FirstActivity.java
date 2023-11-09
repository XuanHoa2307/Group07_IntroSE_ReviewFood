package com.example.reviewfood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 2000); // do tre 2 giay de cho first xml
    }

    private void nextActivity(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            // chu login, nhay vao trang login
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
        else{
            // da login chuyen vao man hinh chinh
            /*Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);*/

            // vì đang test nên để, nếu làm xong chức năng đăng xuất rồi thì lấy 2 dòng trên, bỏ 2 dòng dưới này
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }
}