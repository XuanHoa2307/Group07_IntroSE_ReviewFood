package com.example.reviewfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reviewfood.Fragment.SettingFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edTxt_NewPassword, edTxt_ConfirmPassword;
    private Button btn_ChangePass, btn_BackToFragChangePass;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        edTxt_NewPassword = findViewById(R.id.edTxt_Password_New);
        edTxt_ConfirmPassword = findViewById(R.id.edTxt_Password_Confirm);
        btn_ChangePass = findViewById(R.id.btn_ChangePass);
        btn_BackToFragChangePass = findViewById(R.id.btn_Back);
        progressDialog = new ProgressDialog(this);

        ClickToChangePassword();

        ClickBackToFragSetting();

    }

    private void ClickToChangePassword(){
        btn_ChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Password = edTxt_NewPassword.getText().toString().trim();
                String ConfirmPassword = edTxt_ConfirmPassword.getText().toString().trim();

                if (Password.isEmpty() || ConfirmPassword.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Vui lòng nhập đủ New-password và Confirm-password", Toast.LENGTH_SHORT).show();
                }

                if (!Password.equals(ConfirmPassword)) {

                    Toast.makeText(ChangePasswordActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                }

                else {
                    progressDialog.show();

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    user.updatePassword(Password)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ChangePasswordActivity.this, "Change password success!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void ClickBackToFragSetting(){
        btn_BackToFragChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}