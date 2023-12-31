package com.example.taskwise.Welcome.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskwise.Main.MainActivity;
import com.example.taskmanager.R;
import com.example.taskwise.SharedPreferences.AppSettingContainer;
import com.example.taskwise.SharedPreferences.UserInfoContainer;

public class UserInfoFragment extends Fragment {

    View view;
    EditText nameEt , familyEt , expertiseEt;
    AppCompatButton submitBtn;
    TextView userInfoTv;
    ImageView il1 , il2;
    RelativeLayout backBtn;

    UserInfoContainer userContainer;
    AppSettingContainer settingContainer;

    public void cast(){
        nameEt = view.findViewById(R.id.nameEt);
        familyEt = view.findViewById(R.id.familyEt);
        expertiseEt = view.findViewById(R.id.expertiseEt);
        submitBtn = view.findViewById(R.id.submitBtn);
        userInfoTv = view.findViewById(R.id.userInfoTv);
        il1 = view.findViewById(R.id.il1);
        il2 = view.findViewById(R.id.il2);
        backBtn = view.findViewById(R.id.backBtn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user_info, container, false);

        cast();

        userContainer = new UserInfoContainer(getActivity());
        settingContainer = new AppSettingContainer(getActivity());

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
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEt.getText().toString();
                String family = familyEt.getText().toString();
                String expertise = expertiseEt.getText().toString();

                if (name.length() > 0 && family.length() > 0 && expertise.length() > 0){
                    userContainer.saveInfo(nameEt.getText().toString() , familyEt.getText().toString() , expertiseEt.getText().toString());
                    getActivity().finish();
                    Intent intent = new Intent(getActivity() , MainActivity.class);
                    startActivity(intent);
                    
                    if (!userContainer.getName().equals(""))
                        Toast.makeText(getActivity(), "Saving Info Completed!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "Changes Saved!", Toast.LENGTH_SHORT).show();
                    
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
                getActivity().finish();
            }
        });

        return view;
    }

    public int setIlls(int theme){
        if (theme == 0)
            return R.drawable.il_edit_user_info_green;

        else if (theme == 1)
            return R.drawable.il_edit_user_info_ivory;

        else
            return R.drawable.il_edit_user_info_blue;
    }
}