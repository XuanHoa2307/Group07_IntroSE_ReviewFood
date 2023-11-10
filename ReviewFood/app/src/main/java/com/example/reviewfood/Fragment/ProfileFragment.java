package com.example.reviewfood.Fragment;

import static android.content.ContentValues.TAG;
import static com.example.reviewfood.MainActivity.MY_REQUEST_CODE;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.reviewfood.MainActivity;
import com.example.reviewfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileFragment extends Fragment {

    private View mview;
    private ImageView imgAvatar;
    private EditText edTxt_Fullname, edTxt_Email, edTxt_Birth;
    private Button btnSelect, btnUpdate;
    private Spinner genderSpinner;

    private Uri mUri;

    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mview = inflater.inflate(R.layout.fragment_profile, container, false);

        imgAvatar = mview.findViewById(R.id.imgAvatar_Profile);
        edTxt_Fullname = mview.findViewById(R.id.edTxt_Fullname);
        edTxt_Email = mview.findViewById(R.id.edTxt_Email);
        edTxt_Birth = mview.findViewById(R.id.edTxt_Birth);

        btnSelect = mview.findViewById(R.id.btnSelect);
        genderSpinner = mview.findViewById(R.id.genderSpinner);

        btnUpdate = mview.findViewById(R.id.btnUpdate);

        progressDialog = new ProgressDialog(getActivity());


        // show thong tin hien tai
        showUserInformationPresent();

        // chon anh Avatar tu thu vien
        setAvatar();

        // set ngay sinh
        setBirthday();

        // set gioi tinh
        setGender();

        // update Information
        setUpdateInformation();

        return mview;

    }

    private void showUserInformationPresent(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.avatar_default).into(imgAvatar);
        edTxt_Fullname.setText(user.getDisplayName());
        edTxt_Email.setText(user.getEmail());

    }

    private void setAvatar(){
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
            }
        });
    }

    private void onClickRequestPermission(){

        MainActivity mainActivity = (MainActivity) getActivity();

        if(mainActivity == null){
            return;
        }

        // neu user da cho phep su dung cai nay
        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            mainActivity.openGallery();
        }
        else{ // chua cho phep thi phai ay
            String [] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permission, MY_REQUEST_CODE);
            mainActivity.openGallery();
        }
    }

    public void setBitmapImageView(Bitmap bitmapImageView){
        imgAvatar.setImageBitmap(bitmapImageView);
    }

    public static void setUri(Uri mUri) {
        mUri = mUri;
    }

    private void setBirthday(){
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        // Cập nhật ngày tháng năm vào EditText "Birthdate" theo định dạng dd/mm/yyyy
                        String date = String.format("%02d/%02d/%04d", dayOfMonth, (month + 1), year);
                        edTxt_Birth.setText(date);
                    }
                }, 2000, 0, 1); // Mặc định ngày 1 tháng 1 năm 2000
                datePickerDialog.show();
            }
        });
    }

    private void setGender(){

        // Khai báo danh sách giới tính
        String[] genders = {"Nam", "Nữ", "Bí mật"};

        // Tạo một ArrayAdapter để hiển thị danh sách giới tính
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, genders);

        // Đặt kiểu hiển thị cho Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Đặt ArrayAdapter cho Spinner
        genderSpinner.setAdapter(adapter);
    }

    private void setUpdateInformation(){

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickToUpdateProfile();
            }
        });
    }

    private void onClickToUpdateProfile(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            return;
        }
        String Fullname = edTxt_Fullname.getText().toString().trim();

        progressDialog.show();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(Fullname)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Update profile success!", Toast.LENGTH_SHORT).show();

                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.showUserInformation();
                        }
                    }
                });
    }
}
