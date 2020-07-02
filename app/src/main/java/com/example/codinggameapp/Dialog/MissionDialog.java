package com.example.codinggameapp.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.codinggameapp.Utils.DataManager;
import com.example.codinggameapp.Http.HttpPostRequestClass;
import com.example.codinggameapp.R;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import androidx.appcompat.widget.AppCompatImageView;

public class MissionDialog extends Dialog {
    private AppCompatImageView ms_Image;
    private TextView dialog_message_01, dialog_message_02, dialog_title, btn_start;
    private Context mContext;

    private String message = "";
    private int image_id = 0;

    public MissionDialog(Context context){
        super(context);
        mContext = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.mission_dialog);

        ms_Image = findViewById(R.id.mission_Image);
        dialog_message_01 = findViewById(R.id.dialog_message_01);
        dialog_message_02 = findViewById(R.id.dialog_message_02);
        dialog_title = findViewById(R.id.dialog_title);

        btn_start = (TextView)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MissionDialog.super.onBackPressed();
                new requestLogAction().execute("http://" + DataManager.connectURL + "/logAction");
            }
        });
    }

    public void setDialogConfig(String mission){
        try{
            JSONObject object = new JSONObject(getJsonString(mContext));
            Log.i("seo","object : " + object.toString());
            String config = object.getString(mission);
            Log.i("seo","config : " + config);

            JSONObject configObject = new JSONObject(config);
            String title = configObject.getString("title");
            String message01 = configObject.getString("message01");
            String message02 = configObject.getString("message02");
            String image = configObject.getString("image");
            dialog_title.setText(title);
            dialog_message_01.setText(message01);
            dialog_message_02.setText(message02);

            Context context = ms_Image.getContext();
            int id = context.getResources().getIdentifier(image, "drawable", context.getPackageName());
            ms_Image.setImageResource(id);
        }catch (Exception e){
            Log.i("seo","setDialogConfig error : " + e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private static String getJsonString(Context context){
        String json = "";
        try {
            InputStream is = context.getAssets().open("missionConfig.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return json;
    }

    private class requestLogAction extends AsyncTask<String, String, String> {
        String userID, userAction;
        String userInfo = DataManager.getSharedPreferences_UserInfo(mContext);
        JSONObject jsonObject = new JSONObject();
        @Override
        protected void onPreExecute() {
            try{
                JSONObject userJson = new JSONObject(userInfo);
                this.userID = userJson.getString("user_id");
                this.userAction = "MISSION";

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

        //doInBackground메소드가 끝나면 여기로 와서 텍스트뷰의 값을 바꿔준다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result == ""){
                // TODO 예외처리
            }
            else{

            }
        }
    }
}
