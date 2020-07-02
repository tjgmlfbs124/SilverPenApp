package com.example.codinggameapp.JoystickFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.codinggameapp.BluetoothSendManager;
import com.example.codinggameapp.Utils.DataManager;
import com.example.codinggameapp.R;
import com.larswerkman.holocolorpicker.ColorPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RgbFragment extends Fragment {
    ColorPicker colorPickerView;
    SharedPreferences pref;
    int color;
    private String robot;
    Vibrator vibrator;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_light, container, false);
        colorPickerView = (ColorPicker)view.findViewById(R.id.colorPickerView);
        robot = DataManager.getSaveConnectedRobot(getContext());
        vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
        colorPickerView.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                color = colorPickerView.getColor();
                String s_color = Integer.toHexString(color).substring(2);
                if (robot.equals("")) {
                    Toast.makeText(getContext(),"로봇과 블루투스 연결이 필요합니다.",Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    switch (robot){
                        case "weeemake" :
                            BluetoothSendManager.sendProtocol("ff23");
                            break;
                        case "eggBean" :
                            BluetoothSendManager.sendProtocol("ff0200"+s_color+"23");
                            Log.i("seo","rgb color : " + s_color);
                            break;
                        case "camRobot" :
                            BluetoothSendManager.sendProtocol("ff02"+s_color+"23");
                            break;
                        default:
                            Toast.makeText(getContext(),"블루투스를 켜주세요.",Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                catch (Exception e){
                    Log.i("seo","error : " + e);
                }
                vibrator.vibrate(100);
            }
        });
//        colorPickerView.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
//            @Override
//            public void onColorChanged(int color) {
//                color = colorPickerView.getColor();
//                String s_color = Integer.toHexString(color).substring(2);
//                Log.i("seo","rgb color : " + s_color);
//                switch (BluetoothSendManager.getSelectBleRobot()){
//                    case "weeemake" :
//                        BluetoothSendManager.sendProtocol("ff23");
//                        break;
//                    case "eggBean" :
//                        BluetoothSendManager.sendProtocol("ff0200"+s_color+"0000000023");
//                        break;
//                    case "camRobot" :
//                        BluetoothSendManager.sendProtocol("ff02"+s_color+"23");
//                        break;
//                }
//            }
//        });
        return view;

    }
}
