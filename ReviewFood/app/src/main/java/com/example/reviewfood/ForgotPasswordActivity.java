package com.example.reviewfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edTxt_Email_ForgotPassword;
    private Button btnSentEmailForResetPassword, btnBackToSignIn;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edTxt_Email_ForgotPassword = findViewById(R.id.edTxt_Email_ForgotPassword);
        btnSentEmailForResetPassword = findViewById(R.id.btn_ForgotPassword);
        btnBackToSignIn = findViewById(R.id.btn_Back);

        progressDialog = new ProgressDialog(this);

        ClickSentToEmailForResetPassword();
        ClickBackToLogIn();

    }

    private void ClickSentToEmailForResetPassword(){
        btnSentEmailForResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = edTxt_Email_ForgotPassword.getText().toString().trim();
                progressDialog.show();

                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                progressDialog.dismiss();

                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this, "Email sent, please check.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(ForgotPasswordActivity.this, "Email does not exist, please check. ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private void ClickBackToLogIn(){
        btnBackToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}