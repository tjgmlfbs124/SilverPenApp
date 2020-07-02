package com.example.codinggameapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codinggameapp.Dialog.RequestProgressDialog;
import com.example.codinggameapp.Http.HttpGetRequestClass;
import com.example.codinggameapp.Utils.DataManager;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

public class LoginActivity extends AppCompatActivity {
    private EditText edt_login_id, edt_login_name;
    private TextView btn_login_ok, btn_login_cancel, btn_signUp, btn_getID;
    private RequestProgressDialog reqDialog;
    private AppCompatImageView btn_home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_home = (AppCompatImageView)findViewById(R.id.btn_home);
        reqDialog = new RequestProgressDialog(LoginActivity.this);
        edt_login_id = (EditText)findViewById(R.id.edt_login_id);
        edt_login_name = (EditText)findViewById(R.id.edt_login_name);
        btn_getID = (TextView)findViewById(R.id.btn_getID);

        btn_login_ok = (TextView)findViewById(R.id.btn_login_ok);
        btn_login_ok.setOnClickListener(new ButtonClickListener());
        btn_login_cancel = (TextView)findViewById(R.id.btn_login_cancel);
        btn_login_cancel.setOnClickListener(new ButtonClickListener());
        btn_getID.setOnClickListener(new ButtonClickListener());
        btn_signUp = (TextView)findViewById(R.id.btn_signUp);
        btn_signUp.setOnClickListener(new ButtonClickListener());

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private class ButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){
                case R.id.btn_login_ok :
                    if(edt_login_id.getText().toString().equals("") || edt_login_name.getText().toString().equals(""))
                        Toast.makeText(LoginActivity.this,"이름과 아이디를 입력해주세요.",Toast.LENGTH_SHORT);
                    else {
                        new requestLogin().execute("http://" + DataManager.connectURL + "/signIn");

                    }
                    break;
                case R.id.btn_login_cancel :
                    intent = new Intent(LoginActivity.this, MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case R.id.btn_signUp :
                    intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_getID :
                    intent = new Intent(LoginActivity.this, GetIDActivity.class);
                    startActivity(intent);

                    break;
            }
        }
    }
    public class requestLogin extends AsyncTask<String, String, String> { // 아이디 중복체크 요청
        String userID, userName;
        @Override
        protected void onPreExecute() {
            this.userID = edt_login_id.getText().toString();
            this.userName = edt_login_name.getText().toString();
            reqDialog.run();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String result;
            ContentValues value = new ContentValues(); // key와 value을 SET으로 저장하는 객체.
            value.put("user_id", this.userID);
            value.put("user_name", this.userName);
            String url = "http://" + DataManager.connectURL + "/signIn";

            HttpGetRequestClass requestHttpURLConnection = new HttpGetRequestClass();
            result = requestHttpURLConnection.request(url, value); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        //doInBackground메소드가 끝나면 여기로 와서 텍스트뷰의 값을 바꿔준다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            reqDialog.close();
            Log.i("seo","result : " + result);
            if(result == "null"){

            }
            else{
                try{
                    JSONArray resultArray = new JSONArray(result);
                    int length = resultArray.length();
                    if(length == 0) {
                        Toast.makeText(LoginActivity.this,"정보가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        JSONObject resJsonObject = resultArray.getJSONObject(0);
                        String user_name = resJsonObject.getString("user_name");

                        DataManager.setSharedPreferences_UserInfo(LoginActivity.this, resJsonObject);
                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        Toast.makeText(LoginActivity.this,user_name+"님 환영합니다.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                }catch (Exception e){
                    Log.i("seo","e : " + e);
                }
            }
        }
    }
}
