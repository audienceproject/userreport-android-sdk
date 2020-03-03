package com.audienceproject.userreport;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

// Utils class to simplify work with shared preferences.
public class SharedPreferencesWrapper {
    private final String FILE_NAME = "AudienceProject_storage";
    private final SharedPreferences settings;

    public SharedPreferencesWrapper(Context context) {
        Application application = ((Application) context.getApplicationContext());
        this.settings = application.getSharedPreferences(FILE_NAME, 0);
    }

    public long readLong(String key){
        return this.settings.getLong(key, 0);
    }

    public void writeLong(String key, long value){
        this.settings.edit().putLong(key, value).apply();
    }

    public boolean readBool(String key){
        return this.settings.getBoolean(key, false);
    }

    public void writeBool(String key, boolean value){
        this.settings.edit().putBoolean(key, value).apply();
    }

    public String readString(String key){
        return this.settings.getString(key, "");
    }

    public void writeString(String key, String value){
        this.settings.edit().putString(key, value).apply();
    }
}
