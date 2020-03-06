package com.audienceproject.userreport;

import android.content.Context;

import com.audienceproject.userreport.interfaces.Survey;
import com.audienceproject.userreport.interfaces.SurveyErrorCallback;
import com.audienceproject.userreport.interfaces.SurveyFinishedCallback;
import com.audienceproject.userreport.interfaces.SurveyLogger;
import com.audienceproject.userreport.models.InvitationRequest;
import com.audienceproject.userreport.models.InvitationResponse;
import com.audienceproject.userreport.models.Session;


/**
 * Implementation of Survey.
 */
class UserReportSurvey implements Survey {

    private final String mediaId;
    private Context context;
    private Session session;
    private CollectApiClient collectApiClient;
    private VisitRequestDataProvider invitationProvider;
    private SurveyLogger logger;
    private SurveyFinishedCallback onSurveyFinishedCallback;
    private SurveyErrorCallback surveyErrorCallback;
    private int color;
    private CustomTabLauncher customTabLauncher;
    private String userId;
    private String invitationId;

    private boolean surveyInProgress;

    public UserReportSurvey(Context context,
                            CollectApiClient collectApi,
                            String mediaId,
                            int color,
                            SurveyLogger logger,
                            Session session,
                            VisitRequestDataProvider invitationProvider) {
        this.context = context;
        this.collectApiClient = collectApi;
        this.mediaId = mediaId;
        this.color = color;
        this.logger = logger;
        this.session = session;
        this.invitationProvider = invitationProvider;
    }

    public void setSurveyOnFinished(SurveyFinishedCallback callback) {
        this.onSurveyFinishedCallback = callback;
    }

    public boolean tryInvite() {
        if (surveyInProgress) {
            return false;
        }
        surveyInProgress = true;
        customTabLauncher = new CustomTabLauncher(context, color, this::onSurveyClosed, logger);
        this.invitationProvider.createInvitation(this.context, request ->
                collectApiClient.tryInviteToSurvey((InvitationRequest) request, new InviteCallback() {
                    @Override
                    public void processInviteResult(InvitationResponse response) {
                        if (response.invite) {
                            userId = response.userId;
                            invitationId = response.invitationId;
                            customTabLauncher.loadPage(response.invitationUrl);
                            logger.message("invitationUrl - " + response.invitationUrl);
                        } else {
                            surveyInProgress = false;
                        }
                    }

                    @Override
                    public void processInviteFail(int httpStatusCode, String message) {
                        if (surveyErrorCallback != null) {
                            surveyErrorCallback.handle(httpStatusCode, message);
                        }
                        surveyInProgress = false;
                    }
                }));
        return true;
    }

    private void onSurveyClosed() {
        Runnable updateQuarantineFunction = () -> collectApiClient.getQuarantine(userId, mediaId, response -> {
            if (response.getInLocal()) {
                session.setLocalQuarantineDate(DateConverter.convert(response.getInLocalTill()));
            }
        });

        collectApiClient.setQuarantine("Close", mediaId, invitationId, userId, updateQuarantineFunction);

        if (onSurveyFinishedCallback != null) {
            onSurveyFinishedCallback.onFinished();
        }

        surveyInProgress = false;
    }

    public void destroy() {
        this.context = null;
        this.surveyErrorCallback = null;
    }

    @Override
    public void setSurveyErrorCallback(SurveyErrorCallback surveyErrorCallback) {
        this.surveyErrorCallback = surveyErrorCallback;
    }

    @Override
    public void setLogger(SurveyLogger logger) {
        this.logger = logger;
        if (customTabLauncher!=null) customTabLauncher.setLogger(logger);
    }
}