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
    private int newRadius;
    private TextView textView;
    private NearbyRequestType requestType;

    /**
     * Set the some context information of the caller activity
     * @param radius       old radius research
     * @param requestType  research request type
     */
    RadiusDialog(int radius, NearbyRequestType requestType){
        actualRadius = radius;
        this.requestType = requestType;
    }

    /**
     * Callback when the dialog is created
     * @param savedInstanceState for eventual saved data
     * @return The dialog
     */
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
                    /**
                     * Callback when Ok button is pressed
                     * @param dialogInterface the dialog
                     * @param i (?)
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(IntentFactory.createNearbyRequestIntent(getContext(), requestType, newRadius));
                    }
                });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * Callback when user tracks the bar
             * @param seekBar  the bar displayed
             * @param progress int representing the position of the user touch on the bar
             * @param fromUser boolean to check if the progress is from the user
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                newRadius = progress + actualRadius;
                String display;
                if(newRadius >= M_TO_KM_DIVIDER){
                    display = (int) Math.ceil(newRadius / M_TO_KM_DIVIDER) + " km";
                }
                else{
                    display = newRadius + " m";
                }
                textView.setText(display);
            }

            /**
             * Callback when the user start tracking the bar
             * @param seekBar the bar displayed
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            /**
             * Callback when the user stop tracking the bar
             * @param seekBar the bar displayed
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        return builder.create();
    }

    /**
    * Callback onAttach to create the listener
    * that will be called on dialog result.
    * Result will be passed to the activity that called
    * the dialog
    * @param context the activity context
    * @throws ClassCastException if the listener is not
    * implemented in activity class
    */

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * The listener public interface
     */
    public interface RadiusDialogListener {
        void applyRadius(int radius);
    }

}
