package com.example.reviewfood.Fragment;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.reviewfood.MainActivity;
import com.example.reviewfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class ProfileFragment extends Fragment {

    private View mview;
    private ImageView imgAvatar;
    private EditText edTxt_Fullname, edTxt_Email, edTxt_Birth;
    private Button btnSelect, btnUpdate;
    private Spinner genderSpinner;
    Uri imgAvtUri;
    private ProgressDialog progressDialog;
    private MainActivity mMainActivity;
    FirebaseFirestore fireStore;
    FirebaseAuth fireAuth;
    StorageReference storageReference;

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

        mMainActivity = (MainActivity) getActivity();

        progressDialog = new ProgressDialog(getActivity());

        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();


        // chon anh Avatar tu thu vien
        setAvatar();

        // set ngay sinh
        setBirthday();

        // set gioi tinh
        setGender();

        // show thong tin hien tai
        showUserInformationProfile();

        // update Information
        setUpdateInformation();

        return mview;

    }

    private void writeFirebase(){

        String imgAvt, fullName, birthday, gender;
        String email = edTxt_Email.getText().toString().trim();
        String[] parts = email.split("@");
        String mail = parts[0]; // Phần trước dấu '@' trong email

        if(edTxt_Fullname != null && edTxt_Fullname.getText() != null){
            fullName = edTxt_Fullname.getText().toString().trim();
        } else{
            return;
        }

        if(edTxt_Birth != null && edTxt_Birth.getText() != null) {
            birthday = edTxt_Birth.getText().toString().trim();
        } else {
            return;
        }

        if(genderSpinner != null && genderSpinner.getSelectedItem() != null) {
            gender = genderSpinner.getSelectedItem().toString();
        } else {
            gender = "Secret";
        }

        //Update image of user
        if (imgAvtUri != null) {
            StorageReference imgAvtRef = storageReference.child("user_avatar").child(mail + ".jpg");
            imgAvtRef.putFile(imgAvtUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        updateImage(task,imgAvtRef);
                    } else {
                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = user.getUid();
        DocumentReference authRef = fireStore.collection("Authentication").document(userUid);
        DocumentReference userRef = fireStore.collection("User").document(userUid);

        //Update fullname
        userRef
                .update("fullname", fullName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Change fullname successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        //Update birthday
        userRef
                .update("birthday", birthday)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Change birthday successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
        //Update gender
        userRef
                .update("gender", gender)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Change gender successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        Toast.makeText(getActivity(), "Update profile success!", Toast.LENGTH_SHORT).show();

    }

    private void updateImage(Task<UploadTask.TaskSnapshot> task, StorageReference imgAvtRef) {
        imgAvtRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri uri) {

                //  docRef.update("imgAvt",uri.toString());
                String userUid = fireAuth.getCurrentUser().getUid();
                DocumentReference docRef = fireStore.collection("User").document(userUid);
                docRef
                        .update("imageUri", imgAvtUri.toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mMainActivity.showUserInformation();
                                //Toast.makeText(mMainActivity, "Upload image successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
            }
        });}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgAvtUri = result.getUri();
                imgAvatar.setImageURI(imgAvtUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getContext(), result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readFirebase(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = fireAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        // Get and show current info of user
        DocumentReference docRef = fireStore.collection("User").document(userUid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String imgAvt, email, fullName, birthday, gender;

                        imgAvt = document.getString("imageUri");
                        email = document.getString("email");
                        fullName = document.getString("fullname");
                        birthday = document.getString("birthday");
                        gender = document.getString("gender");

                        edTxt_Email.setText(email);
                        edTxt_Fullname.setText(fullName);
                        edTxt_Birth.setText(birthday);

                        if (imgAvt != null) {
                            Uri myUri = Uri.parse(imgAvt);
                            Glide.with(getActivity()).load(myUri.toString()).error(R.drawable.avatar_default).into(imgAvatar);
                        } else {
                            Glide.with(getActivity()).load(R.drawable.avatar_default).into(imgAvatar);
                        }

                        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) genderSpinner.getAdapter();
                        if (adapter != null) {
                            int position = adapter.getPosition(gender);
                            genderSpinner.setSelection(position);
                        }
                        else{
                            Toast.makeText(getActivity(), "adapter null", Toast.LENGTH_SHORT).show();
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

    private void showUserInformationProfile(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        readFirebase();
    }

    private void setAvatar(){
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
                        } else {
                            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(mMainActivity, ProfileFragment.this);
                        }
                    }
                }
            }
        });
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
                }, 2000, 1, 1); // Mặc định ngày 1 tháng 1 năm 2000
                datePickerDialog.show();
            }
        });
    }

    private void setGender(){

        // Khai báo danh sách giới tính
        String[] genders = {"Male", "Female", "Secret"};

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
        writeFirebase();
        mMainActivity.showUserInformation();
    }
}
