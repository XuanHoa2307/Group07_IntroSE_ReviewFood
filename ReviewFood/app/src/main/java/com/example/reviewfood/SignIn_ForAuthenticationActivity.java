package com.example.reviewfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reviewfood.Fragment.SettingFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignIn_ForAuthenticationActivity extends AppCompatActivity {

    private EditText edTxt_Email_Au, edTxt_Password_Au;
    private Button btn_Authentication, btn_BackToFragChangePass;
    private TextView txt_ForgotPassword;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_for_authentication);

        edTxt_Email_Au = findViewById(R.id.edTxt_Email_Authentication);
        edTxt_Password_Au = findViewById(R.id.edTxt_Password_Authentication);
        btn_Authentication = findViewById(R.id.btn_Authentication);
        txt_ForgotPassword = findViewById(R.id.forgotPassword);
        progressDialog = new ProgressDialog(this);
        btn_BackToFragChangePass = findViewById(R.id.btn_Back);

        ClickToAuthentication();

        ClickToBackFragChangePass();

    }

    private void ClickToAuthentication(){
        btn_Authentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edTxt_Email_Au.getText().toString().trim();
                String password = edTxt_Password_Au.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    // Kiểm tra xem bất kỳ trường nào chưa được nhập
                    Toast.makeText(SignIn_ForAuthenticationActivity.this, "Vui lòng nhập đủ email, password", Toast.LENGTH_SHORT).show();
                }
                else{

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    progressDialog.show();
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignIn_ForAuthenticationActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {

                                        // Sign in success, update UI with the signed-in user's information
                                        Intent intent = new Intent(SignIn_ForAuthenticationActivity.this, ChangePasswordActivity.class);
                                        startActivity(intent);
                                        finishAffinity();
                                    }

                                    else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignIn_ForAuthenticationActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void ClickToBackFragChangePass(){
        btn_BackToFragChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn_ForAuthenticationActivity.this, SettingFragment.class);
                startActivity(intent);
                finish();
            }
        });
    }
}