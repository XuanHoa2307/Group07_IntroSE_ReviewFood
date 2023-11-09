package com.example.reviewfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.reviewfood.Fragment.DraftFragment;
import com.example.reviewfood.Fragment.FavoriteFragment;
import com.example.reviewfood.Fragment.HomeFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawLayout;


    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_FAVORITE = 1;
    private static final int FRAGMENT_DRAFT = 2;

    private int mCurrentFragment = FRAGMENT_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cài thanh toolbar và kết hợp với menu trượt tạo thành giao diện đóng mở
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawLayout.addDrawerListener(toggle);
        toggle.syncState();
        // ------------------------------------------------------------------------


        // Xử lí click tác vụ
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Set man hinh moi vao
        replaceFragment(new HomeFragment());
        navigationView.getMenu().findItem(R.id.navi_home).setChecked(true);

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

        mDrawLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Nếu xài HDH thấp có nút back home thì xài, ko thì chắc kệ đi =))
    /*@Override
    public void onBackPressed() {
        if(mDrawLayout.isDrawerOpen(GravityCompat.START)){
            mDrawLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }*/

    private void replaceFragment(Fragment fragment){

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }
}