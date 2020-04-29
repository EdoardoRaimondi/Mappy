package com.example.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

public class RadiusDialog extends AppCompatDialogFragment {

    private static final double M_TO_KM_DIVIDER = 1000.0;

    private int actualRadius;
    private TextView textView;
    private RadiusDialogListener listener;

    RadiusDialog(int radius){
        this.actualRadius = radius;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getActivity().getLayoutInflater();

        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.radius_dialog, null);

        textView = view.findViewById(R.id.text_view);
        final SeekBar seekBar = view.findViewById(R.id.seek);

        seekBar.setMax(50000 - actualRadius);
        String display;
        if(actualRadius >= M_TO_KM_DIVIDER){
            display = (int) Math.ceil(actualRadius / M_TO_KM_DIVIDER) + " km";
        }
        else{
            display = actualRadius + " m";
        }
        textView.setText(display);

        builder.setView(view)
                .setTitle(getString(R.string.radius_title))
                .setNegativeButton(getString(R.string.radius_cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(getString(R.string.radius_ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        int progress = seekBar.getProgress();
                        listener.applyRadius(progress + actualRadius);
                    }
                });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newRadius = progress + actualRadius;
                String display;
                if(newRadius >= M_TO_KM_DIVIDER){
                    display = (int) Math.ceil(newRadius / M_TO_KM_DIVIDER) + " km";
                }
                else{
                    display = newRadius + " m";
                }
                textView.setText(display);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (RadiusDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement RadiusDialogListener");
        }
    }

    public interface RadiusDialogListener {
        void applyRadius(int radius);
    }

}