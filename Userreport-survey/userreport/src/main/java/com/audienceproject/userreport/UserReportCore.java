package com.audienceproject.userreport;

import android.text.TextUtils;

import com.audienceproject.userreport.interfaces.SurveyErrorCallback;
import com.audienceproject.userreport.interfaces.SurveyFinishedCallback;
import com.audienceproject.userreport.interfaces.SurveyInvoker;
import com.audienceproject.userreport.interfaces.SurveyLogger;
import com.audienceproject.userreport.models.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserReportCore {
    private String sakId;
    private String mediaId;
    private List<String> skipActivityWithClasses = new ArrayList<>();
    private Map<UserIdentificationType, String> knownUserInfo = new HashMap<>();
    private boolean autoTracking = true;
    private SurveyInvoker invoker;
    private SurveyLogger logger;
    private Settings settings;
    private boolean testMode;
    private SurveyFinishedCallback onSurveyFinishedCallback;
    private SurveyErrorCallback onSurveyErrorCallback;

    private UserReportCore() { }

    String getSakId() {
        return sakId;
    }

    String getMediaId() {
        return mediaId;
    }

    List<String> getSkipActivityWithClasses() {
        return skipActivityWithClasses;
    }

    boolean isAutoTracking() {
        return autoTracking;
    }

    Map<UserIdentificationType, String> getKnownUserInfo() {
        return knownUserInfo;
    }

    SurveyInvoker getInvoker() {
        return invoker;
    }

    SurveyLogger getLogger() {
        return logger;
    }

    Settings getSettings() {
        return settings;
    }

    boolean isTestMode() {
        return testMode;
    }

    SurveyFinishedCallback getOnSurveyFinishedCallback() {
        return onSurveyFinishedCallback;
    }

    SurveyErrorCallback getOnSurveyErrorCallback() {
        return onSurveyErrorCallback;
    }

    /**
     * Init builder.
     *
     * @param sakId   your SakId value
     * @param mediaId your MediaId value
     * @return same instance of SurveyBuilder so you can chain setup calls
     */
    public static Builder newBuilder(String sakId, String mediaId) {
        return new UserReportCore().new Builder(sakId, mediaId);
    }

    public class Builder {

        private Builder(String sakId, String mediaId) {
            UserReportCore.this.mediaId = mediaId;
            UserReportCore.this.sakId = sakId;
        }

        /**
         * Setups logger for survey.
         * <p>
         * If you do not call this method then default logger will be used.
         *
         * @param logger your implementation of logger
         * @return same instance of SurveyBuilder so you can chain setup calls
         */
        public Builder setLogger(SurveyLogger logger) {
            UserReportCore.this.logger = logger;
            return this;
        }

        /**
         * Setups survey finished callback.
         *
         * @param onFinished implementation of ISurveyFinished
         * @return same instance of builder so you can chain setup calls
         */
        public Builder setSurveyFinished(SurveyFinishedCallback onFinished) {
            UserReportCore.this.onSurveyFinishedCallback = onFinished;
            return this;
        }

        /**
         * Setups survey onError callback.
         *
         * @param onError implementation of ISurveyError
         * @return same instance of builder so you can chain setup calls
         */
        public Builder setOnSurveyError(SurveyErrorCallback onError) {
            UserReportCore.this.onSurveyErrorCallback = onError;
            return this;
        }

        /**
         * Starts survey in test mode.
         * <p>
         * In this mode you will bw always invited to take survey. This is very useful when you need to check how
         * survey will be displayed.
         * Do not forget to remove call to this method in production.
         *
         * @return same instance of builder so you can chain setup calls
         */
        public Builder setSurveyTestMode() {
            UserReportCore.this.testMode = true;
            return this;
        }

        /**
         * Allows provide additional information about user
         *
         * @return same instance of SurveyBuilder so you can chain setup calls
         */
        public Builder setUserInfo(UserIdentificationType type, String value) {
            UserReportCore.this.knownUserInfo.put(type, value);
            return this;
        }

        /**
         * Most important part of survey setup
         * <p>
         * Passed invoker will decide when to invite user to survey.
         *
         * @param invoker Invoker class which implements some invite strategy.
         * @return same instance of builder so you can chain setup calls
         */
        public Builder setSurveyInvoker(SurveyInvoker invoker) {
            UserReportCore.this.invoker = invoker;
            return this;
        }

        /**
         * UserReport survey settings provided by user
         *
         * @param settings UserReport survey settings
         * @return Instance of ISurvey
         */
        public Builder setSettings(Settings settings) {
            UserReportCore.this.settings = settings;
            return this;
        }

        /**
         * This method needed to prevent counting switches to main activity.
         * <p>
         * For example you have activity called MainActivity and some AuctionActivity. So when user opens some
         * AuctionActivity  we will count it,
         * but then user close this activity and MainActivity will be counted, so if you do not want this return to
         * MainActivity as action call this method and
         * put MainActivity.class.getName() as parameter/
         *
         * @param activityClasses name of activities you do not want to count.
         * @return UserReportBuilder
         */
        public Builder skipTrackingFor(List<String> activityClasses) {
            UserReportCore.this.skipActivityWithClasses = activityClasses;
            return this;
        }

        /**
         * By default autoTracking = true
         *
         * @param autoTracking
         * @return
         */
        public Builder setScreenViewAutoTracking(boolean autoTracking) {
            UserReportCore.this.autoTracking = autoTracking;
            return this;
        }

        /**
         * When all survey preparations finished call this method and receive UserReportCore instance
         *
         * @return Instance of UserReportCore
         */
        public UserReportCore build() {
            checkSakAndMediaId();
            return UserReportCore.this;
        }

        private void checkSakAndMediaId() {
            if (TextUtils.isEmpty(mediaId) || TextUtils.isEmpty(sakId)) {
                throw new IllegalStateException("You must provide MediaId and SakId!");
            }
        }
    }
}
