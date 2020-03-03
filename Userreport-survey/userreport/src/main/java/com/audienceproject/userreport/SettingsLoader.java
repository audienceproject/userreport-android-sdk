package com.audienceproject.userreport;

public interface SettingsLoader {

    void load();

    void registerSettingsLoadCallback(SettingsLoadingCallback callback);
}