package com.example.reviewfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.reviewfood.Fragment.ChangePassFragment;
import com.example.reviewfood.Fragment.DraftFragment;
import com.example.reviewfood.Fragment.FavoriteFragment;
import com.example.reviewfood.Fragment.HomeFragment;
import com.example.reviewfood.Fragment.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawLayout;


    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_FAVORITE = 1;
    private static final int FRAGMENT_DRAFT = 2;
    private static final int FRAGMENT_PROFILE = 3;
    private static final int FRAGMENT_CHANGE_PASS = 4;


    private int mCurrentFragment = FRAGMENT_HOME;

    private NavigationView mNavigationView;
    private ImageView imgAvatar;
    private TextView txtName, txtMail;


    //@SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationView = findViewById(R.id.navigation_view);

        imgAvatar = mNavigationView.getHeaderView(0).findViewById(R.id.imgAvatar);
        txtName = mNavigationView.getHeaderView(0).findViewById(R.id.txtName);
        txtMail = mNavigationView.getHeaderView(0).findViewById(R.id.txtMail);

        // Cài thanh toolbar và kết hợp với menu trượt tạo thành giao diện đóng mở
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                replaceFragment(new ProfileFragment());
                mCurrentFragment = FRAGMENT_PROFILE;
            }
        }
        else if (id == R.id.navi_change_password) {
            if(mCurrentFragment != FRAGMENT_CHANGE_PASS ) {
                replaceFragment(new ChangePassFragment());
                mCurrentFragment = FRAGMENT_CHANGE_PASS;
            }
        }
        else if (id == R.id.navi_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
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

    private void showUserInformation(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }

        // set thong tin nguoi dung vao Menu hien thi
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        // check neu chua dat Name
        if(name == null){
            txtName.setVisibility(View.GONE);
        }
        else{
            txtName.setVisibility(View.VISIBLE);
            txtName.setText(name);
        }

        txtName.setText(name);
        txtMail.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.avatar_default).into(imgAvatar);

    }

}