package com.example.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
    
    private int actualRadius;
    private TextView textView;

    // this is insane, I have tryed that but there were exceptions
    RadiusDialog(int radius){
        this.actualRadius = radius;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.radius_dialog, null);

        textView = view.findViewById(R.id.text_view);
        SeekBar seekBar = view.findViewById(R.id.seek);

        seekBar.setMax(50000-actualRadius);
        if(actualRadius >= 1000){
            textView.setText(""+ (int) Math.ceil(actualRadius/1000.0)+" km");
        }
        else{
            textView.setText(""+ actualRadius+" m");
        }

        builder.setView(view)
                .setTitle("Update Radius")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // NO
                    }
                })
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // OK
                    }
                });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newRadius = progress + actualRadius;
                if(newRadius >= 1000){
                    textView.setText(""+ (int) Math.ceil(newRadius/1000.0)+" km");
                }
                else{
                    textView.setText(""+ newRadius+" m");
                }
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

    public interface RadiusDialogListener {
        void applyRadius(int radius);
    }

}