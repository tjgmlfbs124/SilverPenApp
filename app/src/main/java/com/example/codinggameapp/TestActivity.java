package com.example.codinggameapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {
    TextView btn_start;
    public static TextView text_testResult;
    public static ImageView img_barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        BluetoothSendManager.onNotify(getApplicationContext());
        btn_start = (TextView)findViewById(R.id.btn_start);
        text_testResult = (TextView)findViewById(R.id.text_testResult);
        img_barcode = (ImageView)findViewById(R.id.img_test);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothSendManager.sendProtocol("ff"+ Long.toHexString(System.currentTimeMillis() - 1575216556166L ) +"23" );
                Log.i("seo", "send date:"+(System.currentTimeMillis()-1575216556166L ) );
                Log.i("seo", "cur time"+ new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(System.currentTimeMillis()));
            }
        });
    }

    public static void Alram(Context context, String scanTime, long result, String barcode){
        double c = (double)result / 1000;
        int id = context.getResources().getIdentifier("@drawable/barcode" + barcode,"id", context.getPackageName());
        text_testResult.setText("경과시간 결과\n" + "인식시간 : " + scanTime + "\n" + "경과시간 : " + c + "\n" + "인식 바코드 : " + barcode);
        img_barcode.setImageResource(id);
    }
}
