package com.example.codinggameapp.Utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.example.codinggameapp.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RadarChartClass {
    public RadarChart mChart;
    public Context mContext;

    public RadarChartClass(RadarChart chart, Context context){
        mChart = chart;
        mContext = context;
    }

    public void chartInit(){
        mChart.setBackgroundColor(Color.parseColor("#fafafa"));

        mChart.getDescription().setEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.parseColor("#303030"));
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.parseColor("#303030"));
        mChart.setWebAlpha(100);

        MarkerView mv = new RadarMarkerView(mContext, R.layout.radar_markerview);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

//        setData();

        mChart.animateXY(1400, 1400, Easing.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new ValueFormatter() {

            private final String[] mActivities = new String[]{"반복적사고", "로봇조종", "순차적 사고", "접속", "논리적 사고", "자유코딩"};

            @Override
            public String getFormattedValue(float value) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.parseColor("#303030"));

        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(5000f);
        yAxis.setDrawLabels(false);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setTextColor(Color.parseColor("#303030"));

        mChart.getLegend().setEnabled(false);   // Hide the legend

    }

    public void setContentsData(String result){
        ArrayList<RadarEntry> entries1 = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject object = new JSONObject(jsonArray.get(i).toString());
                entries1.add(new RadarEntry( Float.parseFloat(object.getString("count"))));
            }
        }catch (Exception e){
            Log.i("seo","e : " + e);
        }

        RadarDataSet set1 = new RadarDataSet(entries1, "활동 내역");
        set1.setColor(Color.rgb(103, 110, 129));
        set1.setFillColor(Color.parseColor("#FFD814"));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.invalidate();
    }
}
