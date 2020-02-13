package com.audienceproject.userreport;

public interface ISettingsLoader {

    void load();

    void registerSettingsLoadCallback(ISettingsCallback callback);
}