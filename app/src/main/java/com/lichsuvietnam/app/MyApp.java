package com.lichsuvietnam.app;

import android.app.Application;
import com.lichsuvietnam.app.data.seed.DatabaseSeeder;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseSeeder.seedIfEmpty(this);
    }
}
