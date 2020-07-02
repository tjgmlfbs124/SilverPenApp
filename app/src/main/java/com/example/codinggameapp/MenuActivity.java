package com.example.codinggameapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.example.codinggameapp.Http.HttpPostRequestClass;
import com.example.codinggameapp.Utils.DataManager;

import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

public class MenuActivity extends AppCompatActivity {
    private AppCompatImageView btn_ble, menu_login;
    private FrameLayout menu_code, menu_userInfo, menu_userMission, menu_signup,  menu_Mission, menu_joystick;
    private TextView txt_login, btn_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_menu);
        txt_login = findViewById(R.id.txt_login);
        menu_login = findViewById(R.id.menu_sign_01);
        menu_userInfo = findViewById(R.id.menu_user_btn_01);
        menu_userMission = findViewById(R.id.menu_user_btn_02);
        menu_code = findViewById(R.id.menu_code_btn_01);
        menu_Mission = findViewById(R.id.menu_code_btn_02);
        menu_signup = findViewById(R.id.menu_sign_02);
        menu_joystick = findViewById(R.id.menu_code_btn_03);
        btn_ble = findViewById(R.id.btn_ble);

        menu_login.setOnClickListener(new ButtonClickListener());
        menu_userInfo.setOnClickListener(new ButtonClickListener());
        menu_userMission.setOnClickListener(new ButtonClickListener());
        menu_code.setOnClickListener(new ButtonClickListener());
        menu_signup.setOnClickListener(new ButtonClickListener());
        menu_Mission.setOnClickListener(new ButtonClickListener());
        menu_joystick.setOnClickListener(new ButtonClickListener());
        btn_ble.setOnClickListener(new ButtonClickListener());

        UpdateLoginButton();
        if(BleManager.getInstance().getAllConnectedDevice() == null || (BleManager.getInstance().getAllConnectedDevice()).size() == 0) // 연결된 상태가 아니라면
            btn_ble.setImageResource(R.drawable.bluetooth_white_on);
        else // 연결된 상태라면
            btn_ble.setImageResource(R.drawable.bluetooth_white_off);

        btn_test = (TextView)findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String robot;
                robot = DataManager.getSaveConnectedRobot(getApplicationContext());
                if (robot.equals("")) {
                    Toast.makeText(getApplicationContext(),"로봇과 블루투스 연결이 필요합니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MenuActivity.this,TestActivity.class);
                startActivity(intent);
            }
        });
    }

    public class ButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()){
                case R.id.btn_ble :
                    if(BleManager.getInstance().getAllConnectedDevice() == null || (BleManager.getInstance().getAllConnectedDevice()).size() == 0){ // 블루투스 연결
                        intent = new Intent(MenuActivity.this, BluetoothActivity.class);
//                    startActivity(bt_discovery_intent, ActivityOptions.makeSceneTransitionAnimation(MenuActivity.this).toBundle());
                    }
                    else{ // 블루투스 해제
                        BleManager.getInstance().disconnectAllDevice();
                        BleManager.getInstance().destroy();
                        btn_ble.setImageResource(R.drawable.bluetooth_white_on);
                    }
                    break;
                case R.id.menu_sign_01 : {
                    if(!DataManager.LoginStatus(getApplicationContext()))
                        intent = new Intent(MenuActivity.this, LoginActivity.class);
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                        builder.setTitle("알림");
                        builder.setMessage("로그아웃 하시겠습니까?");
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataManager.removeSharedPreferences_UserInfo(MenuActivity.this);
                                txt_login.setText("로그인");
                                Toast.makeText(MenuActivity.this, "로그아웃 되었습니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                }
                break;

                case R.id.menu_user_btn_01 :{
                    intent = new Intent(MenuActivity.this, UserInfoActivity.class);
                }
                break;

                case R.id.menu_user_btn_02 :{
                    intent = new Intent(MenuActivity.this, UserMissionActivity.class);
                }
                break;

//                case R.id.menu_user_btn_03 : {
//                    intent = new Intent(MenuActivity.this, RankingActivity.class);
//                }
//                break;

                case R.id.menu_code_btn_01 :{
                    if(DataManager.LoginStatus(getApplicationContext()) == true)
                        new requestLogAction().execute("http://" + DataManager.connectURL + "/logAction");
                    intent = new Intent(MenuActivity.this, CodingActivity.class);
                }
                break;

                case R.id.menu_code_btn_02 :{
                    intent = new Intent(MenuActivity.this, MissionListActivity.class);
                }
                break;

                case R.id.menu_sign_02 : {
                    intent = new Intent(MenuActivity.this, SignupActivity.class);
                }
                break;
                case R.id.menu_code_btn_03 : {
                    if(DataManager.LoginStatus(getApplicationContext()) == true)
                        new requestLogAction().execute("http://" + DataManager.connectURL + "/logAction");
                    intent = new Intent(MenuActivity.this, JoystickActivity.class);

                }
                break;
            }
            if(intent != null)
                startActivity(intent);
        }
    }

    void UpdateLoginButton(){
        if(DataManager.LoginStatus(getApplicationContext()))
            txt_login.setText("로그아웃");
        else
            txt_login.setText("로그인");
    }

    private class requestLogAction extends AsyncTask<String, String, String> {
        String userAction, userID;
        String userInfo = DataManager.getSharedPreferences_UserInfo(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        @Override
        protected void onPreExecute() {
            try{
                JSONObject userJson = new JSONObject(userInfo);
                this.userID = userJson.getString("user_id");
                this.userAction = "JOYSTICK";
                jsonObject.accumulate("user_id", this.userID);
                jsonObject.accumulate("user_action", this.userAction);
            }
            catch (Exception e){
                Log.i("seo","json object Error : " + e);
            }
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String result;
            HttpPostRequestClass requestClass = new HttpPostRequestClass();
            result = requestClass.request(urls[0], jsonObject); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("seo","result : " + result);
            if(result == "null"){
                // TODO 예외처리
            }
            else{

            }
        }
    }
}
