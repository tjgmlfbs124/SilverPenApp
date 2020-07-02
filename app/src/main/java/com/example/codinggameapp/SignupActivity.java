package com.example.codinggameapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codinggameapp.Dialog.RequestProgressDialog;
import com.example.codinggameapp.Http.HttpGetRequestClass;
import com.example.codinggameapp.Http.HttpPostRequestClass;
import com.example.codinggameapp.Utils.DataManager;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

public class SignupActivity extends AppCompatActivity {
    private TextView btn_isCheck, txt_comment_userID, txt_comment_userName, btn_signUp;
    private EditText edt_userID, edt_userName, edt_userYear, edt_userEmail, edt_userPhone;
    private Spinner emailList, sexList;
    private String[] emailItem = new String[]{"naver.com", "gmail.com", "daum.net", "직접입력"};
    private String[] sexItem = new String[]{"남","여"};
    private AppCompatImageView btn_home;
    private boolean isUserCheck = false; // 중복체크
    private RequestProgressDialog reqDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        reqDialog = new RequestProgressDialog(SignupActivity.this);

        btn_home = (AppCompatImageView)findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new ButtonclickListener());

        btn_isCheck = (TextView)findViewById(R.id.btn_idCheck);
        btn_signUp = (TextView)findViewById(R.id.btn_signUp);

        txt_comment_userID = (TextView)findViewById(R.id.txt_comment_userID);
        txt_comment_userName = (TextView)findViewById(R.id.txt_comment_userName);

        emailList = findViewById(R.id.spinner_emailList);
        sexList = (Spinner) findViewById(R.id.spinner_userSex);

        ArrayAdapter<String> emailSpinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, emailItem);
        ArrayAdapter<String> sexSpinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sexItem);

        emailList.setAdapter(emailSpinner_adapter);
        sexList.setAdapter(sexSpinner_adapter);
        emailList.setOnItemSelectedListener(new SpinnerItemSelectedListener());
        sexList.setOnItemSelectedListener(new SpinnerItemSelectedListener());

        edt_userID = (EditText)findViewById(R.id.edt_userID);
        edt_userName = (EditText)findViewById(R.id.edt_userName);
        edt_userYear = (EditText)findViewById(R.id.edt_userYear);
        edt_userEmail = (EditText)findViewById(R.id.edt_userEmail);
        edt_userPhone = (EditText)findViewById(R.id.edt_userPhone);
        edt_userPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        btn_isCheck.setOnClickListener(new ButtonclickListener());
        btn_signUp.setOnClickListener(new ButtonclickListener());
    }

    private class ButtonclickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_idCheck :
                    if(edt_userID.getText().toString().equals(""))  Toast.makeText(SignupActivity.this,"아이디를 입력해주세요",Toast.LENGTH_SHORT).show();
                    else{
                        new requestIDCheck().execute("http://" + DataManager.connectURL + "/isUser");
                    }
                    break;

                case R.id.btn_signUp :
                    if(isUserCheck == false ) Toast.makeText(SignupActivity.this,"ID 중복확인부터 해주세요", Toast.LENGTH_SHORT).show();
                    else if (edt_userID.getText().toString().equals("") || edt_userName.getText().toString().equals("")) Toast.makeText(SignupActivity.this,"ID와 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    else new requestSignUp().execute("http://" + DataManager.connectURL + "/signUp");
                    break;

                case R.id.btn_home :
                    Intent intent = new Intent(SignupActivity.this, MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
            }
        }
    }

    private class SpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try{
                ((TextView)parent.getChildAt(0)).setTextColor(Color.parseColor("#303030"));
            }catch (Exception e){
                Log.i("seo","[SpinnerItemSelectedListener] error : " + e);
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public class requestIDCheck extends AsyncTask<String, String, String> { // 아이디 중복체크 요청
        String userID;
        @Override
        protected void onPreExecute() {
            this.userID = edt_userID.getText().toString();
            reqDialog.run();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String result;
            ContentValues value = new ContentValues(); // key와 value을 SET으로 저장하는 객체.
            value.put("user_id", this.userID);
            HttpGetRequestClass requestHttpURLConnection = new HttpGetRequestClass();
            result = requestHttpURLConnection.request(urls[0], value); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        //doInBackground메소드가 끝나면 여기로 와서 텍스트뷰의 값을 바꿔준다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            reqDialog.close();
            Log.i("seo","result : " + result);
            if(result == "null"){
                Toast.makeText(getApplication(),"서버와의 통신에 문제가 있습니다.",Toast.LENGTH_SHORT).show();
            }
            else{
                try{
                    JSONArray resultArray = new JSONArray(result);
                    int length = resultArray.length();
                    Log.i("seo","resultArray.length(): " + resultArray.length());
                    if(length == 0) {
                        Toast.makeText(SignupActivity.this,"사용가능한 ID입니다.", Toast.LENGTH_SHORT).show();
                        txt_comment_userID.setText("사용가능한 ID입니다.");
                        isUserCheck = true;
                        edt_userID.setEnabled(false);
                    }
                    else{
                        Toast.makeText(SignupActivity.this,"중복된 ID입니다.", Toast.LENGTH_SHORT).show();
                        isUserCheck = false;
                        txt_comment_userID.setText("다른 아이디를 사용하세요.");
                    }
                }catch (Exception e){
                    Log.i("seo","e : " + e);
                }
            }
        }
    }
    public class requestSignUp extends AsyncTask<String, String, String> {
        String userID, userName, userYear, userEmail, userPhone, userSex;
        JSONObject jsonObject = new JSONObject();
        @Override
        protected void onPreExecute() {
            this.userID = edt_userID.getText().toString();
            this.userName = edt_userName.getText().toString();
            this.userEmail = edt_userEmail.getText().toString().equals("") ? "" : edt_userEmail.getText().toString() + "@" + (String)emailList.getSelectedItem();
            this.userPhone = edt_userPhone.getText().toString();
            this.userYear = edt_userYear.getText().toString();
            this.userSex = (String)sexList.getSelectedItem();
            try{
                jsonObject.accumulate("user_id", userID);
                jsonObject.accumulate("user_name", userName);
                jsonObject.accumulate("user_email", userEmail);
                jsonObject.accumulate("user_phone", userPhone);
                jsonObject.accumulate("user_sex", userSex);
                jsonObject.accumulate("user_year", userYear);
            }
            catch (Exception e){
                Log.i("seo","json object Error : " + e);
            }
            reqDialog.run();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String result;
            HttpPostRequestClass requestClass = new HttpPostRequestClass();
            result = requestClass.request(urls[0], jsonObject); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        //doInBackground메소드가 끝나면 여기로 와서 텍스트뷰의 값을 바꿔준다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            reqDialog.close();
            if(result == ""){
                // TODO 예외처리
            }
            else{
                Intent intent = new Intent(SignupActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(SignupActivity.this,edt_userID.getText().toString() + "님의 정보가 등록되었습니다.",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }
    }
}
