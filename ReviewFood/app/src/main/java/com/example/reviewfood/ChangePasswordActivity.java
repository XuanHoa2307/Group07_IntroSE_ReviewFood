package com.example.reviewfood;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edTxt_OldPassword, edTxt_NewPassword, edTxt_ConfirmPassword;
    private Button btn_ChangePass;
    private ImageButton btn_BackToFragChangePass;
    private ProgressDialog progressDialog;

    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    Authentication authUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edTxt_OldPassword = findViewById(R.id.edTxt_Password_Old);
        edTxt_NewPassword = findViewById(R.id.edTxt_Password_New);
        edTxt_ConfirmPassword = findViewById(R.id.edTxt_Password_Confirm);
        btn_ChangePass = findViewById(R.id.btn_ChangePass);
        btn_BackToFragChangePass = findViewById(R.id.btn_Back);
        progressDialog = new ProgressDialog(this);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        authUser = new Authentication();

        ChangePassword();

        ClickBackToFragSetting();

    }

    private void ChangePassword(){
        btn_ChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OldPassword = edTxt_OldPassword.getText().toString().trim();
                String NewPassword = edTxt_NewPassword.getText().toString().trim();
                String ConfirmPassword = edTxt_ConfirmPassword.getText().toString().trim();

                if (OldPassword.isEmpty() || NewPassword.isEmpty() || ConfirmPassword.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Vui lòng nhập đủ Old-password, New-password và Confirm-password", Toast.LENGTH_SHORT).show();
                }
                if (!NewPassword.equals(ConfirmPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                }

                else {
                    progressDialog.show();
                    checkOldPasswordAndChange(OldPassword, NewPassword);
                }
            }
        });
    }

    private void checkOldPasswordAndChange(String OldPassword, String NewPassword) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fireAuth.signInWithEmailAndPassword(user.getEmail(), OldPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(NewPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused){
                            authUser.setEmail(user.getEmail());
                            authUser.setPassword(Authentication.hashPass(NewPassword));
                            fireStore.collection("Authentication")
                                     .document(user.getUid())
                                     .update("password", authUser.getPassword()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            Toast.makeText(ChangePasswordActivity.this, "Change password success!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(ChangePasswordActivity.this, "Old password is incorrect, please check again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ClickBackToFragSetting(){
        btn_BackToFragChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}