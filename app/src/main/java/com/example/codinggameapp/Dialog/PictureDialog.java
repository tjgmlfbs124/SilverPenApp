package com.example.codinggameapp.Dialog;

import android.app.AlertDialog;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codinggameapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

public class PictureDialog extends BottomSheetDialogFragment{
    private AppCompatImageView img_barcode, img_family;
    private TextView txt_barcode;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_picture, container, false);

        Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        vibrator.vibrate(200);

        img_barcode = view.findViewById(R.id.img_barcode);
        img_family = view.findViewById(R.id.img_family);
        txt_barcode = view.findViewById(R.id.txt_barcode);

        img_family.setImageResource(R.drawable.img_family);

        String barcode = getArguments().getString("barcode");
        int imageID = getResources().getIdentifier("barcode"+ barcode,"drawable",getActivity().getPackageName());

        img_barcode.setImageResource(imageID);
        txt_barcode.setText(barcode + "번 바코드");

        Log.i("seo","barcode : " + barcode);
        return view;
    }
}
