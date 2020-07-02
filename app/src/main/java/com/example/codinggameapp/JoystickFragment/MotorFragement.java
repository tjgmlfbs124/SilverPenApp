package com.example.codinggameapp.JoystickFragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.example.codinggameapp.Utils.DataManager;
import com.example.codinggameapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MotorFragement extends Fragment {
    String [] gageColors = {"#f55142","#f58142","#f5ad42","#f5d442","#fdf542"};
    LinearLayout parentLayout;
    Button [] buttonArray ;
    String[] speed = {"fe","dc","b4","8c","64"};
    Vibrator vibrator;
    String robot;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_motor,container, false);
        robot = DataManager.getSaveConnectedRobot(getContext());
        vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
        DataManager.setJoystickMotorSpeed("64");
        buttonArray = new Button[5];
        buttonArray[0] = (Button)view.findViewById(R.id.btn_setSpeed_5);
        buttonArray[1] = (Button)view.findViewById(R.id.btn_setSpeed_4);
        buttonArray[2] = (Button)view.findViewById(R.id.btn_setSpeed_3);
        buttonArray[3] = (Button)view.findViewById(R.id.btn_setSpeed_2);
        buttonArray[4] = (Button)view.findViewById(R.id.btn_setSpeed_1);

        parentLayout = (LinearLayout)view.findViewById(R.id.parentLayout);

        buttonArray[4].setBackgroundColor(Color.parseColor(gageColors[4]));
        for(int i=0; i<5; i++){
            buttonArray[i].setOnClickListener(new ButtonClickListener());
        }
        return view;
    }

    class ButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int index = parentLayout.indexOfChild(v);
            int maxCount = parentLayout.getChildCount();
            for(int i = maxCount-1; i >= 0; i--){
                if(i >= index) {
                    buttonArray[i].setBackgroundColor(Color.parseColor(gageColors[i]));
                    DataManager.setJoystickMotorSpeed(speed[i]);
                }
                else
                    buttonArray[i].setBackgroundColor(Color.parseColor("#363c46"));
            }
            vibrator.vibrate(150);
        }
    }
}
