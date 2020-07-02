package com.example.codinggameapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.codinggameapp.Utils.DataManager;

import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

public class UserInfoActivity extends AppCompatActivity {
    private TextView user_name, user_gradeComment, user_year, user_sex, user_email, user_phone, user_grade;
    private AppCompatImageView btn_home, img_grade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        user_name = (TextView)findViewById(R.id.txt_user_name);
        user_gradeComment = (TextView)findViewById(R.id.txt_user_gradeComment);
        user_grade = (TextView)findViewById(R.id.txt_user_grade);
        user_year = (TextView)findViewById(R.id.txt_user_year);
        user_sex = (TextView)findViewById(R.id.txt_user_sex);
        user_email = (TextView)findViewById(R.id.txt_user_email);
        user_phone = (TextView)findViewById(R.id.txt_user_phone);
        btn_home = (AppCompatImageView)findViewById(R.id.btn_home);
        img_grade = (AppCompatImageView)findViewById(R.id.img_user_grade);


        try{
            String userInfo = DataManager.getSharedPreferences_UserInfo(UserInfoActivity.this);
            Log.i("seo","userinfo : " + userInfo);
            JSONObject json = new JSONObject(userInfo);
            int imageID = getResources().getIdentifier("userinfo_level_"+ json.getString("grade"),"drawable",getPackageName());
            user_name.setText(json.getString("user_name"));
            user_gradeComment.setText("현재 회원님의 등급은 " + ConvertToGrade(json.getString("grade")) +  " 입니다.");
            user_grade.setText(ConvertToGrade(json.getString("grade")));
            img_grade.setImageResource(imageID);
            user_year.setText(json.getString("user_year"));
            user_sex.setText(json.getString("user_sex"));
            user_email.setText(json.getString("user_email"));
            user_phone.setText(json.getString("user_phone"));
        }catch (Exception e){
            Log.i("seo","err : " + e);

        }

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private String ConvertToGrade(String grade){
        String convertedGrade = "브론즈";
        switch (grade){
            case "bronze" :
                convertedGrade = "브론즈";
                break;
            case "silver" :
                convertedGrade = "실버";
                break;
            case "gold" :
                convertedGrade = "골드";
                break;
            case "platinum" :
                convertedGrade = "플래티넘";
                break;
            case "diamond" :
                convertedGrade = "다이아몬드";
                break;
        }
        return convertedGrade;
    }
}
