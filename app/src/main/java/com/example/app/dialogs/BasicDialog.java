package com.example.app.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.app.R;

/**
 * BasicDialog class for generic Dialog
 */
public class BasicDialog extends AppCompatDialogFragment {

    // Object params
    private String id;
    private String title;
    private String text;
    private String textForOkButton;
    private String textForCancelButton;
    private BasicDialogListener listener;

    /**
     * Callback to get the basic dialog instance
     * @param savedInstanceState The Bundle of any previous basic dialog if any
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.basic_dialog, null);
        // Getting and setting widgets
        TextView textView = view.findViewById(R.id.text_view);
        textView.setText(this.text);
        // Build the Dialog
        builder.setView(view)
                .setTitle(this.title)
                .setNegativeButton(this.textForCancelButton, (dialog, i) -> {
                    listener.onDialogResult(id, false);
                    dialog.dismiss();
                })
                .setPositiveButton(this.textForOkButton, (dialog, i) -> {
                    listener.onDialogResult(id, true);
                    dialog.dismiss();
                });

        return builder.create();
    }

    /**
     * Callback onAttach to create the listener
     * that will be called on dialog result.
     * Result will be passed to the activity that called
     * the dialog
     * @param context             The activity/fragment Context
     * @throws ClassCastException If the listener is not implemented in any Context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (BasicDialogListener) context;
        }
        catch (ClassCastException e) {
            // This should be managed in some LOG file of the app
            throw new ClassCastException(context.toString() + "must implement BasicDialogListener");
        }
    }

    /**
     * The listener public interface
     */
    public interface BasicDialogListener {
        void onDialogResult(String id, boolean option);
    }

    /**
     * Builder for BasicDialog
     */
    public static class BasicDialogBuilder {

        // Builder params
        private String id;
        private String title;
        private String text;
        private String textForOkButton;
        private String textForCancelButton;

        /**
         * Builder constructor
         * @param id Identification String of the dialog
         */
        public BasicDialogBuilder(String id){
            this.id = id;
        }

        /**
         * @param title String for title to set
         */
        public void setTitle(String title){
            this.title = title;
        }

        /**
         * @param text String for text to set
         */
        public void setText(String text){
            this.text = text;
        }

        /**
         * @param textForOkButton String for ok button to set
         */
        public void setTextForOkButton(String textForOkButton){
            this.textForOkButton = textForOkButton;
        }

        /**
         * @param textForCancelButton String for cancel button to set
         */
        public void setTextForCancelButton(String textForCancelButton){
            this.textForCancelButton = textForCancelButton;
        }

        /**
         * @return A basic Dialog instance built with the parameters set
         */
        public BasicDialog build(){
            BasicDialog basicDialog = new BasicDialog();
            basicDialog.id    = this.id;
            basicDialog.title = this.title;
            basicDialog.text  = this.text;
            basicDialog.textForOkButton     = this.textForOkButton;
            basicDialog.textForCancelButton = this.textForCancelButton;
            return basicDialog;
        }
    }

}

