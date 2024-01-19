package com.example.reviewfood.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.reviewfood.ChangePasswordActivity;
import com.example.reviewfood.PostFragment;
import com.example.reviewfood.R;
import com.example.reviewfood.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingFragment extends Fragment {

    private View mView;
    private TextView MyListPost, changePassword, FAQ, btnLogOut;

    private ImageView imgAvatar;
    private TextView txtName;

    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    StorageReference storageReference;
    String UserID;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setting, container, false);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View mView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(mView, savedInstanceState);

        MyListPost = mView.findViewById(R.id.My_List_PostCreate);
        changePassword = mView.findViewById(R.id.ChangePassword);
        FAQ = mView.findViewById(R.id.FAQ);
        btnLogOut = mView.findViewById(R.id.btn_LogOut);

        imgAvatar = mView.findViewById(R.id.imgAvatarSettings);
        txtName = mView.findViewById(R.id.txtNameSettings);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        UserID = fireAuth.getCurrentUser().getUid();

        MyListPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostFragment postFragment = new PostFragment();


                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.content_frame, postFragment);

                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit();
            }
        });

        showUserInformationSettings();

        ClickToChangePassword();

        ClickFAQ();

        ClickToLogOut();

    }


    public void showUserInformationSettings(){

        readInformation();
    }

    private void readInformation(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        String userUid = fireAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        DocumentReference docRef = fireStore.collection("User").document(userUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String imgAvt, fullName;

                        imgAvt = document.getString("imageUri");
                        fullName = document.getString("fullname");

                        if(fullName == null){
                            txtName.setVisibility(View.GONE);
                        }
                        else{
                            txtName.setVisibility(View.VISIBLE);
                            txtName.setText(fullName);
                        }
                        txtName.setText(fullName);

                        if (imgAvt != null) {
                            Uri myUri = Uri.parse(imgAvt);
                            Glide.with(SettingFragment.this).load(myUri.toString()).error(R.drawable.avatar_default).into(imgAvatar);
                        } else {
                            Glide.with(SettingFragment.this).load(R.drawable.avatar_default).into(imgAvatar);
                        }
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void ClickToChangePassword(){
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void ClickFAQ(){
        FAQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Nothing, waiting to do", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ClickToLogOut(){
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

}