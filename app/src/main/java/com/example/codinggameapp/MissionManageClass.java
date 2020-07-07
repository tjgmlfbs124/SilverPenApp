package com.example.codinggameapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.liquidplayer.webkit.javascriptcore.JSValue;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MissionManageClass {
    public static String mission = null;
    public static int[][] fieldMetrix;

    public static int objectX;
    public static int objectY;
    public static int completeScore;
    public static ArrayList<String> runBlock;
    public static Context mContext;
    public static String getCompleteJsonFile;

    public MissionManageClass(Context context, String mode){ // Constructor
        fieldMetrix = new int[100][100];
        mContext = context;
        initObject(mode);
        Log.i("seo","[1] mode : " + mode);
    }
    static void initObject(String mode){
        objectX = 50;
        objectY = 50;
        runBlock = new ArrayList<String>();
        getCompleteJsonFile = getJsonString(mContext, mode);
        Log.i("seo","[2] mode : " + mode);
        completeScore = 0;
    }

    static int completeCheck(){
        if(mission == null || mission.equals("")) return 0;
        else{
            try{
                JSONObject object = new JSONObject(getCompleteJsonFile);

                String completeJSON = object.getString(mission);
                JSONObject completeObject = new JSONObject(completeJSON);

                int complete_objectX = completeObject.getInt("x");
                int complete_objectY = completeObject.getInt("y");
                int complete_minBlock = completeObject.getInt("minBlock");
                JSONArray complete_EssentialBlock;
                complete_EssentialBlock = completeObject.getJSONArray("EssentialBlock");
                Log.i("seo","complete_objectX : " + complete_objectX);
                Log.i("seo","complete_objectY : " + complete_objectY);
                Log.i("seo","objectX : " + objectX);
                Log.i("seo","objectX : " + objectY);
                if(checkCompleteObjectLocation(complete_objectX, complete_objectY)){
                    completeScore++;
                    if(checkCompleteBlockList(complete_EssentialBlock)) {
                        completeScore++;
                        if(checkCompleteBlockIndex(complete_minBlock))
                            completeScore++;
                    }
                }
                else{
                    Log.i("seo","Not matching!! ");
                    return 0;
                }
            }catch (Exception e){
                Log.i("seo","get Complete Json error e : " + e);
            }
            return completeScore;
        }
    }

    private static boolean findCompleteBlock(JSONArray jsonArray, String block){
        Log.i("seo","jsonArray.toString() : " + jsonArray.toString());
        return jsonArray.toString().contains(block);
    }

    private static boolean checkCompleteObjectLocation(int completeX, int completeY){
        if(objectX == completeX &&  objectY== completeY) return true;
        else return false;
    }

    private static boolean checkCompleteBlockList(JSONArray completeBlockList){
        boolean isMatch = false;
        for(int index =0; index < runBlock.size(); index++){
            isMatch = isMatch | findCompleteBlock(completeBlockList, runBlock.get(index));
        }
        return isMatch;
    }

    private static boolean checkCompleteBlockIndex(int minBlockIndex){
        Log.i("seo","runBlock.size() : " + runBlock.size());
        if(runBlock.size() <= minBlockIndex) return true;
        else return false;
    }


    void runBuffer(JSValue buffer){
        try{
            JSONObject object = new JSONObject(buffer.toJSON());
            Log.i("seo","object  : " + object);
            int moveX = Integer.parseInt(object.getString("x"));
            int moveY = Integer.parseInt(object.getString("y"));
            String protocol = object.getString("protocol");
            Log.i("seo","protocol  : " + protocol);
            objectX = objectX + moveX;
            objectY = objectY + moveY;
            runBlock.add(object.getString("blockname"));
            BluetoothSendManager.sendProtocol(protocol);

            Log.i("seo","objectX : " + objectX + " objectY : " + objectY);
        }catch (Exception e){
            Log.i("seo","runBuffer error : " + e);
        }
    }

    private static String getJsonString(Context context, String mode){
        Log.i("seo","[3] mode : " + mode);
        if(mode == null) mode = "";
        String json = "";
        try {

            InputStream is;
            if(mode.equals("barcode"))
                is = context.getAssets().open("barcode_MissionComplete.json");
            else
                is = context.getAssets().open("complete.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        return json;
    }

}
