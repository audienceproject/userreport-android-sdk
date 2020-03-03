package com.audienceproject.userreport;

import android.content.Context;
import android.util.Log;

import com.audienceproject.userreport.interfaces.SurveyErrorCallback;
import com.audienceproject.userreport.interfaces.SurveyFinishedCallback;
import com.audienceproject.userreport.interfaces.Survey;
import com.audienceproject.userreport.models.InvitationRequest;
import com.audienceproject.userreport.models.InvitationResponse;
import com.audienceproject.userreport.models.Session;
import com.audienceproject.userreport.models.VisitRequest;


/**
 * Implementation of Survey.
 */
class UserReportSurvey implements Survey {

    private final String mediaId;
    private Context context;
    private Session session;
    private CollectApiClient collectApiClient;
    private VisitRequestDataProvider invitationProvider;
    private ErrorsSubmitter errorsSubmitter;
    private SurveyFinishedCallback onSurveyFinishedCallback;
    private SurveyErrorCallback surveyErrorCallback;
    private int color;

    private boolean surveyInProgress;

    public UserReportSurvey(Context context,
                            CollectApiClient collectApi,
                            String mediaId,
                            int color,
                            ErrorsSubmitter errorsSubmitter,
                            Session session,
                            VisitRequestDataProvider invitationProvider) {
        this.context = context;
        this.collectApiClient = collectApi;
        this.mediaId = mediaId;
        this.color = color;
        this.errorsSubmitter = errorsSubmitter;
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

        this.invitationProvider.createInvitation(this.context, new VisitRequestReadyCallBack() {
            CustomTabLauncher v = new CustomTabLauncher(context, color, this::onSurveyClosed, errorsSubmitter);

            String userId;
            String invitationId;

            @Override
            public void onReady(VisitRequest request) {

                collectApiClient.tryInviteToSurvey((InvitationRequest) request, new InviteCallback() {
                    @Override
                    public void processInviteResult(InvitationResponse response) {
                        if (response.invite) {
                            userId = response.userId;
                            invitationId = response.invitationId;

                            v.loadPage(response.invitationUrl);
                            Log.i("invitationUrl ", response.invitationUrl);
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
                });
            }

            void onSurveyClosed() {
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
        });

        return true;
    }

    public void destroy() {
        this.context = null;
        this.surveyErrorCallback = null;
    }

    @Override
    public void setSurveyErrorCallback(SurveyErrorCallback surveyErrorCallback) {
        this.surveyErrorCallback = surveyErrorCallback;
    }
}