package com.example.codinggameapp;

import android.graphics.drawable.Drawable;

/**
 * Created by USER on 2018-07-23.
 */

public class MissionLogListItem {
    private int mIconDrawable ;
    private String titleStr, dateStr ;
    private int graphLength;

    public void setLevel(int iconDrawable) {
        mIconDrawable = iconDrawable ;
    }
    public void setNumber(String title) {
        titleStr = title ;
    }
    public void setMissionName(String date) {
        dateStr = date ;
    }

    public int getLevel() {
        return this.mIconDrawable ;
    }
    public String getNumber() {
        return this.titleStr ;
    }
    public String getMissionName() {
        return this.dateStr ;
    }
}
