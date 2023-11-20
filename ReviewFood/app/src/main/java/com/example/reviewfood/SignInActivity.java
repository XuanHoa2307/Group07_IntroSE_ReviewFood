package com.example.reviewfood;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private EditText edTxt_Email, edTxt_Password;
    private Button btnSignIn;
    private TextView layoutSignUp, forgotPassword;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private CheckBox checkRememberAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edTxt_Email = findViewById(R.id.edTxt_Email_SignIn);
        edTxt_Password = findViewById(R.id.edTxt_Password_SignIn);
        btnSignIn = findViewById(R.id.btn_SignIn);

        layoutSignUp = findViewById(R.id.SignUp);
        progressDialog = new ProgressDialog(this);
        forgotPassword = findViewById(R.id.forgotPassword);

        initCheck();

        ClickForgotPassword();

        SignInClick();

        SignUpNowViewTxt();
    }

    private void SignUpNowViewTxt(){

        layoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // chuyen qua Sign up
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SignInClick(){
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edTxt_Email.getText().toString().trim();
                String password = edTxt_Password.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    // Kiểm tra xem bất kỳ trường nào chưa được nhập
                    Toast.makeText(SignInActivity.this, "Vui lòng nhập đủ email, password", Toast.LENGTH_SHORT).show();
                }
                else{

                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    progressDialog.show();
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {

                                        if (checkRememberAcc.isChecked()) {
                                            saveCredentials(email, password);
                                        }
                                        else{
                                            clearCredentials();
                                        }

                                        // Sign in success, update UI with the signed-in user's information
                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                        startActivity(intent);

                                        // dong tat ca cac activity trc khi dki account va vao giao dien thanh cong
                                        finishAffinity();
                                    }

                                    else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void initCheck() {
        checkRememberAcc = findViewById(R.id.checkBoxRemember);
        sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE);

        // Load saved credentials if they exist
        loadCredentials();
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.apply();
    }

    private void loadCredentials() {
        String savedEmail = sharedPreferences.getString("Email", "");
        String savedPassword = sharedPreferences.getString("Password", "");
        if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            edTxt_Email.setText(savedEmail);
            edTxt_Password.setText(savedPassword);
            checkRememberAcc.setChecked(true);
        }
    }

    private void clearCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("Email");
        editor.remove("Password");
        editor.apply();
    }

    private void ClickForgotPassword(){
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

}