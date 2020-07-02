package com.example.codinggameapp.Dialog;

import android.app.ProgressDialog;
import android.content.Context;

public class RequestProgressDialog {
    public static Context mContext;
    public static ProgressDialog asyncDialog;

    public RequestProgressDialog(Context context){ // Constructor
        asyncDialog = new ProgressDialog(context);
        mContext = context;
    }

    public static void run(){
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("해당 정보를 가져오는중입니다..");
        asyncDialog.show();
    }

    public static void close(){
        asyncDialog.dismiss();
    }
}