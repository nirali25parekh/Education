package com.example.mind_mover;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BaseActivity extends AppCompatActivity {
    TextView textViewTitle;
    public static Toolbar customToolbar;


    //    public static String ACTIVITY_TITLE = "DJCSI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setupToolbar(){
        customToolbar=(Toolbar)findViewById(R.id.home_toolbar);
        textViewTitle = (TextView) customToolbar.findViewById(R.id.toolbar_title);
        customToolbar.setTitle("");
        setSupportActionBar(customToolbar);
    }

    @Override
    public void finish () {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkAuthorisation();
    }

    protected void checkAuthorisation () {
        FirebaseAuthorisation.check(this);

    }


    public void setTitle(String title) {
        textViewTitle.setText(title);
    }

}