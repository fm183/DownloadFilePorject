package com.file.downloadfile;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;


public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }
}
