package com.example.codinggameapp.Dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.codinggameapp.CodingActivity;
import com.example.codinggameapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class PictureDialog extends BottomSheetDialogFragment{
    private AppCompatImageView img_barcode;
    private TextView txt_barcode, txt_title, btn_missionOk, txt_message02;
    private String barcode;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_picture, container, false);

        Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        vibrator.vibrate(200);

//        img_barcode = view.findViewById(R.id.img_barcode);
//        txt_barcode = view.findViewById(R.id.txt_barcode);

        txt_title = view.findViewById(R.id.txt_title);
//        txt_message01 = view.findViewById(R.id.txt_message01);
        txt_message02 = view.findViewById(R.id.txt_message02);
        btn_missionOk = view.findViewById(R.id.btn_missionOk);
        btn_missionOk.setOnClickListener(new buttonclickListener());

        barcode = getArguments().getString("barcode");
        int imageID = getResources().getIdentifier("barcode"+ barcode,"drawable",getActivity().getPackageName());

//        img_barcode.setImageResource(imageID);
//        txt_barcode.setText(barcode + "번 바코드");
        try{
            JSONObject object = new JSONObject(getJsonString());
            object = new JSONObject(object.getString("barcode"+barcode));
            txt_title.setText("[" + object.getString("title") + "]  " +  object.getString("message01"));
            txt_message02.setText(object.getString("message02"));
            Log.i("seo","title : " + object.getString("title"));
            Log.i("seo","message01 : " + object.getString("message01"));
            Log.i("seo","message02 : " + object.getString("message02"));
            Log.i("seo","image : " + object.getString("image"));

        }catch (JSONException e){
            Log.i("seo","e : " + e);
        }
        Log.i("seo","barcode : " + barcode);
        return view;
    }

    private String getJsonString(){
        String json = "";
        try {
            InputStream is = getActivity().getAssets().open("barcode_MissionConfig.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");

            return json;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return json;
    }

    public class buttonclickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_missionOk:
                    Intent intent = new Intent(getActivity(), CodingActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("category","default/mission_barcode/barcode_mission_toolbox_" + barcode + ".xml");
                    mBundle.putString("mode","barcode");
                    intent.putExtras(mBundle);
                    startActivity(intent);
                    break;
            }
        }
    }
}
