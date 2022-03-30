package com.brouken.player;

import android.app.Application;

import io.paperdb.Paper;

public class MApplication  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
    }
}
