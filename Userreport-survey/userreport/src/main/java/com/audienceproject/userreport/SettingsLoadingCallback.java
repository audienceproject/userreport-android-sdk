package com.audienceproject.userreport;

import com.audienceproject.userreport.models.MediaSettings;

public interface SettingsLoadingCallback {
    void onSuccess(MediaSettings settings);

    void onFailed(Exception ex);
}
