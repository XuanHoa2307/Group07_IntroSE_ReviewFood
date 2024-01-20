package com.example.reviewfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edTxt_Email_ForgotPassword;
    private Button btnSentEmailForResetPassword;
    private ImageButton btnBackToSignIn;
    private ProgressDialog progressDialog;

    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edTxt_Email_ForgotPassword = findViewById(R.id.edTxt_Email_ForgotPassword);
        btnSentEmailForResetPassword = findViewById(R.id.btn_ForgotPassword);
        btnBackToSignIn = findViewById(R.id.btn_Back);

        progressDialog = new ProgressDialog(this);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        // Sent request to email
        ClickSentToEmailForResetPassword();
        ClickBackToLogIn();

    }

    private void ClickSentToEmailForResetPassword(){
        btnSentEmailForResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailAddress = edTxt_Email_ForgotPassword.getText().toString().trim();
                progressDialog.show();
                // Check if user existed
                checkExistedUserAndSend(emailAddress);
            }
        });

    }

    private void checkExistedUserAndSend(String Email) {

        fireAuth.signInWithEmailAndPassword(getString(R.string.gmail), Authentication.hashPass(getString(R.string.pass)))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fireStore.collection("Authentication")
                                     .whereEqualTo("email", Email)
                                     .get()
                                     .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (!task.getResult().isEmpty()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        fireAuth.signOut();
                                                        fireAuth.sendPasswordResetEmail(Email)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            progressDialog.dismiss();
                                                                            fireAuth.signOut();
                                                                            Toast.makeText(ForgotPasswordActivity.this, "Email sent, please check.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                                else{
                                                    progressDialog.dismiss();
                                                    fireAuth.signOut();
                                                    Toast.makeText(ForgotPasswordActivity.this, "Email is not existed, please check the email or sign up!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else{
                                                progressDialog.dismiss();
                                                fireAuth.signOut();
                                                Toast.makeText(ForgotPasswordActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            progressDialog.dismiss();
                            fireAuth.signOut();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
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




































//