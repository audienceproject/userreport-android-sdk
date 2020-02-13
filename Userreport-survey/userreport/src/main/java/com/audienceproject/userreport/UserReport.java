package com.audienceproject.userreport;

import android.content.Context;

import com.audienceproject.userreport.interfaces.ISurvey;
import com.audienceproject.userreport.interfaces.ISurveyError;
import com.audienceproject.userreport.interfaces.ISurveyFinished;
import com.audienceproject.userreport.interfaces.ISurveyInvoker;
import com.audienceproject.userreport.interfaces.ISurveyLogger;
import com.audienceproject.userreport.models.MediaSettings;
import com.audienceproject.userreport.models.Session;
import com.audienceproject.userreport.models.Settings;


public class UserReport {

    private final CollectApiClient collectApiClient;
    private Context context;
    private VisitRequestDataProvider invitationProvider;
    private ISurveyLogger logger;
    private Session session;
    private MediaSettings settings;
    private InAppEventsTrack track;
    private ISurveyInvoker invoker;
    private ISurvey survey;
    private ISurveyError onError;
    private ISurveyFinished onSurveyFinished;

    UserReport(Context context, ISettingsLoader settingsLoader, String mediaId,
               ISurveyLogger logger, ErrorsSubmitter errorsSubmitter, Session session,
               InAppEventsTrack track,
               ISurveyInvoker invoker) {
        this.context = context;
        this.logger = logger;
        this.session = session;
        this.track = track;
        this.invoker = invoker;
        this.invitationProvider = new VisitRequestDataProvider(mediaId);

        String collectApiEndpoint = context.getString(R.string.ap_collect_api_endpoint);
        this.collectApiClient = new CollectApiClient(collectApiEndpoint, context, logger);

        settingsLoader.registerSettingsLoadCallback(new ISettingsCallback() {
            @Override
            public void onSuccess(MediaSettings settings) {
                UserReport.this.settings = settings;
                invitationProvider.setCompanyId(settings.getCompanyId());

                survey = new Survey(context,
                        collectApiClient,
                        mediaId,
                        settings.getToolBarColor(),
                        errorsSubmitter,
                        session,
                        invitationProvider);
                survey.setSurveyOnFinished(onSurveyFinished);
                survey.setOnError(onError);

                invoker.setSurvey(survey);
            }

            @Override
            public void onFailed(Exception ex) {
                UserReport.this.logger.error("Failed to load settings", ex);
            }
        });

        settingsLoader.load();
    }


    public void setTestMode(Boolean testMode) {
        this.collectApiClient.setTestMode(testMode);
    }

    /**
     * Provide possibility to extend data about user sent to backend.
     * For example call of this method may look like: survey.setUserInfo(UserIdentificationType.Email, "know.user@gmail.com")
     *
     * @param type  UserIdentificationType type of data which you want provide.
     * @param value actually data.
     */
    public void setUserInfo(UserIdentificationType type, String value) {
        this.invitationProvider.setUserInfo(type, value);
    }

    public ISurvey getSurvey() {
        return survey;
    }

    public Session getSession() {
        return session;
    }

    public Settings getSettings() {
        return settings;
    }

    public ISurveyInvoker getInvoker() {
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

    /**
     * You can pass onError handler which will be called in case if something went wrong during survey processing.
     * This method will called in both cases if user passed survey and if he canceled it.
     *
     * @param onError - your implementation of ISurveyError
     */
    public void setOnError(ISurveyError onError) {
        if (survey != null) {
            survey.setOnError(onError);
        }
        this.onError = onError;
    }

    public void setSurveyOnFinished(ISurveyFinished callback) {
        if (survey != null) {
            survey.setSurveyOnFinished(callback);
        }
        this.onSurveyFinished = callback;
    }

    /**
     * This method sends visit request to backend and user for sure will not be invited to take survey.
     * You do not need to call this method directly. It will be called when class instantiated.
     */
    public void logVisit() {
        this.invitationProvider.createVisit(this.context, collectApiClient::logVisit);
    }

    public void trackSectionScreenView(String sectionId) {
        track.trackSectionScreenView(sectionId);
    }

    public void trackScreenView() {
        track.trackScreenView(null);
    }
}
