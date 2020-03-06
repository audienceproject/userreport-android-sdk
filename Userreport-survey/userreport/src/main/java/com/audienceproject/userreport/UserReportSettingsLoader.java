package com.audienceproject.userreport;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.audienceproject.userreport.interfaces.SurveyLogger;
import com.audienceproject.userreport.models.MediaSettings;
import com.audienceproject.userreport.models.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;

/**
 * Loads android.json with settings from server
 */
class UserReportSettingsLoader implements SettingsLoader {

    private static final String SETTINGS_FILE_NAME = "android.json";
    private static final String STORED_SETTINGS_KEY = "previouslyLoadedSettings";
    private static final String STORED_SETTINGS_DATE_KEY = "previouslyLoadedSettingsDate";
    private static final long SETTINGS_TTL = 3600;
    private static final String FILE_NAME = "AudienceProject_storage";
    private static SharedPreferences preferences;

    private final RequestQueue queue;
    private Application application;
    private long tag;
    private MediaSettings mediaSettings;
    private String settingsBaseUrl;

    private ArrayList<SettingsLoadingCallback> callbacks;
    private String sakId;
    private String mediaId;
    private SurveyLogger logger;
    private Settings userSettings;

    UserReportSettingsLoader(Context context, String settingsBaseUrl, String sakId, String mediaId,
                             SurveyLogger logger, Settings settings) {
        this.application = (Application) context.getApplicationContext();
        this.settingsBaseUrl = settingsBaseUrl;
        this.sakId = sakId;
        this.mediaId = mediaId;
        this.logger = logger;
        this.userSettings = settings;

        this.tag = new Date().getTime();
        preferences = application.getSharedPreferences(FILE_NAME, 0);
        this.callbacks = new ArrayList<>();
        this.queue = Volley.newRequestQueue(context);
    }

    public void registerSettingsLoadCallback(SettingsLoadingCallback callback) {
        callbacks.add(callback);
        if (mediaSettings != null) {
            callback.onSuccess(mediaSettings);
        }
    }

    @Override
    public void setLogger(SurveyLogger logger) {
        this.logger = logger;
    }

    @Override
    public void load() {
        MediaSettings stored = loadSettingsFromLocals();
        if (stored != null) {
            raiseSettingsLoaded(stored);
        } else {
            loadSAKSettings();
        }
    }

    private void raiseSettingsLoaded(final MediaSettings sakSettings) {
        final MediaSettings resultSettings = new MediaSettings();

        resultSettings.setLocalQuarantineDays(userSettings != null
                && userSettings.getLocalQuarantineDays() != null
                ? userSettings.getLocalQuarantineDays()
                : sakSettings.getLocalQuarantineDays() != null
                ? sakSettings.getLocalQuarantineDays()
                : Settings.getDefaultSettings().getLocalQuarantineDays());

        resultSettings.setInviteAfterNSecondsInApp(userSettings != null
                && userSettings.getInviteAfterNSecondsInApp() != null
                ? userSettings.getInviteAfterNSecondsInApp()
                : sakSettings.getInviteAfterNSecondsInApp() != null
                ? sakSettings.getInviteAfterNSecondsInApp()
                : Settings.getDefaultSettings().getInviteAfterNSecondsInApp());

        resultSettings.setInviteAfterTotalScreensViewed(userSettings != null
                && userSettings.getInviteAfterTotalScreensViewed() != null
                ? userSettings.getInviteAfterTotalScreensViewed()
                : sakSettings.getInviteAfterTotalScreensViewed() != null
                ? sakSettings.getInviteAfterTotalScreensViewed()
                : Settings.getDefaultSettings().getInviteAfterTotalScreensViewed());

        resultSettings.setSessionScreensView(userSettings != null
                && userSettings.getSessionScreensView() != null
                ? userSettings.getSessionScreensView()
                : sakSettings.getSessionScreensView() != null
                ? sakSettings.getSessionScreensView()
                : Settings.getDefaultSettings().getSessionScreensView());

        resultSettings.setSessionNSecondsLength(userSettings != null
                && userSettings.getSessionNSecondsLength() != null
                ? userSettings.getSessionNSecondsLength()
                : sakSettings.getSessionNSecondsLength() != null
                ? sakSettings.getSessionNSecondsLength()
                : Settings.getDefaultSettings().getSessionNSecondsLength());

        resultSettings.setCompanyId(sakSettings.getCompanyId());
        resultSettings.setKitTcode(sakSettings.getKitTcode());
        resultSettings.setToolBarColor(sakSettings.getToolBarColor());
        resultSettings.setSections(sakSettings.getSections());

        mediaSettings = resultSettings;
        for (SettingsLoadingCallback callback : callbacks) {
            callback.onSuccess(mediaSettings);
        }
    }

    private void raiseSettingsFailed(Exception ex) {
        for (SettingsLoadingCallback callback : callbacks) {
            callback.onFailed(ex);
        }
    }

    private MediaSettings loadSettingsFromLocals() {
        long storedAt = preferences.getLong(STORED_SETTINGS_DATE_KEY, 0);

        if (storedAt > 0) {
            long currTime = new Date().getTime();
            long secsPassed = (currTime - storedAt) / 1000;
            if (secsPassed < SETTINGS_TTL) {
                String data = preferences.getString(STORED_SETTINGS_KEY, "");
                return parseData(data);
            }
        }

        return null;
    }

    private void loadSAKSettings() {
        final String url = generateUrl();
        StringRequest request = new StringRequest(url,
                response -> {
                    MediaSettings settings = parseData(response);
                    storeSettingsLocally(response);
                    raiseSettingsLoaded(settings);
                },
                error -> {
                    logger.error("Request to " + url + " failed", error);
                    raiseSettingsFailed(error);
                }
        );

        request.setTag(tag);
        queue.add(request);
    }

    private void storeSettingsLocally(String settings) {
        SharedPreferences.Editor edit = UserReportSettingsLoader.preferences.edit();
        edit.putString(STORED_SETTINGS_KEY, settings);
        edit.putLong(STORED_SETTINGS_DATE_KEY, new Date().getTime());
        edit.commit();
    }

    private MediaSettings parseData(String data) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(data, MediaSettings.class);
    }

    private String generateUrl() {
        return settingsBaseUrl + sakId + "/media/" + mediaId + "/" + SETTINGS_FILE_NAME;
    }
}