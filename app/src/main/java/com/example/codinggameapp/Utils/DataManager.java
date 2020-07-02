package com.example.codinggameapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.clj.fastble.data.BleDevice;

import org.json.JSONObject;

public class DataManager {
//    public static String connectURL = "183.111.226.60:13700"; // 서버컴퓨터 IP
    public static String connectURL = "192.168.0.52:3000"; // 개발컴퓨터 IP
    static SharedPreferences pref;
    private static String BLE = "BLE";
    public static BleDevice mBleDevice;
    static String mSpeed = "00";

    public static void setSharedPreferences_UserInfo(Context context, JSONObject jsonObject){
        try{
            pref = context.getSharedPreferences("UserInfo",context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("userJson", jsonObject.toString());
            editor.commit();
        }catch (Exception e){
            Log.i("seo","error: " + e);
        }
    }

    public static String getSharedPreferences_UserInfo(Context context){
        pref = context.getSharedPreferences("UserInfo", context.MODE_PRIVATE);
        return pref.getString("userJson", "");
    }

    public static void removeSharedPreferences_UserInfo(Context context){
        pref = context.getSharedPreferences("UserInfo", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    // @SEO 연결된 Ble 디바이스 정보를 static 형식으로 저장한다. -> 다른 액티비티에 리턴해주기위해.
    public static void setConnectedDevice(BleDevice device){
        mBleDevice = device;
    }

    // @SEO 저장했었던 연결된 Ble 디바이스를 초기화 한다.
    public static void removeConntectedDevice(){
        mBleDevice = null;
    }

    // @SEO 저장했었던 연결된 Ble 디바이스 정보를 리턴한다.
    public static BleDevice getConnectedDevice(){
        return mBleDevice;
    }

    public static void saveConnectedRobot(Context context, String robot){
        SharedPreferences pref = context.getSharedPreferences("robotDB",context.MODE_PRIVATE);
        SharedPreferences.Editor pref_editor = pref.edit();
        pref_editor.putString("robot",robot);
        pref_editor.commit();
    }
    public static void removeConnectedRobot(Context context){
        SharedPreferences pref = context.getSharedPreferences("robotDB",context.MODE_PRIVATE);
        SharedPreferences.Editor pref_editor = pref.edit();
        pref_editor.remove("robot");
        pref_editor.commit();
    }
    public static String getSaveConnectedRobot(Context context){
        String robot;
        SharedPreferences pref = context.getSharedPreferences("robotDB",context.MODE_PRIVATE);
        SharedPreferences.Editor pref_editor = pref.edit();
        pref = context.getSharedPreferences("robotDB",context.MODE_PRIVATE);
        robot = pref.getString("robot","");
        Log.i("seo","Currently Connected robot is " + robot);
        return robot;
    }

    public static boolean LoginStatus(Context context){
        if(getSharedPreferences_UserInfo(context).equals("")){
            return false;
        }
        else{
            return true;
        }
    }

    public static void setJoystickMotorSpeed(String speed){
        mSpeed = speed;
    }
    public static String getJoystickMotorSpeed(){
        if(mSpeed.equals("00")) return "64";
        else return mSpeed;
    }
}
