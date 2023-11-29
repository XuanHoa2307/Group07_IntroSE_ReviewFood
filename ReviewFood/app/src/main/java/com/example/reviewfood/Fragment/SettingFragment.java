package com.example.reviewfood.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.reviewfood.ChangePasswordActivity;
import com.example.reviewfood.R;

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
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
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