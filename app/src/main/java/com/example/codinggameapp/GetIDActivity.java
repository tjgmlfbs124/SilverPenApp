package com.example.codinggameapp;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.example.codinggameapp.Utils.ScalableLayout;

import androidx.appcompat.app.AppCompatActivity;

public class GetIDActivity extends AppCompatActivity {
    private EditText edt_name;
    private TextView txt_getId, txt_result, txt_close;
    private ScalableLayout resultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_id);
        edt_name = (EditText)findViewById(R.id.edt_get_name);
        txt_getId = (TextView)findViewById(R.id.btn_getID);
        txt_result = (TextView)findViewById(R.id.txt_result);
        txt_close = (TextView)findViewById(R.id.btn_close);
        resultLayout = (ScalableLayout)findViewById(R.id.layout_result);

        txt_getId.setOnClickListener(new ButtonClickListener());
        txt_result.setOnClickListener(new ButtonClickListener());
        txt_close.setOnClickListener(new ButtonClickListener());
    }

    private class ButtonClickListener implements View.OnClickListener{
        Animation animation;
        @Override
        public void onClick(View v) {
            resultLayout.clearAnimation();
            switch (v.getId()){
                case R.id.btn_getID :
                    animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_move_to_top);
                    resultLayout.setVisibility(View.VISIBLE);
                    resultLayout.startAnimation(animation);
                    break;
                case R.id.txt_result :
                    break;
                case R.id.btn_close :
                    animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_move_to_bottom);
                    resultLayout.startAnimation(animation);
                    resultLayout.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
