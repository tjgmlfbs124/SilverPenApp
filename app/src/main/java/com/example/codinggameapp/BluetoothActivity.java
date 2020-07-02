package com.example.codinggameapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.example.codinggameapp.Utils.DataManager;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by USER on 2017-11-13.
 */

public class BluetoothActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2; // @SEO BLE

    private AppCompatImageView SearchButton, CancelImg;
    TextView bluetooth_txt_message;
    private ProgressDialog progressDialog;
    static Menu bleSearchList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        bluetooth_txt_message = (TextView) findViewById(R.id.bluetooth_txt_message);
        progressDialog = new ProgressDialog(this); // @SEO 블루투스 연결할때 사용
        /* eggbean around circle animation */
        final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        // @SEO BLE Scanner 초기설정
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(30000)
                .setOperateTimeout(5000);

        checkPermissions();
    }

    // @SEO 권한을 체크하는 함수
    private void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetooth_txt_message.setText("블루투스 권한을 검사합니다..");
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this,"블루투스를 켜주세요.." ,Toast.LENGTH_LONG).show();
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    // @SEO GPS 권한을 체크하는 함수
    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // @SEO 블루투스 권한이 확인했을때 호출되는 함수.
    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle("경고")
                            .setMessage("!!!!")
                            .setNegativeButton("취소",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton("설정",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                } else {
//                    if(DataManager.Get_BleName(getApplicationContext()) != "null")
                        startScan();
//                    else
//                        Toast.makeText(getApplicationContext(), "블루투스 연결해제", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void startScan() {
        bluetooth_txt_message.setText("로봇을 재탐색합니다.");
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                Log.i("seo","[startScan] onScanStarted");
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
                if(bleDevice.getName() != null){
                    int rssi = bleDevice.getRssi();
//                    Log.i("seo","bleDevice.getScanRecord(): " + bleDevice.getScanRecord());
                    Log.i("seo","bleDevice : " + bleDevice.getName() + " / rssi : " + rssi);
//                    Log.i("seo","bleDevice : " + bleDevice.getName() + " / rssi : " + rssi);
                    if(rssi > -45 ){
                        bluetooth_txt_message.setText("로봇을 검사하고있습니다");
                        if(bleDevice.getName().contains("JKP-3000") && !BleManager.getInstance().isConnected(bleDevice)){
                            BleManager.getInstance().cancelScan();
                            connect(bleDevice, "weeemake");
                        }

//                        if(bleDevice.getName().contains("WeeeMake") && !BleManager.getInstance().isConnected(bleDevice)){
//                            BleManager.getInstance().cancelScan();
//                            connect(bleDevice, "weeemake");
//                        }
                        else if(bleDevice.getName().contains("camRobot") && !BleManager.getInstance().isConnected(bleDevice)){
                            BleManager.getInstance().cancelScan();
                            connect(bleDevice, "camRobot");
                        }
//                        else if(bleDevice.getName().contains("eggBean") && !BleManager.getInstance().isConnected(bleDevice)){
//                            BleManager.getInstance().cancelScan();
//                            connect(bleDevice, "eggBean");
//                        }
//                        else if(bleDevice.getName().contains("unityBLE") && !BleManager.getInstance().isConnected(bleDevice)){
//                            BleManager.getInstance().cancelScan();
//                            connect(bleDevice, "camRobot");
//                        }
//                        else{
//                            // TODO 안내메시지 ?
//                        }
                    }
                }
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
//                startScan();
            }
        });
    }

    private void connect(final BleDevice bleDevice, final String robot) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                if(!((Activity) BluetoothActivity.this).isFinishing())
                    progressDialog.show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "연결을 실패했습니다.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                DataManager.setConnectedDevice(bleDevice); // @SEO 연결된 ble 정보를 여러곳에서 쓰기위해 DataManager에 static으로 getter, setter 해둔게 있음.

                // Shared DB에 연결된 로봇 저장.
                DataManager.saveConnectedRobot(getApplicationContext(), robot);
                switch (robot){
                    case "eggBean" :
//                        BluetoothSendManager.sendProtocol("ff05030023");
                        break;
                    case "camRobot" :
                        BluetoothSendManager.sendProtocol("ff060023");
                        BluetoothSendManager.onNotify(getApplicationContext());
                        break;
                    case "weeemake" :
                        BluetoothSendManager.sendProtocol("ff23");
                        break;
                }
                progressDialog.dismiss();
                Intent intent = new Intent(BluetoothActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();
                BleManager.getInstance().disconnectAllDevice();
                BleManager.getInstance().destroy();
                if (isActiveDisConnected) {
                    Toast.makeText(getApplicationContext(),"연결을 해제했습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "연결을 해제했습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BluetoothActivity.this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
