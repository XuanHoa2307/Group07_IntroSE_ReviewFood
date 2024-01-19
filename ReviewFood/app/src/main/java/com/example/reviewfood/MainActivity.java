package com.example.reviewfood;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.reviewfood.Fragment.DraftFragment;
import com.example.reviewfood.Fragment.FavoriteFragment;
import com.example.reviewfood.Fragment.HomeFragment;
import com.example.reviewfood.Fragment.NotificationFragment;
import com.example.reviewfood.Fragment.ProfileFragment;
import com.example.reviewfood.Fragment.SettingFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawLayout;

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_FAVORITE = 1;
    private static final int FRAGMENT_DRAFT = 2;
    private static final int FRAGMENT_PROFILE = 3;
    private static final int FRAGMENT_NOTIFICATION = 4;
    private static final int FRAGMENT_SETTING = 5;
    private int mCurrentFragment = FRAGMENT_HOME;

    public static final int MY_REQUEST_CODE = 123;

    private NavigationView mNavigationView;
    private ImageView imgAvatar;
    private TextView txtName, txtMail;

    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    StorageReference storageReference;

    //--------------------------------------------------------------------
    final private ProfileFragment mProfileFragment = new ProfileFragment();

    final private SettingFragment mSettingFragment = new SettingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationView = findViewById(R.id.navigation_view);

        imgAvatar = mNavigationView.getHeaderView(0).findViewById(R.id.imgAvatar);
        txtName = mNavigationView.getHeaderView(0).findViewById(R.id.txtName);
        txtMail = mNavigationView.getHeaderView(0).findViewById(R.id.txtMail);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        // Cài thanh toolbar và kết hợp với menu trượt tạo thành giao diện đóng mở
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
        }

        mDrawLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawLayout.addDrawerListener(toggle);
        toggle.syncState();
        // ------------------

        // set thong tin len Header navigation
        showUserInformation();

        // Xử lí click tác vụ
        mNavigationView.setNavigationItemSelectedListener(this);

        // Set man hinh moi vao
        replaceFragment(new HomeFragment());
        mNavigationView.getMenu().findItem(R.id.navi_home).setChecked(true);

    }

    // Xử lí sự kiện bấm vào các tác vụ
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.navi_home){
            if(mCurrentFragment != FRAGMENT_HOME ){
                replaceFragment(new HomeFragment());
                mCurrentFragment = FRAGMENT_HOME;
            }
        }
        else if (id == R.id.navi_favorite){
            if(mCurrentFragment != FRAGMENT_FAVORITE ){
                replaceFragment(new FavoriteFragment());
                mCurrentFragment = FRAGMENT_FAVORITE;
            }
        }
        else if (id == R.id.navi_draft){
            if(mCurrentFragment != FRAGMENT_DRAFT ){
                replaceFragment(new DraftFragment());
                mCurrentFragment = FRAGMENT_DRAFT;
            }
        }
        else if (id == R.id.navi_my_profile) {
            if(mCurrentFragment != FRAGMENT_PROFILE ) {
                replaceFragment(mProfileFragment);
                mCurrentFragment = FRAGMENT_PROFILE;
            }
        }
        else if (id == R.id.navi_notifications) {
            if(mCurrentFragment != FRAGMENT_NOTIFICATION) {
                replaceFragment(new NotificationFragment());
                mCurrentFragment = FRAGMENT_NOTIFICATION;
            }
        }
        else if (id == R.id.navi_setting) {
            if(mCurrentFragment != FRAGMENT_SETTING ) {
                replaceFragment(mSettingFragment);
                mCurrentFragment = FRAGMENT_SETTING;
            }
        }

        mDrawLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Xu li nut back
    @Override
    public void onBackPressed() {
        if(mDrawLayout.isDrawerOpen(GravityCompat.START)){
            mDrawLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    public void showUserInformation(){

        readInformation();
    }

    private void readInformation(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
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
                        String imgAvt, email, fullName;

                        imgAvt = document.getString("imageUri");
                        email = document.getString("email");
                        fullName = document.getString("fullname");

                        if(fullName == null){
                            txtName.setVisibility(View.GONE);
                        }
                        else{
                            txtName.setVisibility(View.VISIBLE);
                            txtName.setText(fullName);
                        }
                        txtMail.setText(email);
                        txtName.setText(fullName);

                        if (imgAvt != null) {
                            Uri myUri = Uri.parse(imgAvt);
                            Glide.with(MainActivity.this).load(myUri.toString()).error(R.drawable.avatar_default).into(imgAvatar);
                        } else {
                            Glide.with(MainActivity.this).load(R.drawable.avatar_default).into(imgAvatar);
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


    // code ...





}