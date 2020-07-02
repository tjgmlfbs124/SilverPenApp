package com.example.codinggameapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.codinggameapp.Dialog.RequestProgressDialog;
import com.example.codinggameapp.Http.HttpGetRequestClass;
import com.example.codinggameapp.Utils.DataManager;
import com.example.codinggameapp.Utils.RadarChartClass;
import com.github.mikephil.charting.charts.RadarChart;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class UserMissionActivity extends AppCompatActivity {
    private ListView missionListView;
    private MissionLogListAdapter missionLogListAdapter;
    private AppCompatImageView btn_home;
    private RequestProgressDialog reqDialog;
    private RadarChart radarChart;
    private LineChartView chartTop;
    private LineChartData lineData;
    private RadarChartClass mStepChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mission);
        reqDialog = new RequestProgressDialog(UserMissionActivity.this);
        btn_home = findViewById(R.id.btn_home);
        if(DataManager.LoginStatus(getApplicationContext())) {
            new requestGetUserMissionInfo().execute("http://" + DataManager.connectURL + "/getUserMission");
            new requestGetUserActionInfo().execute("http://" + DataManager.connectURL + "/getUserAction");
        }
        missionListView = findViewById(R.id.listview_mission);
        missionListView.setOnTouchListener(new ListViewClickListener());
        missionLogListAdapter = new MissionLogListAdapter();
        missionListView.setAdapter(missionLogListAdapter);

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserMissionActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // *** TOP LINE CHART ***
        chartTop = (LineChartView) findViewById(R.id.chart_bottom);

        radarChart = findViewById(R.id.chart_userContents);
        mStepChart = new RadarChartClass(radarChart, getApplicationContext());
        mStepChart.chartInit();

        // Generate and set data for line chart

    }

    public class requestGetUserMissionInfo extends AsyncTask<String, String, String> {
        String userID, userName;
        @Override
        protected void onPreExecute() {
            String userInfo = DataManager.getSharedPreferences_UserInfo(getApplicationContext());
            try{
                JSONObject userJson = new JSONObject(userInfo);
                this.userID = userJson.getString("user_id");
                this.userName = userJson.getString("user_name");

            }catch (Exception e){
                Log.i("seo","requestGetUserMissionInfo JSON error : " + e);
            }
            reqDialog.run();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String result;
            ContentValues value = new ContentValues(); // key와 value을 SET으로 저장하는 객체.
            value.put("user_id", this.userID);
            value.put("user_name", this.userName);
            String url = "http://" + DataManager.connectURL + "/getUserMission";

            HttpGetRequestClass requestHttpURLConnection = new HttpGetRequestClass();
            result = requestHttpURLConnection.request(url, value); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }
        //doInBackground메소드가 끝나면 여기로 와서 텍스트뷰의 값을 바꿔준다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            reqDialog.close();
            Log.i("seo","result : " + result);
            generateMissionDataList(result);
        }
    }
    public class requestGetUserActionInfo extends AsyncTask<String, String, String> {
        String userID, userName;
        @Override
        protected void onPreExecute() {
            String userInfo = DataManager.getSharedPreferences_UserInfo(getApplicationContext());
            try{
                JSONObject userJson = new JSONObject(userInfo);
                this.userID = userJson.getString("user_id");
                this.userName = userJson.getString("user_name");

            }catch (Exception e){
                Log.i("seo","requestGetUserMissionInfo JSON error : " + e);
            }
            reqDialog.run();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String result;
            ContentValues value = new ContentValues(); // key와 value을 SET으로 저장하는 객체.
            value.put("user_id", this.userID);
            value.put("user_name", this.userName);
            String url = "http://" + DataManager.connectURL + "/getUserAction";

            HttpGetRequestClass requestHttpURLConnection = new HttpGetRequestClass();
            result = requestHttpURLConnection.request(url, value); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }
        //doInBackground메소드가 끝나면 여기로 와서 텍스트뷰의 값을 바꿔준다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            Log.i("seo","result : " + result);

            mStepChart.setContentsData(result);
            generateGraphData(result);
            resetViewport();
        }
    }
    private void generateMissionDataList(String resultData){
        Log.i("seo","resultData : " + resultData);
        try{
            JSONArray jsonArray = new JSONArray(resultData);
            for(int index = 0; index < jsonArray.length(); index ++){
                JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(index).toString());

                String category = jsonObject.getString("ms_category").length() == 1 ? "0" + jsonObject.getString("ms_category") : jsonObject.getString("ms_category");
                String mission = jsonObject.getString("ms_list").length() == 1 ? "0" + jsonObject.getString("ms_list") : jsonObject.getString("ms_list");

                JSONObject jsonObject1 = new JSONObject(getJsonString());
                JSONObject config = new JSONObject(jsonObject1.getString(category+mission));
                missionLogListAdapter.addItem((index+1) + "",config.getString("title"), jsonObject.getInt("complete"));
            }
            int value = (int)(((float)jsonArray.length() / 28) * 100);
            missionLogListAdapter.notifyDataSetChanged();
        }catch (Exception e){

        }
    }
    private class ListViewClickListener implements AdapterView.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            missionListView.requestDisallowInterceptTouchEvent(true); // 리스트뷰에 터치가 됬을때, 포커싱을 요청하는 메소드.
            return false;
        }
    }
    /**
     * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
     * will select value on column chart.
     */
    private void generateGraphData(String result) {
        JSONArray jsonArray = null;
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();
        int numValues;
        try{
            jsonArray = new JSONArray(result);
            numValues = jsonArray.length();

            for (int i = 0; i < numValues; ++i) {
                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                float value = Float.parseFloat(jsonObject.getString("count"));

                String dateString = jsonObject.getString("group_date");
                String getXLable = dateString.substring(dateString.length()-2, dateString.length()) + "일";
                Log.i("seo","getXLable : " + getXLable);

                values.add(new PointValue(i, value));
                axisValues.add(new AxisValue(i).setLabel(getXLable));
            }

        }catch (Exception e){
            Log.i("seo","jsonArray Error : " + e);
        }

        Line line = new Line(values);
        line.setHasLabels(true);
        line.setColor(Color.parseColor("#FFD814")).setCubic(true);

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

        chartTop.setLineChartData(lineData);

        // For build-up animation you have to disable viewport recalculation.
        chartTop.setViewportCalculationEnabled(false);

        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(0, 110, 6, 0);
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewport(v);

        chartTop.setZoomType(ZoomType.HORIZONTAL);
    }

    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chartTop.getMaximumViewport());
        v.bottom = 0;
        v.top = 80;
        v.left = 0;
        v.right = 7;
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewport(v);
    }

    private String getJsonString(){
        String json = "";
        try {
            InputStream is = getAssets().open("missionConfig.json");
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
