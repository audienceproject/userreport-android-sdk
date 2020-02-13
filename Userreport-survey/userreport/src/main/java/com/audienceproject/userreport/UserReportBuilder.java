package com.audienceproject.userreport;

import android.content.Context;

import com.audienceproject.userreport.interfaces.ISurveyFinished;
import com.audienceproject.userreport.interfaces.ISurveyInvoker;
import com.audienceproject.userreport.interfaces.ISurveyLogger;
import com.audienceproject.userreport.invokers.StandardInvoker;
import com.audienceproject.userreport.models.Session;
import com.audienceproject.userreport.models.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Entry point to survey
 * <p>
 * This class used to setup working survey
 */
public class UserReportBuilder {

    private String sakId;
    private String mediaId;
    private List<String> skipActivityWithClasses = new ArrayList<>();

    // Enable automatic screen view tracking by default
    private boolean autoTracking = true;

    private HashMap<UserIdentificationType, String> knownUserInfo;
    private ISurveyInvoker invoker;
    private ISurveyFinished onFinished;

    private ISurveyLogger logger;
    private ISettingsLoader settingsLoader;
    private Settings settings;

    private boolean testMode;
    private ErrorsSubmitter errorSubmitter;

    /**
     * Initialize survey builder with companies ids
     *
     * @param sakId   Same id which used for sak script initialization in your web site
     * @param mediaId Id of web app media which was created for this app in core
     */
    public UserReportBuilder(String sakId, String mediaId) {
        this.sakId = sakId;
        this.mediaId = mediaId;
        this.knownUserInfo = new HashMap<>();
    }

    /**
     * Setups logger for survey.
     * <p>
     * If you do not call this method then default logger will be used.
     *
     * @param logger your implementation of logger
     * @return same instance of SurveyBuilder so you can chain setup calls
     */
    public UserReportBuilder setLogger(ISurveyLogger logger) {
        this.logger = logger;
        return this;
    }

    /**
     * Setups survey finished callback.
     *
     * @param onFinished implementation of ISurveyFinished
     * @return same instance of builder so you can chain setup calls
     */
    public UserReportBuilder setSurveyFinished(ISurveyFinished onFinished) {
        this.onFinished = onFinished;
        return this;
    }

    /**
     * Starts survey in test mode.
     * <p>
     * In this mode you will bw always invited to take survey. This is very useful when you need to check how survey will be displayed.
     * Do not forget to remove call to this method in production.
     *
     * @return same instance of builder so you can chain setup calls
     */
    public UserReportBuilder setSurveyTestMode() {
        this.testMode = true;
        return this;
    }

    /**
     * Allows provide additional information about user
     *
     * @return same instance of SurveyBuilder so you can chain setup calls
     */
    public UserReportBuilder setUserInfo(UserIdentificationType type, String value) {
        this.knownUserInfo.put(type, value);
        return this;
    }

    /**
     * Most important part of survey setup
     * <p>
     * Passed invoker will decide when to invite user to survey. Also there is no default value so you must call this method.
     *
     * @param invoker Invoker class which implements some invite strategy.
     * @return same instance of builder so you can chain setup calls
     */
    public UserReportBuilder setSurveyInvoker(ISurveyInvoker invoker) {
        this.invoker = invoker;
        return this;
    }

    /**
     * UserReport survey settings provided by user
     *
     * @param settings UserReport survey settings
     * @return Instance of ISurvey
     */
    public UserReportBuilder setSettings(Settings settings) {
        this.settings = settings;
        return this;
    }

    /**
     * When all survey preparations finished call this method and receive survey instance
     *
     * @param context usually it context of your main activity
     * @return Instance of UserReport
     */
    public UserReport build(Context context) {
        SharedPreferencesWrapper prefWrapper = new SharedPreferencesWrapper(context);
        Session session = new Session(prefWrapper);

        if (invoker == null) {
            invoker = new StandardInvoker(context, getSettingsLoader(context), session);
        }

        InAppEventsTrack track = new InAppEventsTrack(context, mediaId,
                getSettingsLoader(context), getLogger(), skipActivityWithClasses,
                getErrorSubmitter(context), autoTracking);

        UserReport userReport = new UserReport(context, getSettingsLoader(context), mediaId,
                logger, errorSubmitter, session, track, invoker);
        userReport.setTestMode(testMode);
        userReport.setSurveyOnFinished(onFinished);

        for (UserIdentificationType type : knownUserInfo.keySet()) {
            userReport.setUserInfo(type, knownUserInfo.get(type));
        }

        userReport.logVisit();
        track.trackScreenView("app_started");

        return userReport;
    }

    /**
     * This method needed to prevent counting switches to main activity.
     * <p>
     * For example you have activity called MainActivity and some AuctionActivity. So when user opens some AuctionActivity  we will count it,
     * but then user close this activity and MainActivity will be counted, so if you do not want this return to MainActivity as action call this method and
     * put MainActivity.class.getName() as parameter/
     *
     * @param activityClasses name of activities you do not want to count.
     * @return UserReportBuilder
     */
    public UserReportBuilder skipTrackingFor(List<String> activityClasses) {
        this.skipActivityWithClasses = activityClasses;
        return this;
    }

    /**
     * By default autoTracking = true
     *
     * @param autoTracking
     * @return
     */
    public UserReportBuilder setScreenViewAutoTracking(boolean autoTracking) {
        this.autoTracking = autoTracking;
        return this;
    }

    private ISettingsLoader getSettingsLoader(Context context) {
        String settingsBaseUrl = context.getString(R.string.ap_settings_base_url);
        if (this.settingsLoader == null) {
            this.settingsLoader = new SettingsLoader(context,
                    settingsBaseUrl,
                    this.sakId,
                    this.mediaId,
                    this.getErrorSubmitter(context),
                    this.settings);
        }
        return this.settingsLoader;
    }

    private ErrorsSubmitter getErrorSubmitter(Context context) {
        if (this.errorSubmitter == null) {
            this.errorSubmitter = new ErrorsSubmitter(context, context.getString(R.string.error_logger_url));
        }
        return this.errorSubmitter;
    }

    private ISurveyLogger getLogger() {
        if (this.logger == null) {
            this.logger = new ISurveyLogger() {
                @Override
                public void networkActivity(String type, String data, String url) {
                }

                @Override
                public void error(String message, Exception ex) {
                }
            };
        }
        return this.logger;
    }
}
