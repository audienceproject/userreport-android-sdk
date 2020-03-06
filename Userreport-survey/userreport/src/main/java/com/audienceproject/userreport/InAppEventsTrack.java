package com.audienceproject.userreport;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.audienceproject.userreport.interfaces.SurveyLogger;
import com.audienceproject.userreport.models.MediaSettings;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class InAppEventsTrack implements InAppEventsTracker, Application.ActivityLifecycleCallbacks {

    private String tCode;
    private String bundleId;
    private String aaid;

    private Random rnd;
    private Application applicationContext;
    private List<String> skipActivityWithClasses;
    private ErrorsSubmitter errorsSubmitter;

    private String defaultUserAgent;
    private RequestQueue queue;
    private Context context;
    private SettingsLoader settingsLoader;
    private SurveyLogger logger;
    private InvitationProvider invitationProvider;
    private Map<String, String> sections;
    private boolean initialized;
    private boolean autoTracking;
    private boolean appStartTracked;

    InAppEventsTrack(Context context, SettingsLoader settingsLoader,
                     SurveyLogger logger, List<String> skipActivityWithClasses,
                     ErrorsSubmitter errorsSubmitter, boolean autoTracking,
                     InvitationProvider invitationProvider) {
        this.context = context;
        this.settingsLoader = settingsLoader;
        this.logger = logger;
        this.skipActivityWithClasses = skipActivityWithClasses;
        this.errorsSubmitter = errorsSubmitter;
        this.autoTracking = autoTracking;
        this.invitationProvider = invitationProvider;

        applicationContext = ((Application) context.getApplicationContext());
        rnd = new Random();
        queue = Volley.newRequestQueue(context);
        applicationContext.registerActivityLifecycleCallbacks(this);
        defaultUserAgent = getUserAgentString(context);
    }

    public void destroy() {
        if (this.applicationContext != null) {
            this.applicationContext.unregisterActivityLifecycleCallbacks(this);
            this.applicationContext = null;
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        // Skip tracking on application start because it is already tracked in UserReportBuilder
        if (autoTracking && appStartTracked) {
            String className = activity.getLocalClassName();
            if (!skipActivityWithClasses.contains(className)) {
                trackScreenView(null);
            }
        }

        appStartTracked = true;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }

    void trackSectionScreenView(String sectionId) {
        checkAAid(() -> {
            String tCode = sections.get(sectionId);
            raiseTrackingCode(tCode, null);
        });
    }

    void trackScreenView(String event) {
        checkAAid(() -> raiseTrackingCode(tCode, event));
    }

    private void checkAAid(Runnable callback) {
        if (!initialized) {
            loadSettings(() -> invitationProvider.createVisit(context, request -> {
                        InAppEventsTrack.this.aaid = request.userInfo.getAdid();
                        InAppEventsTrack.this.bundleId = request.media.bundleId;

                        initialized = true;

                        callback.run();
                    }
            ));
        } else {
            callback.run();
        }
    }

    private void loadSettings(Runnable callback) {
        settingsLoader.registerSettingsLoadCallback(new SettingsLoadingCallback() {
            @Override
            public void onSuccess(MediaSettings settings) {
                InAppEventsTrack.this.tCode = settings.getKitTcode();
                InAppEventsTrack.this.sections = settings.getSections();
                callback.run();
            }

            @Override
            public void onFailed(Exception ex) {
                InAppEventsTrack.this.logger.error("Failed to load settings", ex);
            }
        });
    }

    private void raiseTrackingCode(String tCode, String event) {
        if (tCode == null || tCode.equals("")) return;

        final String url = this.composeUrl(tCode, event);

        StringRequest request = new StringRequest(url, response -> logger.networkActivity("App events tracking", "Ok"
                , url),
                error -> {
                    InAppEventsTrack.this.errorsSubmitter.logError(error, url);
                    logger.error("App events tracking", error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = super.getHeaders();
                Map<String, String> result = new HashMap<>();

                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (!entry.getKey().equals("User-Agent")) {
                        result.put(entry.getKey(), entry.getValue());
                    }
                }

                result.put("User-Agent", InAppEventsTrack.this.defaultUserAgent);

                return result;
            }
        };

        this.queue.add(request);
    }

    private String composeUrl(String tCode, String event) {
        String rndPart = "r=" + rnd.nextInt();
        String tCodePart = getUrlPartData("t=", tCode);
        String devicePart = getUrlPartData("d=", aaid);
        String mediaPart = getUrlPartData("med=", bundleId);

        String baseUrl = this.applicationContext.getString(R.string.ap_visit_analytics_base_url);
        String resultUrl = baseUrl + tCodePart + "&" + rndPart + "&" + devicePart + "&" + mediaPart;

        if (event != null) {
            resultUrl += "&" + getUrlPartData("event=", event);
        }

        return resultUrl;
    }

    private String getUrlPartData(String partPrefix, String partData) {
        String result = partPrefix;
        if (partData != null && partData.length() > 0) {
            try {
                result = partPrefix + URLEncoder.encode(partData, "UTF8");
            } catch (UnsupportedEncodingException e) {
                this.logger.error("Error during url part generation.", e);
                e.printStackTrace();

            }
        }
        return result;
    }


    private String getUserAgentString(Context context) {
        try {
            return new WebView(context).getSettings().getUserAgentString();
        } catch (Exception ex) {
            return null; // exception might appear if there is no google APIs
        }
    }

    void setLogger(SurveyLogger logger) {
        this.logger = logger;
    }

    public void setSkipActivityWithClasses(List<String> skipActivityWithClasses) {
        this.skipActivityWithClasses = skipActivityWithClasses;
    }

    public void setAutoTracking(boolean autoTracking) {
        this.autoTracking = autoTracking;
    }
}
