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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText edTxt_Email, edTxt_Password, edTxt_Confirm_Password;
    private TextView txt_HaveAccount;
    private Button btn_SignUp;

    private ProgressDialog progressDialog;
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

        // Su kien click Sign up account!
        SignUpAccount_Click();

        // Su kien click da co account, back ve Sign In!
        BackSignIn_haveAccount();
    }

    private void SignUpAccount_Click(){
        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check chua nhap du thong tin thong bao nguoi dung
                // Lấy giá trị từ các trường nhập liệu
                String email = edTxt_Email.getText().toString();
                String password = edTxt_Password.getText().toString();
                String confirmPassword = edTxt_Confirm_Password.getText().toString();

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    // Kiểm tra xem bất kỳ trường nào chưa được nhập
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập đủ email, password và confirm password", Toast.LENGTH_SHORT).show();
                }

                else {
                    // Tiến hành xử lý đăng ký khi đã nhập đủ thông tin
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String strEmail = edTxt_Email.getText().toString().trim();
                    String strPassword = edTxt_Password.getText().toString().trim();

                    progressDialog.show();

                    // tao account xu li tren firebase
                    auth.createUserWithEmailAndPassword(strEmail, strPassword)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);

                                        // dong tat ca cac activity trc khi dki account va vao giao dien thanh cong
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