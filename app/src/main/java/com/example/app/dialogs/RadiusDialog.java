package com.example.app.dialogs;

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

import com.example.app.R;
import com.example.app.finals.MapsUtility;

/**
 * RadiusDialog class for a specific type of dialog required
 * in {@link com.example.app.MapsActivity}
 */
public class RadiusDialog extends AppCompatDialogFragment {

    // Object params
    private int actualRadius;
    private int newRadius;
    private TextView textView;
    private RadiusDialogListener listener;

    /**
     * Set the some context information of the caller activity
     * @param actualRadius The old int radius research
     */
    public RadiusDialog(int actualRadius){
        this.actualRadius = actualRadius/ MapsUtility.KM_TO_M + MapsUtility.DEFAULT_INCREMENT;
    }

    /**
     * Callback when the dialog is created
     * @param savedInstanceState For eventual instance saved data
     * @return The dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.radius_dialog, null);
        // Finding widgets
        final SeekBar seekBar = view.findViewById(R.id.seek);
        textView = view.findViewById(R.id.text_view);
        // Init them
        String display = "" + actualRadius  + " " + getResources().getString(R.string.measure_unit);
        textView.setText(display);
        seekBar.setMax(getResources().getInteger(R.integer.max_radius_bar) - actualRadius);
        // Now build the Dialog
        builder.setView(view)
                .setTitle(getString(R.string.radius_title))
                .setNegativeButton(getString(R.string.radius_cancel_button), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(getString(R.string.radius_ok_button), new DialogInterface.OnClickListener() {
                    /**
                     * Callback when ok button is pressed
                     * @param dialog The dialog
                     * @param i      The dialog universal id
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        listener.onRadiusDialogResult(newRadius * MapsUtility.KM_TO_M);
                        dialog.dismiss();
                    }
                });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * Callback when user tracks the bar
             * @param seekBar  The bar displayed
             * @param progress The int representing the position of the user touch on the bar
             * @param fromUser The boolean to check if the progress is from the user
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                newRadius = progress + actualRadius;
                String display = "" + newRadius + " " + getResources().getString(R.string.measure_unit);
                textView.setText(display);
            }

            /**
             * Callback when the user start moving the bar
             * @param seekBar The bar displayed
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            /**
             * Callback when the user stop moving the bar
             * @param seekBar The bar displayed
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
     * @param context             The activity context
     * @throws ClassCastException If the listener is not implemented in activity class
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (RadiusDialogListener) context;
        }
        catch (ClassCastException e) {
            // This should be managed in some LOG file of the app
            throw new ClassCastException(context.toString() + "must implement RadiusDialogListener");
        }
    }

    /**
     * The listener public interface
     */
    public interface RadiusDialogListener {
        void onRadiusDialogResult(int radius);
    }


}
