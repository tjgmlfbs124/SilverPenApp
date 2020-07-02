package com.example.codinggameapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.codinggameapp.Dialog.RequestProgressDialog;
import com.example.codinggameapp.Http.HttpGetRequestClass;
import com.example.codinggameapp.MissionFragment.MissionFragment01;
import com.example.codinggameapp.MissionFragment.MissionFragment02;
import com.example.codinggameapp.MissionFragment.MissionFragment03;
import com.example.codinggameapp.MissionFragment.MissionFragment04;
import com.example.codinggameapp.Utils.DataManager;
import com.github.lzyzsd.circleprogress.DonutProgress;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MissionListActivity extends AppCompatActivity {
    private FrameLayout missionMenu01, missionMenu02, missionMenu03, missionMenu04;
    private TextView txt_clear_mission;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MissionFragment01 fragment01;
    private MissionFragment02 fragment02;
    private MissionFragment03 fragment03;
    private MissionFragment04 fragment04;
//    AppCompatImageView missionMenuIcon01, missionMenuIcon02, missionMenuIcon03, missionMenuIcon04;
    private DonutProgress donutProgress;
    private RequestProgressDialog reqDialog;
    private AppCompatImageView btn_home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_mission_list);

        donutProgress = (DonutProgress)findViewById(R.id.mission_list_donut_progress);
        txt_clear_mission = (TextView)findViewById(R.id.txt_clear_mission);
        reqDialog = new RequestProgressDialog(MissionListActivity.this);
        btn_home = (AppCompatImageView)findViewById(R.id.btn_home);

        missionMenu01 = (FrameLayout) findViewById(R.id.missionMenu01);
        missionMenu02 = (FrameLayout) findViewById(R.id.missionMenu02);
        missionMenu03 = (FrameLayout)findViewById(R.id.missionMenu03);
        missionMenu04 = (FrameLayout)findViewById(R.id.missionMenu04);

//        missionMenuIcon01 = (AppCompatImageView)findViewById(R.id.missionMenuIcon01);
//        missionMenuIcon02 = (AppCompatImageView)findViewById(R.id.missionMenuIcon02);
//        missionMenuIcon03 = (AppCompatImageView)findViewById(R.id.missionMenuIcon03);
//        missionMenuIcon04 = (AppCompatImageView)findViewById(R.id.missionMenuIcon04);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        missionMenu01.setOnClickListener(new MissionButtonClickListener());
        missionMenu02.setOnClickListener(new MissionButtonClickListener());
        missionMenu03.setOnClickListener(new MissionButtonClickListener());
        missionMenu04.setOnClickListener(new MissionButtonClickListener());

        fragment01 = new MissionFragment01();
        fragmentTransaction.replace(R.id.missionLayout, fragment01).commitAllowingStateLoss();

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MissionListActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        if(DataManager.LoginStatus(getApplicationContext()))
            new requestGetUserMissionInfo().execute("http://" + DataManager.connectURL + "/getUserMission");


    }

    private class MissionButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (v.getId()){
                case R.id.missionMenu01 :
                    fragment01 = new MissionFragment01();
                    missionMenu01.setBackgroundResource(R.drawable.missionlist_tab_on);
                    missionMenu02.setBackgroundResource(R.drawable.missionlist_tab_off);
                    missionMenu03.setBackgroundResource(R.drawable.missionlist_tab_off);
                    missionMenu04.setBackgroundResource(R.drawable.missionlist_tab_off);
//                    missionMenuIcon01.setImageResource(R.drawable.mission_tab01_icon_white);
//                    missionMenuIcon02.setImageResource(R.drawable.mission_tab02_icon_yellow);
//                    missionMenuIcon03.setImageResource(R.drawable.mission_tab03_icon_yellow);
//                    missionMenuIcon04.setImageResource(R.drawable.mission_tab04_icon_yellow);
                    fragmentTransaction.replace(R.id.missionLayout, fragment01).commitAllowingStateLoss();
                    break;
                case R.id.missionMenu02 :
                    fragment02 = new MissionFragment02();
                    missionMenu01.setBackgroundResource(R.drawable.missionlist_tab_off);
                    missionMenu02.setBackgroundResource(R.drawable.missionlist_tab_on);
                    missionMenu03.setBackgroundResource(R.drawable.missionlist_tab_off);
                    missionMenu04.setBackgroundResource(R.drawable.missionlist_tab_off);
//                    missionMenuIcon01.setImageResource(R.drawable.mission_tab01_icon_yellow);
//                    missionMenuIcon02.setImageResource(R.drawable.mission_tab02_icon_white);
//                    missionMenuIcon03.setImageResource(R.drawable.mission_tab03_icon_yellow);
//                    missionMenuIcon04.setImageResource(R.drawable.mission_tab04_icon_yellow);
                    fragmentTransaction.replace(R.id.missionLayout, fragment02).commitAllowingStateLoss();
                    break;
                case R.id.missionMenu03 :
                    fragment03 = new MissionFragment03();
                    missionMenu01.setBackgroundResource(R.drawable.missionlist_tab_off);
                    missionMenu02.setBackgroundResource(R.drawable.missionlist_tab_off);
                    missionMenu03.setBackgroundResource(R.drawable.missionlist_tab_on);
                    missionMenu04.setBackgroundResource(R.drawable.missionlist_tab_off);
//                    missionMenuIcon01.setImageResource(R.drawable.mission_tab01_icon_yellow);
//                    missionMenuIcon02.setImageResource(R.drawable.mission_tab02_icon_yellow);
//                    missionMenuIcon03.setImageResource(R.drawable.mission_tab03_icon_white);
//                    missionMenuIcon04.setImageResource(R.drawable.mission_tab04_icon_yellow);
                    fragmentTransaction.replace(R.id.missionLayout, fragment03).commitAllowingStateLoss();
                    break;
                case R.id.missionMenu04 :
                    fragment04 = new MissionFragment04();
                    missionMenu01.setBackgroundResource(R.drawable.missionlist_tab_off);
                    missionMenu02.setBackgroundResource(R.drawable.missionlist_tab_off);
                    missionMenu03.setBackgroundResource(R.drawable.missionlist_tab_off);
                    missionMenu04.setBackgroundResource(R.drawable.missionlist_tab_on);
//                    missionMenuIcon01.setImageResource(R.drawable.mission_tab01_icon_yellow);
//                    missionMenuIcon02.setImageResource(R.drawable.mission_tab02_icon_yellow);
//                    missionMenuIcon03.setImageResource(R.drawable.mission_tab03_icon_yellow);
//                    missionMenuIcon04.setImageResource(R.drawable.mission_tab04_icon_white);
                    fragmentTransaction.replace(R.id.missionLayout, fragment04).commitAllowingStateLoss();
                    break;
            }
        }
    }

    public class requestGetUserMissionInfo extends AsyncTask<String, String, String> { // 아이디 중복체크 요청
        String userID, userName;
        @Override
        protected void onPreExecute() {
            String userInfo = DataManager.getSharedPreferences_UserInfo(getApplicationContext());
            try{
                JSONObject userJson = new JSONObject(userInfo);
                this.userID = userJson.getString("user_id");
                this.userName = userJson.getString("user_name");

            }catch (Exception e){
                Log.i("seo","requestGetUserMissionInfo JSON error : " + e);
            }
            reqDialog.run();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String result;
            ContentValues value = new ContentValues(); // key와 value을 SET으로 저장하는 객체.
            value.put("user_id", this.userID);
            value.put("user_name", this.userName);
            String url = "http://" + DataManager.connectURL + "/getUserMission";

            HttpGetRequestClass requestHttpURLConnection = new HttpGetRequestClass();
            result = requestHttpURLConnection.request(url, value); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }
        //doInBackground메소드가 끝나면 여기로 와서 텍스트뷰의 값을 바꿔준다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("seo","result : " + result);
            reqDialog.close();
            try{
                JSONArray jsonArray = new JSONArray(result);
                int value = (int)(((float)jsonArray.length() / 28) * 100);
                donutProgress.setDonut_progress(String.valueOf(value));
                txt_clear_mission.setText(jsonArray.length() + "/28");
            }catch (Exception e){

            }
        }
    }
}
