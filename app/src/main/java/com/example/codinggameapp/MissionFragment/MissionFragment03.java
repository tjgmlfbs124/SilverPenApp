package com.example.codinggameapp.MissionFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.codinggameapp.CodingActivity;
import com.example.codinggameapp.R;
import com.example.codinggameapp.Utils.ScalableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MissionFragment03 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mission03_fragment,null);
        ScalableLayout mission0307 = (ScalableLayout) view.findViewById(R.id.mission_0307);
        ScalableLayout mission0306 = (ScalableLayout) view.findViewById(R.id.mission_0306);
        ScalableLayout mission0305 = (ScalableLayout) view.findViewById(R.id.mission_0305);
        ScalableLayout mission0304 = (ScalableLayout) view.findViewById(R.id.mission_0304);
        ScalableLayout mission0303 = (ScalableLayout) view.findViewById(R.id.mission_0303);
        ScalableLayout mission0302 = (ScalableLayout) view.findViewById(R.id.mission_0302);
        ScalableLayout mission0301 = (ScalableLayout) view.findViewById(R.id.mission_0301);

        mission0307.setOnClickListener(new LayoutClickListener());
        mission0306.setOnClickListener(new LayoutClickListener());
        mission0305.setOnClickListener(new LayoutClickListener());
        mission0304.setOnClickListener(new LayoutClickListener());
        mission0303.setOnClickListener(new LayoutClickListener());
        mission0302.setOnClickListener(new LayoutClickListener());
        mission0301.setOnClickListener(new LayoutClickListener());

        return view;
    }
    private class LayoutClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), CodingActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putString("category","default/mission_toolbox/mission_toolbox_" + v.getTag().toString() + ".xml");
            mBundle.putString("mission", v.getTag().toString());
            intent.putExtras(mBundle);
            startActivity(intent);
        }
    }
}
