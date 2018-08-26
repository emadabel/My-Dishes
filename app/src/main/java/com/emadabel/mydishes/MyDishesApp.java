package com.emadabel.mydishes;

import android.app.Application;

import com.emadabel.mydishes.utilities.ReleaseTree;

import timber.log.Timber;

public class MyDishesApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(BuildConfig.DEBUG ? new Timber.DebugTree() : new ReleaseTree());
    }
}
