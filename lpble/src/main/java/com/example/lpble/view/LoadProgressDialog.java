package com.example.lpble.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.lpble.R;

public class LoadProgressDialog extends Dialog {
    private String message;
    private boolean canCancel;
    private TextView textView;

    public LoadProgressDialog(Context context, String message) {
        this(context, message, false);
    }

    public LoadProgressDialog(Context context, String message, boolean canCancel) {
        super(context, R.style.LoadProgressDialog);
        this.message = message;
        this.canCancel = canCancel;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loadprogress);
        textView = findViewById(R.id.tv_message);
        setCancelable(canCancel);
        setCanceledOnTouchOutside(canCancel);
        textView.setText(message);
    }
}
