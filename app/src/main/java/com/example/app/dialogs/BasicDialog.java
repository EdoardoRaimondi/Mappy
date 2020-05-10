package com.example.app.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.app.R;

import java.util.Objects;

public class BasicDialog extends AppCompatDialogFragment {

    private String id;
    private String title;
    private String text;
    private String textForOkButton;
    private String textForCancelButton;
    private BasicDialogListener listener;

    /**
     * RadiusDialog constructor
     * @param id the identifier of this dialog
     * @param title the title of dialog
     * @param text the dialog message
     * @param textForCancelButton the label of negative button
     * @param textForOkButton the label of positive button
     */
    public BasicDialog(String id, String title, String text, String textForOkButton, String textForCancelButton){
        this.id = id;
        this.title = title;
        this.text = text;
        this.textForOkButton = textForOkButton;
        this.textForCancelButton = textForCancelButton;
    }

    /**
     * Callback to get the basic dialog instance
     * @param savedInstanceState the Bundle of any previous basic dialog if any
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.basic_dialog, null);

        TextView textView = view.findViewById(R.id.text_view);
        textView.setText(this.text);

        builder.setView(view)
                .setTitle(this.title)
                .setNegativeButton(this.textForCancelButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        listener.onDialogResult(id, false);
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(this.textForOkButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        listener.onDialogResult(id, true);
                        dialog.dismiss();
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
     * @throws ClassCastException if the listener is not implemented in activity class
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (BasicDialogListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement BasicDialogListener");
        }
    }

    /**
     * The listener public interface
     */
    public interface BasicDialogListener {
        void onDialogResult(String id, boolean option);
    }

}

