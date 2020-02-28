package com.audienceproject.userreport;

import android.content.Context;

import com.audienceproject.userreport.interfaces.Survey;
import com.audienceproject.userreport.interfaces.SurveyErrorCallback;
import com.audienceproject.userreport.interfaces.SurveyFinishedCallback;
import com.audienceproject.userreport.interfaces.SurveyInvoker;
import com.audienceproject.userreport.interfaces.SurveyLogger;
import com.audienceproject.userreport.invokers.StandardInvoker;
import com.audienceproject.userreport.models.MediaSettings;
import com.audienceproject.userreport.models.Session;
import com.audienceproject.userreport.models.Settings;

public class UserReport {
    private static volatile UserReport instance;
    private UserReportCore core;
    private Context context;
    private CollectApiClient collectApiClient;
    private InAppEventsTrack track;
    private Session session;
    private SurveyInvoker invoker;
    private SettingsLoader settingsLoader;
    private ErrorsSubmitter errorsSubmitter;
    private SurveyLogger logger;
    private InvintationProvider invitationProvider;
    private MediaSettings settings;
    private Survey survey;
    private SurveyErrorCallback surveyErrorCallback;
    private SurveyFinishedCallback surveyFinishedCallBack;


    private UserReport(Context context, UserReportCore core) {
        this.core = core;
        this.context = context;
        init();
    }

    public static UserReport with(Context context, UserReportCore userReportCore) {
        UserReport localInstance = instance;
        if (localInstance == null) {
            synchronized (UserReport.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new UserReport(context, userReportCore);
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
        initTracker();
        createInvitationProvider();
        createCollectApiClient();
        initCallBacks();
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
                core.getSakId(),
                core.getMediaId(),
                errorsSubmitter,
                this.settings);
    }

    private void initInvoker() {
        if (core.getInvoker() == null) {
            invoker = new StandardInvoker(context, settingsLoader, session);
        } else {
            invoker = core.getInvoker();
        }
    }

    private void initLogger() {
        if (core.getLogger() == null) {
            logger = new SilentLogger();
        } else {
            logger = core.getLogger();
        }
    }

    private void initTracker() {
        track = new InAppEventsTrack(context, core.getMediaId(), settingsLoader, logger,
                core.getSkipActivityWithClasses(), errorsSubmitter, core.isAutoTracking());
    }

    private void createInvitationProvider() {
        invitationProvider = new InvintationProvider(core.getMediaId());
        for (UserIdentificationType type : core.getKnownUserInfo().keySet()) {
            invitationProvider.setUserInfo(type, core.getKnownUserInfo().get(type));
        }
    }

    private void createCollectApiClient() {
        String collectApiEndpoint = context.getString(R.string.ap_collect_api_endpoint);
        collectApiClient = new CollectApiClient(collectApiEndpoint, context, logger);
        collectApiClient.setTestMode(core.isTestMode());
    }

    private void initCallBacks() {
        surveyErrorCallback = core.getOnSurveyErrorCallback();
        surveyFinishedCallBack = core.getOnSurveyFinishedCallback();
    }

    private void createSurvey() {
        settingsLoader.registerSettingsLoadCallback(new SettingsLoadingCallback() {
            @Override
            public void onSuccess(MediaSettings settings) {
                UserReport.this.settings = settings;
                invitationProvider.setCompanyId(settings.getCompanyId());
                survey = new UserReportSurvey(context,
                        collectApiClient,
                        core.getMediaId(),
                        settings.getToolBarColor(),
                        errorsSubmitter,
                        session,
                        invitationProvider);
                survey.setSurveyOnFinished(surveyFinishedCallBack);
                survey.setSurveyErrorCallback(surveyErrorCallback);
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
     * For example call of this method may look like: survey.setUserInfo(UserIdentificationType.Email, "know
     * .user@gmail.com")
     *
     * @param type  UserIdentificationType type of data which you want provide.
     * @param value actually data.
     */
    public void setUserInfo(UserIdentificationType type, String value) {
        if (invitationProvider != null) invitationProvider.setUserInfo(type, value);
        createSurvey();
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

    public Settings getSettings() {
        return settings;
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
        this.surveyErrorCallback = callback;
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
        surveyFinishedCallBack = callback;
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
}
