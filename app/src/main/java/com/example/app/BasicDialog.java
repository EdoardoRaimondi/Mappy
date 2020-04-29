package com.example.app;

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


import java.util.Objects;

public class BasicDialog extends AppCompatDialogFragment {

    private String id;
    private String title;
    private String text;
    private String textForOkButton;
    private String textForCancelButton;
    private BasicDialogListener listener;

    BasicDialog(String id, String title, String text, String textForOkButton, String textForCancelButton){
        this.id = id;
        this.title = title;
        this.text = text;
        this.textForOkButton = textForOkButton;
        this.textForCancelButton = textForCancelButton;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.basic_dialog, null);

        TextView textView = view.findViewById(R.id.text_view);
        textView.setText(this.text);

        builder.setView(view)
                .setTitle(this.title)
                .setNegativeButton(this.textForCancelButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        listener.onDialogResult(id, false);
                    }
                })
                .setPositiveButton(this.textForOkButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        listener.onDialogResult(id, true);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (BasicDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement BasicDialogListener");
        }
    }

    public interface BasicDialogListener {
        void onDialogResult(String id, boolean option);
    }

}
