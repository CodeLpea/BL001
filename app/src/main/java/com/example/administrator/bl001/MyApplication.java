package com.example.administrator.bl001;

import android.app.Application;

import com.example.lpble.LpBle;

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LpBle.getInstance().init(this);
    }
}
