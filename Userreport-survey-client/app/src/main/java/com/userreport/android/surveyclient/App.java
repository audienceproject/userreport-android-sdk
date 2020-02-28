package com.userreport.android.surveyclient;

import android.app.Application;

import com.audienceproject.userreport.UserReport;
import com.audienceproject.userreport.UserReportCore;
import com.audienceproject.userreport.models.Settings;

public class App extends Application {
    static App instance;
    private UserReport userReport;

    public static synchronized App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) instance = this;
        initUserReport();
    }

    private void initUserReport() {
        // Optional
        Settings settings = new Settings();
        settings.setSessionNSecondsLength(7);
        settings.setSessionScreensView(3);
        settings.setLocalQuarantineDays(10);
        UserReportCore core = UserReportCore.newBuilder("audienceproject", "8aa7a61b-5c16-40c4-9b9e-c5ba641a160b")
                .setSettings(settings)
                .build();
        userReport = UserReport.with(this, core);
    }

    public UserReport getUserReport() {
        return userReport;
    }
}
