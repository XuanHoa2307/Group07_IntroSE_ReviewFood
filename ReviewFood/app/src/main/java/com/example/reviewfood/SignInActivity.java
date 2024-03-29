package com.example.reviewfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private EditText edTxt_Email, edTxt_Password;
    private Button btnSignIn;
    private TextView layoutSignUp, forgotPassword;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private CheckBox checkRememberAcc;

    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    Authentication authUser;
    User userInf;

    private List<String> mailAdmin = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        /*mailAdmin.add("admin001@gmail.com");
        mailAdmin.add("admin002@gmail.com");
        mailAdmin.add("admin003@gmail.com");
        mailAdmin.add("admin004@gmail.com");
        mailAdmin.add("admin005@gmail.com");*/

        edTxt_Email = findViewById(R.id.edTxt_Email_SignIn);
        edTxt_Password = findViewById(R.id.edTxt_Password_SignIn);
        btnSignIn = findViewById(R.id.btn_SignIn);

        layoutSignUp = findViewById(R.id.SignUp);
        progressDialog = new ProgressDialog(this);
        forgotPassword = findViewById(R.id.forgotPassword);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        authUser = new Authentication();
        userInf = new User();

        getMailAdminList();

        initCheck();

        ClickForgotPassword();

        SignInApp();

        SignUpNowViewTxt();
    }


    private void getMailAdminList(){
        fireStore.collection("AdminAuthentication").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    private void SignUpNowViewTxt(){

        layoutSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SignInApp(){
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edTxt_Email.getText().toString().trim();
                String password = edTxt_Password.getText().toString().trim();
                // check if email or password empty?
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Please enter full account and password information!", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.show();
                    fireAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    // check if task okay
                                    if (task.isSuccessful()) {
                                        authUser.setEmail(email);
                                        authUser.setPassword(Authentication.hashPass(password));
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        String userUid = user.getUid();

                                        fireStore.collection("Authentication")
                                                 .document(userUid)
                                                 .set(authUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        // update Data
                                                    }
                                                });

                                        if (checkRememberAcc.isChecked()) {
                                            saveCredentials(email, password);
                                        }
                                        else{
                                            clearCredentials();
                                        }

                                        SharedPreferences preferences = getSharedPreferences("AdminPreferences", SignInActivity.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        if (!mailAdmin.contains(authUser.getEmail())) {
                                            editor.putBoolean("isAdmin", false);
                                            editor.apply();
                                            // Sign in success, update UI with the signed-in user's information
                                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                        else {
                                            // Dung share reference luu vai tro sau khi dang nhap

                                            editor.putBoolean("isAdmin", true);
                                            editor.apply();

                                            Intent intent = new Intent(SignInActivity.this, MainActivityAdmin.class);
                                            startActivity(intent);
                                        }

                                        
                                        // dong tat ca cac activity trc khi dki account va vao giao dien thanh cong
                                        finishAffinity();
                                    }
                                    else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignInActivity.this, "Account and password are incorrect, please check again.",
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