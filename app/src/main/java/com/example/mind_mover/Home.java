package com.example.mind_mover;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Home extends BaseActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupToolbar();
        setTitle("Home");
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.navigationView);
        final ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.abc:
                        item.setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout,new ProfileFragment()).commit();
                        displayMessage("ABC selected");
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.onetwothree:
                        item.setChecked(true);
                        displayMessage("123 selected");
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.articles:
                        item.setChecked(true);
                        displayMessage("Articles");
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.quiz:
                        item.setChecked(true);
                        displayMessage("Quiz");
                        drawerLayout.closeDrawers();
                        return true;
                }
                return false;
            }
        });
    }

    private void displayMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
