package com.example.reviewfood;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText edTxt_Email, edTxt_Password, edTxt_Confirm_Password;
    private TextView txt_HaveAccount;
    private Button btn_SignUp;
    private ProgressDialog progressDialog;

    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    Authentication authUser;
    User userInf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edTxt_Email = findViewById(R.id.edTxt_Email_SignUp);
        edTxt_Password = findViewById(R.id.edTxt_Password_SignUp);
        edTxt_Confirm_Password = findViewById(R.id.edTxt_ConfirmPass_SignUp);
        btn_SignUp = findViewById(R.id.btn_SignUp);
        txt_HaveAccount = findViewById(R.id.txt_HaveAccount);
        progressDialog = new ProgressDialog(this);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        authUser = new Authentication();
        userInf = new User();

        SignUpAccount();

        BackSignIn_haveAccount();
    }

    private void SignUpAccount(){
        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateEmail() | !validatePassword() | !validateConfirmPassword()) {
                    return;
                }

                else {
                    String strEmail = edTxt_Email.getText().toString().trim();
                    String strPassword = edTxt_Password.getText().toString().trim();
                    authUser = new Authentication(strEmail, strPassword);
                    progressDialog.show();

                    fireAuth.createUserWithEmailAndPassword(authUser.getEmail(), authUser.getPassword())
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {

                                        authUser.setPassword(Authentication.hashPass(authUser.getPassword()));
                                        String userUid = fireAuth.getCurrentUser().getUid();
                                        fireStore.collection("Authentication")
                                                 .document(userUid)
                                                 .set(authUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(SignUpActivity.this, "Add data successfully.", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                        userInf.setEmail(authUser.getEmail());
                                        userInf.setFullname("");
                                        userInf.setBirthday("");
                                        userInf.setGender("");
                                        fireStore.collection("User")
                                                .document(userUid)
                                                .set(userInf).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(SignUpActivity.this, "Add information successfully.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);

                                        finishAffinity();
                                    }

                                    else {
                                        // If sign in fails, display a message to the user
                                        Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean validateEmail() {
        String input = edTxt_Email.getText().toString().trim();

        String regex = "^[A-Za-z0-9.]+@gmail.com$";
        if (input.isEmpty()) {
            edTxt_Email.setError("This field must not be empty");
            return false;
        }
        else if (Character.isDigit(input.charAt(0))) {
            edTxt_Email.setError("Email must not begin with a number");
            return false;
        }
        else if (input.length() < 14) {
            edTxt_Email.setError("Email must contain at least 14 characters");
            return false;
        }
        else if (!input.endsWith("@gmail.com")) {
            edTxt_Email.setError("Email must be a valid Gmail address (@gmail.com)");
            return false;
        }
        else if (input.contains(" ")) {
            edTxt_Email.setError("Email must not contain spaces");
            return false;
        }
        else if (!input.matches(regex)) {
            edTxt_Email.setError("Email must not contain special characters before @gmail.com");
            return false;
        }
        else {
            edTxt_Email.setError(null);
            return true;
        }

    }


    private boolean validatePassword(){
        String input = edTxt_Password.getText().toString().trim();

        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern digit = Pattern.compile("[0-9]");
        Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

        Matcher hasUppercase = uppercase.matcher(input);
        Matcher hasDigit = digit.matcher(input);
        Matcher hasSpecial = special.matcher(input);

        if (input.isEmpty()) {
            edTxt_Password.setError("This field must not be empty");
            return false;
        }
        else if (input.length() < 8) {
            edTxt_Password.setError("Password must contain at least 8 characters");
            return false;
        }
        else if (input.contains(" ")) {
            edTxt_Password.setError("Password must not contain whitespace");
            return false;
        }
        else if (!hasUppercase.find()) {
            edTxt_Password.setError("Password must contain at least 1 uppercase");
            return false;
        }
        else if (!hasDigit.find()) {
            edTxt_Password.setError("Password must contain at least 1 digit");
            return false;
        }
        else if (!hasSpecial.find()) {
            edTxt_Password.setError("Password must contain at least 1 special character");
            return false;
        }
        else {
            edTxt_Password.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String input = edTxt_Confirm_Password.getText().toString().trim();

        if (input.isEmpty()) {
            edTxt_Confirm_Password.setError("This field must not be empty");
            return false;
        }
        else if (!input.equals(edTxt_Confirm_Password.getText().toString().trim())) {
            edTxt_Confirm_Password.setError("Confirm password does not match");
            return false;
        }
        else {
            edTxt_Confirm_Password.setError(null);
            return true;
        }
    }


    private void BackSignIn_haveAccount(){
        txt_HaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}