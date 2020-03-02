package com.audienceproject.userreport;

import android.content.Context;
import android.text.TextUtils;

import com.audienceproject.userreport.interfaces.Survey;
import com.audienceproject.userreport.interfaces.SurveyErrorCallback;
import com.audienceproject.userreport.interfaces.SurveyFinishedCallback;
import com.audienceproject.userreport.interfaces.SurveyInvoker;
import com.audienceproject.userreport.interfaces.SurveyLogger;
import com.audienceproject.userreport.invokers.StandardInvoker;
import com.audienceproject.userreport.models.MediaSettings;
import com.audienceproject.userreport.models.Session;
import com.audienceproject.userreport.models.Settings;
import com.audienceproject.userreport.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserReport {
    private static volatile UserReport instance;
    private String sakId;
    private String mediaId;
    private Settings settings;
    private User user;
    private Context context;
    private SurveyInvoker invoker;
    private List<String> skipActivityWithClasses = new ArrayList<>();
    private boolean autoTracking = true;
    private SurveyLogger logger;
    private boolean testMode = false;

    private SurveyFinishedCallback onSurveyFinishedCallback;
    private SurveyErrorCallback onSurveyErrorCallback;

    private CollectApiClient collectApiClient;
    private InAppEventsTrack track;
    private Session session;
    private SettingsLoader settingsLoader;
    private ErrorsSubmitter errorsSubmitter;
    private InvitationProvider invitationProvider;
    private MediaSettings mediaSettings;
    private Survey survey;


    private UserReport(Context context, String sakId, String mediaId, User user, Settings settings,
                       SurveyInvoker invoker) {
        this.mediaId = mediaId;
        this.sakId = sakId;
        this.settings = settings;
        this.user = user;
        this.invoker = invoker;
        this.context = context;
        checkSakAndMediaId();
        init();
    }

    public static UserReport configure(Context context, String sakId, String mediaId, User user, Settings settings,
                                       SurveyInvoker invoker) {
        UserReport localInstance = instance;
        if (localInstance == null) {
            synchronized (UserReport.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new UserReport(context, sakId, mediaId, user, settings, invoker);
                }
            }
        }
        return localInstance;
    }

    private void init() {
        createSession();
        initErrorSubmitter();
        initSettingsLoader();
        initInvoker();
        initLogger();
        createInvitationProvider();
        initTracker();
        createCollectApiClient();
        createSurvey();
        logVisit();
        trackAppStarted();
    }

    private void createSession() {
        SharedPreferencesWrapper prefWrapper = new SharedPreferencesWrapper(context);
        session = new Session(prefWrapper);
    }

    private void initErrorSubmitter() {
        errorsSubmitter = new ErrorsSubmitter(context, context.getString(R.string.error_logger_url));
    }

    private void initSettingsLoader() {
        String settingsBaseUrl = context.getString(R.string.ap_settings_base_url);
        settingsLoader = new UserReportSettingsLoader(context,
                settingsBaseUrl,
                sakId,
                mediaId,
                errorsSubmitter,
                settings);
    }

    private void initInvoker() {
        if (invoker == null) {
            invoker = new StandardInvoker(context, settingsLoader, session);
        }
    }

    private void initLogger() {
        if (logger == null) {
            logger = new SilentLogger();
        }
    }

    private void initTracker() {
        track = new InAppEventsTrack(context, settingsLoader, logger,
                skipActivityWithClasses, errorsSubmitter, autoTracking, invitationProvider);
    }

    private void createInvitationProvider() {
        invitationProvider = new InvitationProvider(mediaId, user);
    }

    private void createCollectApiClient() {
        String collectApiEndpoint = context.getString(R.string.ap_collect_api_endpoint);
        collectApiClient = new CollectApiClient(collectApiEndpoint, context, logger);
        collectApiClient.setTestMode(testMode);
    }

    private void createSurvey() {
        settingsLoader.registerSettingsLoadCallback(new SettingsLoadingCallback() {
            @Override
            public void onSuccess(MediaSettings settings) {
                mediaSettings = settings;
                invitationProvider.setCompanyId(settings.getCompanyId());
                survey = new UserReportSurvey(context,
                        collectApiClient,
                        mediaId,
                        settings.getToolBarColor(),
                        errorsSubmitter,
                        session,
                        invitationProvider);
                survey.setSurveyErrorCallback(onSurveyErrorCallback);
                survey.setSurveyOnFinished(onSurveyFinishedCallback);
                invoker.setSurvey(survey);
            }

            @Override
            public void onFailed(Exception ex) {
                logger.error("Failed to load settings", ex);
            }
        });

        settingsLoader.load();
    }

    /**
     * Provide possibility to extend data about user sent to backend.
     *
     * @param user User info
     */
    public void updateUser(User user) {
        if (invitationProvider != null) invitationProvider.setUser(user);
    }

    public void setTestMode(Boolean testMode) {
        if (collectApiClient != null)
            collectApiClient.setTestMode(testMode);
    }

    public Survey getSurvey() {
        return survey;
    }

    public Session getSession() {
        return session;
    }

    public Settings getMediaSettings() {
        return mediaSettings;
    }

    public SurveyInvoker getInvoker() {
        return invoker;
    }

    public void destroy() {
        survey.destroy();
        collectApiClient.destroy();
        track.destroy();
        invoker.destroy();
        logger = null;
        invitationProvider = null;
    }

    private void trackAppStarted() {
        track.trackScreenView("app_started");
    }

    /**
     * Will send invitation request to backend. Depending on response will invite to take survey or not.
     */
    public void tryToInvite() {
        if (survey != null) survey.tryInvite();
    }

    /**
     * You can pass onSurveyError handler which will be called in case if something went wrong during survey processing.
     * This method will called in both cases if user passed survey and if he canceled it.
     *
     * @param callback - your implementation of ISurveyError
     */
    public void setOnErrorListener(SurveyErrorCallback callback) {
        if (survey != null) {
            survey.setSurveyErrorCallback(callback);
        }
        onSurveyErrorCallback = callback;
    }

    /**
     * You can pass onSurveyFinished handler which will be called when a survey is finished.
     *
     * @param callback - your implementation of ISurveyError
     */
    public void setSurveyFinishedCallback(SurveyFinishedCallback callback) {
        if (survey != null) {
            survey.setSurveyOnFinished(callback);
        }
        onSurveyFinishedCallback = callback;
    }

    /**
     * This method sends visit request to backend and user for sure will not be invited to take survey.
     * You do not need to call this method directly. It will be called when class instantiated.
     */
    public void logVisit() {
        this.invitationProvider.createVisit(this.context, collectApiClient::logVisit);
    }

    /**
     * Manual tracking of selection screen
     *
     * @param sectionId - your selection screen id
     */
    public void trackSectionScreenView(String sectionId) {
        track.trackSectionScreenView(sectionId);
    }

    /**
     * Manual tracking of screen
     */
    public void trackScreenView() {
        track.trackScreenView(null);
    }

    /**
     * Setups logger for survey.
     * <p>
     * If you do not call this method then default logger will be used.
     *
     * @param logger your implementation of logger
     */
    public void setLogger(SurveyLogger logger) {
        if (track != null) track.setLogger(logger);
        if (collectApiClient != null) collectApiClient.setLogger(logger);
    }

    private void checkSakAndMediaId() {
        if (TextUtils.isEmpty(mediaId) || TextUtils.isEmpty(sakId)) {
            throw new IllegalStateException("You must provide MediaId and SakId!");
        }
    }

    /**
     * By default autoTracking = true
     *
     * @param autoTracking
     */
    public void setAutoTracking(boolean autoTracking) {
        this.autoTracking = autoTracking;
        if (track != null)
            track.setAutoTracking(autoTracking);
    }

    /**
     * UserReport survey settings provided by user
     *
     * @param settings UserReport survey settings
     */
    public void updateSettings(Settings settings) {
        this.settings = settings;
        initSettingsLoader();
        createSurvey();
    }

    /**
     * Starts survey in test mode.
     * <p>
     * In this mode you will bw always invited to take survey. This is very useful when you need to check how
     * survey will be displayed.
     * Do not forget to remove call to this method in production.
     */
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
        if (collectApiClient != null) collectApiClient.setTestMode(testMode);
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
     * @param skipActivityWithClasses name of activities you do not want to count.
     * @return UserReportBuilder
     */
    public void skipTrackingFor(List<String> skipActivityWithClasses) {
        this.skipActivityWithClasses = skipActivityWithClasses;
        if (track != null)
            track.setSkipActivityWithClasses(skipActivityWithClasses);
    }
}
