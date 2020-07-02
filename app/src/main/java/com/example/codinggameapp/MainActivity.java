package com.example.codinggameapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.codinggameapp.Utils.DataManager;

import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    int count = 0;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataManager.removeConnectedRobot(getApplicationContext());
        timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if(count > 1) {
                    timer.cancel();
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else
                    count++;
            }

        };
        timer.schedule(tt, 0, 2000);
    }
}
