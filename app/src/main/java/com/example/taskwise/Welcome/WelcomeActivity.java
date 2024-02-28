package com.example.taskwise.Welcome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.taskmanager.R;
import com.example.taskwise.ContextWrapper;
import com.example.taskwise.SharedPreferences.AppSettingContainer;

public class WelcomeActivity extends AppCompatActivity {

    AppCompatButton getStartedBtn;

    public void cast(){
        getStartedBtn = findViewById(R.id.getStartedBtn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextWrapper.setTheme(this , new AppSettingContainer(this).getAppTheme());
        setContentView(R.layout.activity_welcome);
        cast();

        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this , UserInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}