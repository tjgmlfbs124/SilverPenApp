package com.example.codinggameapp.JoystickFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.codinggameapp.BluetoothSendManager;
import com.example.codinggameapp.Utils.DataManager;
import com.example.codinggameapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MelodyFragment extends Fragment {
    private String robot;
    private Vibrator vibrator;
    private boolean isPlay = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_melody, container, false);

        LinearLayout btn_melody1, btn_melody2, btn_melody3, btn_melody4;

        robot = DataManager.getSaveConnectedRobot(getContext());
        vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
        btn_melody1 = (LinearLayout)view.findViewById(R.id.btn_melody1);
        btn_melody2 = (LinearLayout)view.findViewById(R.id.btn_melody2);
        btn_melody3 = (LinearLayout)view.findViewById(R.id.btn_melody3);
        btn_melody4 = (LinearLayout)view.findViewById(R.id.btn_melody4);

        btn_melody1.setOnClickListener(new ButtonClickListener());
        btn_melody2.setOnClickListener(new ButtonClickListener());
        btn_melody3.setOnClickListener(new ButtonClickListener());
        btn_melody4.setOnClickListener(new ButtonClickListener());
        return view;
    }

    public class ButtonClickListener implements View.OnClickListener{
        String melodyNumber = "00" ;
        @Override
        public void onClick(View v) {
            vibrator.vibrate(150);
            if(!robot.equals("camRobot")) {
                Toast.makeText(getContext(),"로봇과 블루투스 연결이 필요합니다.",Toast.LENGTH_SHORT).show();
                return;
            }
            if(!isPlay){
                switch (v.getId()){
                    case R.id.btn_melody1 :
                        melodyNumber = "00";
                        break;
                    case R.id.btn_melody2 :
                        melodyNumber = "01";
                        break;
                    case R.id.btn_melody3 :
                        melodyNumber = "02";
                        break;
                    case R.id.btn_melody4 :
                        melodyNumber = "03";
                        break;
                }
                BluetoothSendManager.sendProtocol("ff03" + melodyNumber + "0023");
            }
            else{
                BluetoothSendManager.sendProtocol("ff03" + "04" + "0023");
            }
            isPlay = !isPlay;
        }
    }
}
