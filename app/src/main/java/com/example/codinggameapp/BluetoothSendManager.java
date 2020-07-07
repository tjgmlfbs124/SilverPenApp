package com.example.codinggameapp;


import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.example.codinggameapp.Utils.DataManager;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;

public class BluetoothSendManager {
    static BluetoothGattService service = BleManager.getInstance().getBluetoothGattServices(DataManager.getConnectedDevice()).get(2);
    static BluetoothGattCharacteristic characteristic = BleManager.getInstance().getBluetoothGattCharacteristics(service).get(0);
    static boolean isReadValue = false;
    public static void sendProtocol(String serialHexValue){
        if(serialHexValue == null) return ;
        try{
            Log.i("seo","[" + "robot" + "] serialHexValue : " + serialHexValue);
            BleManager.getInstance().write(
                    DataManager.getConnectedDevice(),
                    characteristic.getService().getUuid().toString(),
                    characteristic.getUuid().toString(),
                    HexUtil.hexStringToBytes(serialHexValue),
                    new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                            Log.i("seo","success");
                        }

                        @Override
                        public void onWriteFailure(final BleException exception) {
                            Log.i("seo","Bluetooth Error : " + exception);
                        }
                    }
            );
        }catch (Exception e){
            Log.i("seo","Bluetooth Error : " + e);
        }
    }

    /** @SEO
     * 로봇에서 'D'를 받는지 검사하는 함수
     * 목적 : 코딩블록 화면에서 로봇과 싱크를 맞추기위해서 로봇에서 "D"를 받으면, 다음 블록을 수행한다.
     * ex : RGB 변화하는 블록이 3개가 있다.
     *      첫번째 RGB 수행명령을 BLE로 보낸후, 로봇에서 D를 줄때까지 다음 RGB변화 블록을 보내지 않는다.
     *      로봇에서 RGB를 바꾸고 D를 안드로이드로 송신 !
     *      안드로이드에서 D를 받으면 다음 RGB 변화블록을 수행한다.
     * @return : D를 받으면 true, D를 못받으면 false.
     */
    public static boolean getSuccessBLESignal(){
        Log.i("seo","getSuccessBLESignal");

        BleManager.getInstance().read(
            DataManager.getConnectedDevice(),
            characteristic.getService().getUuid().toString(),
            characteristic.getUuid().toString(),
            new BleReadCallback() {
                @Override
                public void onReadSuccess(byte[] data) {
                    int a = (int)data[0];
                    Log.i("seo","a : " + a);
                    isReadValue = true;
                }

                @Override
                public void onReadFailure(BleException exception) {
                    Log.i("seo","READ BleException : " + exception);
                }
            });
        return isReadValue;
    }

    public static void onNotify(final AppCompatActivity activity) {
        Log.i( "ckw","startNotify" );

        BleManager.getInstance().notify(
            DataManager.getConnectedDevice(),
            characteristic.getService().getUuid().toString(),
            characteristic.getUuid().toString(),
            new BleNotifyCallback() {
                @Override
                public void onNotifySuccess() {
                    Log.i( "ckw","on notify" );
                }

                @Override
                public void onNotifyFailure(BleException exception) {
                    Log.i( "ckw","notify BleException : " + exception );
                }

                /*@Override
                public void onCharacteristicChanged(byte[] data) {
                    String str = new String(data, Charset.forName("UTF-8"));
                    String str1;
                    long result;
                    String num = "";
                    if(str.contains("@")) {
                        int index = str.indexOf("@");
                        str1 = str.substring(0, index);
                        num = str.substring(index+1);
                    } else {
                        str1 = str;
                    }
                    Log.i( "ckw","result data :"+str1);
                    long m_long = Long.parseLong(str1);
                    m_long += 1575216556166L;

                    long cur_t = System.currentTimeMillis();
                    result = cur_t - m_long;
                    Log.i("ckw", "result time :"+ new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(m_long) );
                    Log.i("ckw", "elapsed time :"+result);
                    Log.i("ckw", "num :"+ num );
                    TestActivity.Alram(getContext, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(m_long), result, num);
                }*/
                public void onCharacteristicChanged(byte[] data) {
                    String str = new String(data, Charset.forName("UTF-8"));
                    Log.i("@ckw", "barcode: "+str);
                    if(!str.equals("resp"))
//                        JoystickActivity.showPicture(activity.getSupportFragmentManager(), str);
                        JoystickActivity.addNaviCount(str);

                }
            });
    }
}
