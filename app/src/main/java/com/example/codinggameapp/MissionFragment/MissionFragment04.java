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

public class MissionFragment04 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mission04_fragment,null);
        ScalableLayout mission0407 = (ScalableLayout) view.findViewById(R.id.mission_0407);
        ScalableLayout mission0406 = (ScalableLayout) view.findViewById(R.id.mission_0406);
        ScalableLayout mission0405 = (ScalableLayout) view.findViewById(R.id.mission_0405);
        ScalableLayout mission0404 = (ScalableLayout) view.findViewById(R.id.mission_0404);
        ScalableLayout mission0403 = (ScalableLayout) view.findViewById(R.id.mission_0403);
        ScalableLayout mission0402 = (ScalableLayout) view.findViewById(R.id.mission_0402);
        ScalableLayout mission0401 = (ScalableLayout) view.findViewById(R.id.mission_0401);

        mission0407.setOnClickListener(new LayoutClickListener());
        mission0406.setOnClickListener(new LayoutClickListener());
        mission0405.setOnClickListener(new LayoutClickListener());
        mission0404.setOnClickListener(new LayoutClickListener());
        mission0403.setOnClickListener(new LayoutClickListener());
        mission0402.setOnClickListener(new LayoutClickListener());
        mission0401.setOnClickListener(new LayoutClickListener());

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
