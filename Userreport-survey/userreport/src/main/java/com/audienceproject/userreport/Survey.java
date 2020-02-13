package com.audienceproject.userreport;

import android.content.Context;
import android.util.Log;

import com.audienceproject.userreport.interfaces.ISurvey;
import com.audienceproject.userreport.interfaces.ISurveyError;
import com.audienceproject.userreport.interfaces.ISurveyFinished;
import com.audienceproject.userreport.models.InvitationRequest;
import com.audienceproject.userreport.models.InvitationResponse;
import com.audienceproject.userreport.models.Session;
import com.audienceproject.userreport.models.VisitRequest;


/**
 * Implementation of ISurvey.
 */
class Survey implements ISurvey {

    private Context context;
    private Session session;
    private CollectApiClient collectApiClient;
    private IVisitRequestDataProvider invitationProvider;
    private ISurveyFinished onFinished;
    private ErrorsSubmitter errorsSubmitter;

    private ISurveyError onError;
    private int color;
    private final String mediaId;

    private boolean surveyInProgress;

    public Survey(Context context,
                  CollectApiClient collectApi,
                  String mediaId,
                  int color,
                  ErrorsSubmitter errorsSubmitter,
                  Session session,
                  IVisitRequestDataProvider invitationProvider) {
        this.context = context;
        this.collectApiClient = collectApi;
        this.mediaId = mediaId;
        this.color = color;
        this.errorsSubmitter = errorsSubmitter;
        this.session = session;
        this.invitationProvider = invitationProvider;
    }

    public void setSurveyOnFinished(ISurveyFinished callback) {
        this.onFinished = callback;
    }

    public boolean tryInvite() {
        if (surveyInProgress) {
            return false;
        }
        surveyInProgress = true;

        this.invitationProvider.createInvitation(this.context, new IVisitRequestReadyCallBack() {
            CustomTabLauncher v = new CustomTabLauncher(context, color, this::onSurveyClosed, errorsSubmitter);

            String userId;
            String invitationId;

            @Override
            public void onReady(VisitRequest request) {

                collectApiClient.tryInviteToSurvey((InvitationRequest) request, new IInviteCallback() {
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
                        if (onError != null) {
                            onError.handle(httpStatusCode, message);
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

                if (onFinished != null) {
                    onFinished.onFinished();
                }

                surveyInProgress = false;
            }
        });

        return true;
    }

    public void destroy() {
        this.context = null;
        this.onError = null;
    }

    @Override
    public void setOnError(ISurveyError onError) {
        this.onError = onError;
    }
}