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

public class MissionFragment01 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mission01_fragment,null);
        ScalableLayout mission0107 = (ScalableLayout) view.findViewById(R.id.mission_0107);
        ScalableLayout mission0106 = (ScalableLayout) view.findViewById(R.id.mission_0106);
        ScalableLayout mission0105 = (ScalableLayout) view.findViewById(R.id.mission_0105);
        ScalableLayout mission0104 = (ScalableLayout) view.findViewById(R.id.mission_0104);
        ScalableLayout mission0103 = (ScalableLayout) view.findViewById(R.id.mission_0103);
        ScalableLayout mission0102 = (ScalableLayout) view.findViewById(R.id.mission_0102);
        ScalableLayout mission0101 = (ScalableLayout) view.findViewById(R.id.mission_0101);

        mission0101.setOnClickListener(new LayoutClickListener());
        mission0102.setOnClickListener(new LayoutClickListener());
        mission0103.setOnClickListener(new LayoutClickListener());
        mission0104.setOnClickListener(new LayoutClickListener());
        mission0105.setOnClickListener(new LayoutClickListener());
        mission0106.setOnClickListener(new LayoutClickListener());
        mission0107.setOnClickListener(new LayoutClickListener());

        return view;
    }
    private class LayoutClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), CodingActivity.class);
            Bundle mBundle = new Bundle();
//            mBundle.putString("category","default/mission_toolbox/mission_toolbox_0101.xml");
            mBundle.putString("category","default/mission_toolbox/mission_toolbox_" + v.getTag().toString() + ".xml");
            mBundle.putString("mission", v.getTag().toString());
            intent.putExtras(mBundle);
            startActivity(intent);
        }
    }
}
