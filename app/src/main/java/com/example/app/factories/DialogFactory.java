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
     * Method to show an over query alert dialog
     * @param context of the activity where it is showed
     */
    public static void showOverQueryAlertDialog(final Context context){
        new AlertDialog.Builder(context)
                .setTitle("Sorry")
                .setMessage("It seems there have been too many requests on our service, try later in a bit.")
                .setPositiveButton(context.getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(IntentFactory.createLobbyReturn(context));
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

    /**
     * Method to show a no connection alert dialog
     * @param context of the activity where it is showed
     */
    public static void showNoConnectionAlertDialog(final Context context){
        new AlertDialog.Builder(context)
                .setTitle("OPS")
                .setMessage("Your device isn't connected to any internet provider.")
                .setPositiveButton(context.getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(IntentFactory.createLobbyReturn(context));
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

    /**
     * Method to show an unknown error alert dialog
     * @param context of the activity where it is showed
     */
    public static void showUnknownErrorAlertDialog(final Context context){
        new AlertDialog.Builder(context)
                .setTitle("OH NO")
                .setMessage("Something really strange happened. Try again please")
                .setPositiveButton(context.getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(IntentFactory.createLobbyReturn(context));
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

    /**
     * Method to show a request denied alert dialog
     * @param context of the activity where it is showed
     */
    public static void showRequestDeniedAlertDialog(final Context context){
        new AlertDialog.Builder(context)
                .setTitle("OUR FAULT")
                .setMessage("Be patient. Close, reopen the app and try again")
                .setPositiveButton(context.getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(IntentFactory.createLobbyReturn(context));
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

    /**
     * Method to show a not found error alert dialog
     * @param context of the activity where it is showed
     */
    public static void showNotFoundAlertDialog(final Context context){
        new AlertDialog.Builder(context)
                .setTitle("OH NO")
                .setMessage("You won, we didn't find you. Retry and see who will win")
                .setPositiveButton(context.getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(IntentFactory.createLobbyReturn(context));
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
