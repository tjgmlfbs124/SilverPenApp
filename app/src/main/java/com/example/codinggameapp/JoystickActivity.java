package com.example.codinggameapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andretietz.android.controller.DirectionView;
import com.andretietz.android.controller.InputView;
import com.example.codinggameapp.Dialog.PictureDialog;
import com.example.codinggameapp.JoystickFragment.MelodyFragment;
import com.example.codinggameapp.JoystickFragment.MotorFragement;
import com.example.codinggameapp.JoystickFragment.RgbFragment;
import com.example.codinggameapp.Utils.DataManager;
import com.example.codinggameapp.Utils.ScalableLayout;
import com.example.codinggameapp.Utils.Stopwatch;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class JoystickActivity extends AppCompatActivity {
    private static AppCompatImageView img_light, img_tone, img_melody, img_motor;
    private static LinearLayout fragment_joystick;
    public static Vibrator vibrator;
    private String preFragment = ""; // 열었던 Fragment 저장
    private String robot;
    private String serialHexValue = "";
    private DirectionView viewDirection;
    public static PictureDialog pictureDialog = new PictureDialog();
    public static TextView txt_navi_count, txt_navi_timer;
    public static ScalableLayout titlebar_logo, titlebar_count;
    public static ArrayList<Integer> getNaviList = new ArrayList<Integer>();
    public static Stopwatch stopWatch = new Stopwatch();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        setContentView(R.layout.activity_joystick);
        robot = DataManager.getSaveConnectedRobot(getApplicationContext());
        img_light = findViewById(R.id.img_light);
        img_tone = findViewById(R.id.img_Tone);
        img_melody = findViewById(R.id.img_melody);
        img_motor =  findViewById(R.id.img_motor);
        fragment_joystick = findViewById(R.id.fragment_joystick);

        txt_navi_count =  findViewById(R.id.txt_navi_count);
        txt_navi_timer = findViewById(R.id.txt_timer);

        titlebar_logo = findViewById(R.id.titlebar_logo);
        titlebar_count = findViewById(R.id.titlebar_count);

        viewDirection = (DirectionView)findViewById(R.id.viewDirection);
        viewDirection.setOnButtonListener(new ActionListener());

        img_light.setOnClickListener(new ImageClickListener());
        img_tone.setOnTouchListener(new ImageTouchListener());
        img_melody.setOnClickListener(new ImageClickListener());
        img_motor.setOnClickListener(new ImageClickListener());

        if(!robot.equals("")) {
            BluetoothSendManager.onNotify(this);
        }

        stopWatch.setListener(new StopwatchListener());
        getNaviList.clear();
    }
    private class ImageTouchListener implements View.OnTouchListener{ // 부저 터치
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN :
                    vibrator.vibrate(100);
                    img_tone.setBackgroundResource(R.drawable.shape_joystick_light_background);
                    if(robot.equals("")) {
                        Toast.makeText(getApplicationContext(),"로봇과 블루투스 연결이 필요합니다.",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    switch (robot){
                        case "weeemake" :
                            BluetoothSendManager.sendProtocol("ff23");
                            break;
                        case "eggBean" :
                            BluetoothSendManager.sendProtocol("ff050423");
                            break;
                        case "camRobot" :
                            BluetoothSendManager.sendProtocol("ff05040023");
                            break;
                    }

                    break;
                case MotionEvent.ACTION_UP :
                    img_tone.setBackgroundResource(R.drawable.shape_joystick_dark_background);
                    if(robot.equals("")) return true;
                    switch (robot){
                        case "weeemake" :
                            BluetoothSendManager.sendProtocol("ff23");
                            break;
                        case "eggBean" :
                            BluetoothSendManager.sendProtocol("ff050523");
                            break;
                        case "camRobot" :
                            BluetoothSendManager.sendProtocol("ff05000023");
                            break;
                    }
                    break;
            }
            return true;
        }
    }

    private class ImageClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            fragment_joystick.setVisibility(View.VISIBLE);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            switch (v.getId()){
                case R.id.img_light :
                    vibrator.vibrate(100);
                    if(preFragment.equals("Light")) {
                        fragmentTransaction.remove(fm.getFragments().get(0));
                        img_light.setImageResource(R.drawable.joystick_rgb_off);
                        preFragment = "";
                    }
                    else{
                        img_light.setImageResource(R.drawable.joystick_rgb_on);
                        img_melody.setImageResource(R.drawable.joystick_melody_off);
                        img_tone.setImageResource(R.drawable.joystick_buzzer_off);
                        img_motor.setImageResource(R.drawable.joystick_speed_off);
                        fragmentTransaction.replace(R.id.fragment_joystick, new RgbFragment());
                        preFragment = "Light";
                    }
                    break;
                case R.id.img_melody :
                    vibrator.vibrate(100);
                    if(preFragment.equals("Melody")){
                        fragmentTransaction.remove(fm.getFragments().get(0));
                        img_melody.setImageResource(R.drawable.joystick_melody_off);
                        preFragment = "";
                    }
                    else{
                        img_light.setImageResource(R.drawable.joystick_rgb_off);
                        img_melody.setImageResource(R.drawable.joystick_melody_on);
                        img_tone.setImageResource(R.drawable.joystick_buzzer_off);
                        img_motor.setImageResource(R.drawable.joystick_speed_off);
                        fragmentTransaction.replace(R.id.fragment_joystick, new MelodyFragment());
                        preFragment = "Melody";
                    }
                    break;
                case R.id.img_motor :
                    vibrator.vibrate(100);
                    if(preFragment.equals("Motor")){
                        fragmentTransaction.remove(fm.getFragments().get(0));
                        img_motor.setImageResource(R.drawable.joystick_speed_off);
                        preFragment = "";
                    }
                    else{
                        img_light.setImageResource(R.drawable.joystick_rgb_off);
                        img_melody.setImageResource(R.drawable.joystick_melody_off);
                        img_tone.setImageResource(R.drawable.joystick_buzzer_off);
                        img_motor.setImageResource(R.drawable.joystick_speed_on);
                        fragmentTransaction.replace(R.id.fragment_joystick, new MotorFragement());
                        preFragment = "Motor";
                    }
                    break;
            }
            fragmentTransaction.commit();
        }
    }
    private class ActionListener implements InputView.InputEventListener{
        @Override
        public void onInputEvent(View view, int buttons) {
            Log.i("seo","robot : " + robot);
            if(robot.equals("")) {
                Toast.makeText(getApplicationContext(),"블루투스를 켜주세요.",Toast.LENGTH_SHORT).show();
                return;
            }
            Log.i("seo","speed : " + DataManager.getJoystickMotorSpeed());
            switch (view.getId()){
                case R.id.viewDirection :
                    switch (directionButtonsToString(buttons)){
                        case "Up" :
                            switch (robot){
                                case "weeemake" :
                                    BluetoothSendManager.sendProtocol("ff23");
                                    break;
                                case "eggBean" :
                                    BluetoothSendManager.sendProtocol("ff0400" + DataManager.getJoystickMotorSpeed() + "0023");
                                    break;
                                case "camRobot" :
                                    BluetoothSendManager.sendProtocol("ff0400"+ DataManager.getJoystickMotorSpeed() +"0023");
                                    break;
                            }
                            break;
                        case "Down" :
                            switch (robot){
                                case "weeemake" :
                                    BluetoothSendManager.sendProtocol("ff23");
                                    break;
                                case "eggBean" :
                                    BluetoothSendManager.sendProtocol("ff0401" + DataManager.getJoystickMotorSpeed() + "0023");
                                    break;
                                case "camRobot" :
                                    BluetoothSendManager.sendProtocol("ff0401" + DataManager.getJoystickMotorSpeed() + "0023");
                                    break;
                            }
                            break;
                        case "Left" :
                            switch (robot){
                                case "weeemake" :
                                    BluetoothSendManager.sendProtocol("ff23");
                                    break;
                                case "eggBean" :
                                    BluetoothSendManager.sendProtocol("ff0402" + DataManager.getJoystickMotorSpeed() + "0023");
                                    break;
                                case "camRobot" :
                                    BluetoothSendManager.sendProtocol("ff0402"+ DataManager.getJoystickMotorSpeed() + "0023");
                                    break;
                            }
                            break;
                        case "Right" :
                            switch (robot){
                                case "weeemake" :
                                    BluetoothSendManager.sendProtocol("ff23");
                                    break;
                                case "eggBean" :
                                    BluetoothSendManager.sendProtocol("ff0403" + DataManager.getJoystickMotorSpeed() + "0023");
                                    break;
                                case "camRobot" :
                                    BluetoothSendManager.sendProtocol("ff0403" + DataManager.getJoystickMotorSpeed() + "0023");
                                    break;
                            }
                            break;
                        default :
                            switch (robot){
                                case "weeemake" :
                                    BluetoothSendManager.sendProtocol("ff23");
                                    break;
                                case "eggBean" :
                                    BluetoothSendManager.sendProtocol("ff0404000023");
                                    break;
                                case "camRobot" :
                                    BluetoothSendManager.sendProtocol("ff0404000023");
                                    break;
                            }
                            break;
                    }
                    break;
            }
            // TODO 블루투스 연결확인도 항상 할것.
            // TODO 연결상태 TEXT변화 필요
            BluetoothSendManager.sendProtocol(serialHexValue);
        }
    }
    private String directionButtonsToString(int buttons) {
        String direction = "NONE";
        switch (buttons&0xff) {
            case DirectionView.DIRECTION_DOWN:
                direction = "Down";
                break;
            case DirectionView.DIRECTION_LEFT:
                direction = "Left";
                break;
            case DirectionView.DIRECTION_RIGHT:
                direction = "Right";
                break;
            case DirectionView.DIRECTION_UP:
                direction = "Up";
                break;
        }
        return direction;
    }

    public static void showPicture(FragmentManager manager, String barcode){
        try{
            if(!pictureDialog.isAdded()){
                Bundle args = new Bundle();
                args.putString("barcode", barcode);
                pictureDialog.setArguments(args);
                pictureDialog.show(manager, "Dialog");
            }
        }catch (Exception e){
            Log.i("seo","Error  :"  + e);
        }

    }

    public static void addNaviCount(Activity activity, String barcode){
        int mBarcode = Integer.parseInt(barcode);
        if(!isDuplicationNavi(mBarcode)){
            getNaviList.add(mBarcode);
            vibrator.vibrate(200);
            if(titlebar_count.getVisibility() == View.GONE){
                stopWatch.start();
                titlebar_logo.setVisibility(View.GONE);
                titlebar_count.setVisibility(View.VISIBLE);
            }
            txt_navi_count.setText("X" + getNaviList.size());
        }
        else{
            Toast.makeText(activity, "이미 획득한 나비입니다.", 200).show();
        }
        try{

        }catch (Exception e){
            Log.i("seo","Error  :"  + e);
        }
    }

    public static boolean isDuplicationNavi(int mBarcode){
        if(getNaviList.contains(mBarcode))
            return true;
        else
            return false;
    }

    class StopwatchListener implements Stopwatch.StopWatchListener{
        @Override
        public void onTick(String time) {
            int count = 30 - Integer.parseInt(time);
            if (count <= 0){
                stopWatch.stop();
                vibrator.vibrate(1000);
                AlertDialog.Builder builder = new AlertDialog.Builder(JoystickActivity.this);
                builder.setTitle("놀이 결과");
                builder.setMessage("획득한 나비 숫자 : " + getNaviList.size() +"개");
                builder.setPositiveButton("다시하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getNaviList.clear();
                        titlebar_logo.setVisibility(View.VISIBLE);
                        titlebar_count.setVisibility(View.GONE);
                        Toast.makeText(JoystickActivity.this,"게임을 다시 시작해주세요!",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.show();
            }
            else{
                txt_navi_timer.setText(count+"");
            }
        }
    }
}
