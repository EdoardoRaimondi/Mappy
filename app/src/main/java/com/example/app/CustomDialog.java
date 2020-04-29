package com.example.app;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CustomDialog extends Dialog {


    private String name;
    public static EditText etName;
    public String zip;
    OnMyDialogResult mDialogResult; // the callback

    public CustomDialog(Context context, String name) {
        super(context);
        this.name = name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // same you have
    }

    private class OKListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            if( mDialogResult != null ){
                mDialogResult.finish(String.valueOf(etName.getText()));
            }
            CustomDialog.this.dismiss();
        }
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(String result);
    }
}
