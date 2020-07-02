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

public class MissionFragment02 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mission02_fragment,null);

        ScalableLayout mission0207 = (ScalableLayout) view.findViewById(R.id.mission_0207);
        ScalableLayout mission0206 = (ScalableLayout) view.findViewById(R.id.mission_0206);
        ScalableLayout mission0205 = (ScalableLayout) view.findViewById(R.id.mission_0205);
        ScalableLayout mission0204 = (ScalableLayout) view.findViewById(R.id.mission_0204);
        ScalableLayout mission0203 = (ScalableLayout) view.findViewById(R.id.mission_0203);
        ScalableLayout mission0202 = (ScalableLayout) view.findViewById(R.id.mission_0202);
        ScalableLayout mission0201 = (ScalableLayout) view.findViewById(R.id.mission_0201);

        mission0201.setOnClickListener(new LayoutClickListener());
        mission0202.setOnClickListener(new LayoutClickListener());
        mission0203.setOnClickListener(new LayoutClickListener());
        mission0204.setOnClickListener(new LayoutClickListener());
        mission0205.setOnClickListener(new LayoutClickListener());
        mission0206.setOnClickListener(new LayoutClickListener());
        mission0207.setOnClickListener(new LayoutClickListener());

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
