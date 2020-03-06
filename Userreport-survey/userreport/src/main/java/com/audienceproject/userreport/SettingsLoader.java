package com.audienceproject.userreport;

import com.audienceproject.userreport.interfaces.SurveyLogger;

public interface SettingsLoader {

    void load();

    void registerSettingsLoadCallback(SettingsLoadingCallback callback);

    void setLogger(SurveyLogger logger);
}