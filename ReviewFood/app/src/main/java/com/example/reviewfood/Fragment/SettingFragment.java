package com.example.reviewfood.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.reviewfood.ChangePasswordActivity;
import com.example.reviewfood.R;
import com.example.reviewfood.SignIn_ForAuthenticationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingFragment extends Fragment {

    private View mView;
    private Button btnChangePassword, btnDeleteAcc, btnNotification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setting, container, false);

        btnChangePassword = mView.findViewById(R.id.btn_ChangePassword);
        btnNotification = mView.findViewById(R.id.btn_Notifications);
        btnDeleteAcc = mView.findViewById(R.id.btn_delAccount);

        ClickToChangePassword();

        ClickNotification();

        ClickToDeleteAccount();

        return mView;
    }

    private void ClickToChangePassword(){
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignIn_ForAuthenticationActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Vui lòng đăng nhập để xác nhận", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ClickNotification(){
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Nothing, waiting to do", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ClickToDeleteAccount(){
        btnDeleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Nothing, waiting to do", Toast.LENGTH_SHORT).show();
            }
        });
    }

}