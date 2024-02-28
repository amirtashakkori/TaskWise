package com.example.taskwise.Welcome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.R;
import com.example.taskwise.ContextWrapper;
import com.example.taskwise.Main.MainActivity;
import com.example.taskwise.SharedPreferences.AppSettingContainer;
import com.example.taskwise.SharedPreferences.UserInfoContainer;

public class UserInfoActivity extends AppCompatActivity {

    EditText nameEt , familyEt , expertiseEt;
    AppCompatButton submitBtn;
    TextView userInfoTv;
    ImageView il1 , il2;
    RelativeLayout backBtn;

    UserInfoContainer userContainer;
    AppSettingContainer settingContainer;

    public void cast(){
        nameEt = findViewById(R.id.nameEt);
        familyEt = findViewById(R.id.familyEt);
        expertiseEt = findViewById(R.id.expertiseEt);
        submitBtn = findViewById(R.id.submitBtn);
        userInfoTv = findViewById(R.id.userInfoTv);
        il1 = findViewById(R.id.il1);
        il2 = findViewById(R.id.il2);
        backBtn = findViewById(R.id.backBtn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingContainer = new AppSettingContainer(this);
        ContextWrapper.setTheme(this , settingContainer.getAppTheme());
        setContentView(R.layout.activity_user_info);

        cast();

        userContainer = new UserInfoContainer(this);
        settingContainer = new AppSettingContainer(this);

        if (!userContainer.getName().equals("")){
            il1.setVisibility(View.GONE);
            il2.setVisibility(View.VISIBLE);
            il2.setImageResource(setIlls(settingContainer.getAppTheme()));
            nameEt.setText(userContainer.getName());
            familyEt.setText(userContainer.getFamily());
            expertiseEt.setText(userContainer.getExpertise());
            userInfoTv.setText(getString(R.string.editUserInfoText));
            submitBtn.setText(getString(R.string.saveChanges));
            backBtn.setVisibility(View.VISIBLE);
        } else {
            backBtn.setVisibility(View.GONE);
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEt.getText().toString();
                String family = familyEt.getText().toString();
                String expertise = expertiseEt.getText().toString();

                if (name.length() > 0 && family.length() > 0 && expertise.length() > 0){
                    userContainer.saveInfo(nameEt.getText().toString() , familyEt.getText().toString() , expertiseEt.getText().toString());
                    Intent intent = new Intent(UserInfoActivity.this , MainActivity.class);
                    startActivity(intent);
                    finish();

                    if (!userContainer.getName().equals(""))
                        Toast.makeText(UserInfoActivity.this, "Saving Info Completed!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(UserInfoActivity.this, "Changes Saved!", Toast.LENGTH_SHORT).show();

                } else if (name.length() == 0)
                    nameEt.setError("Please Enter your first name");

                else if(family.length() == 0)
                    familyEt.setError("Please Enter your family name");

                else if (expertise.length() == 0) {
                    expertiseEt.setError("Please Enter your Expertise");
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public int setIlls(int theme){
        if (theme == 0)
            return R.drawable.il_edit_user_info_green;

        else if (theme == 1)
            return R.drawable.il_edit_user_info_gray;

        else
            return R.drawable.il_edit_user_info_blue;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}