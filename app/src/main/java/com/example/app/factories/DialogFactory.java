package com.example.app.factories;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.app.R;

/**
 * Class that create and show dialogs
 */
public class DialogFactory {

    /**
     * Method to show a not found error alert dialog
     * @param context of the activity where it is showed
     */
    public static void showActivateGPSAlertDialog(final Context context){
        new AlertDialog.Builder(context)
                .setTitle("Hey")
                .setMessage("What did you expect, this is a navigator app. Turn GPS on?")
                .setPositiveButton(context.getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.cancel();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

}
