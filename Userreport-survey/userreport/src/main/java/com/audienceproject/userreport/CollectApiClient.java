package com.audienceproject.userreport;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.audienceproject.userreport.interfaces.Consumer;
import com.audienceproject.userreport.interfaces.SurveyLogger;
import com.audienceproject.userreport.models.InvitationRequest;
import com.audienceproject.userreport.models.InvitationResponse;
import com.audienceproject.userreport.models.QuarantineRequest;
import com.audienceproject.userreport.models.QuarantineResponse;
import com.audienceproject.userreport.models.VisitRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

class CollectApiClient {
    private boolean testModeOn = false;
    private SurveyLogger logger;
    private String apiUrl;

    private long tag;

    private final String invitationUrl = "visit+invitation";
    private final String visitUrl = "visit";
    private final String testInvitationUrl = "visit+invitation/testinvite";
    private final String quarantineUrl = "quarantine";

    private RequestQueue queue;

    public CollectApiClient(String apiUrl, Context context, SurveyLogger networkLogger) {
        this.tag = new Date().getTime();
        this.apiUrl = apiUrl;
        this.queue = Volley.newRequestQueue(context);
        this.logger = networkLogger;
    }

    public void setTestMode(boolean isTestModeOn) {
        this.testModeOn = isTestModeOn;
    }

    public void logVisit(VisitRequest visit) {
        final String url = this.apiUrl + this.visitUrl;

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String json = gson.toJson(visit);

        this.logger.networkActivity("Request", json, url);

        GsonRequest gsonRequest = new GsonRequest(Request.Method.POST,
                url,
                json,
                response -> CollectApiClient.this.logger.networkActivity("Response", response, url),
                error -> CollectApiClient.this.logger.error("Log Visit Response ERROR", error));

        gsonRequest.setTag(this.tag);
        this.queue.add(gsonRequest);
    }

    public void tryInviteToSurvey(InvitationRequest invitation, final InviteCallback callback) {
        String url = this.apiUrl + this.invitationUrl;
        if (this.testModeOn) {
            url = this.apiUrl + this.testInvitationUrl;
        }

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String json = gson.toJson(invitation);

        this.logger.networkActivity("Request", json, url);

        GsonRequest gsonRequest = new GsonRequest(Request.Method.POST, url, json, response -> {
            logger.networkActivity("Response", response, "");
            InvitationResponse result = gson.fromJson(response, InvitationResponse.class);
            callback.processInviteResult(result);
        }, error -> {
            CollectApiClient.this.logger.error("Try Invite To Survey Response ERROR", error);
            int statusCode = 404;
            if (error.networkResponse != null) {
                statusCode = error.networkResponse.statusCode;
            }
            callback.processInviteFail(statusCode, error.getMessage());
        });

        this.queue.add(gsonRequest);
    }

    public void destroy() {
        this.queue.cancelAll(this.tag);
    }

    public void setQuarantine(String reason, String mediaId, String invitationId, String userId, Runnable updateQuarantine) {
        final String url = apiUrl + quarantineUrl;

        final QuarantineRequest request = new QuarantineRequest(reason, mediaId, invitationId, userId);
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String json = gson.toJson(request);

        this.logger.networkActivity("Request", json, url);

        GsonRequest gsonRequest = new GsonRequest(Request.Method.POST,
                url,
                json,
                response -> {
                    logger.networkActivity("Response", response, url);
                    updateQuarantine.run();
                },
                error -> {
                    logger.error("Set Quarantine Response ERROR", error);
                    updateQuarantine.run();
                });

        gsonRequest.setTag(tag);
        this.queue.add(gsonRequest);
    }

    public void getQuarantine(String userId, String mediaId, Consumer<QuarantineResponse> responseConsumer) {
        final String url = String.format("%s%s/%s/media/%s/info", apiUrl, quarantineUrl, userId, mediaId);

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        this.logger.networkActivity("Request", null, url);

        GsonRequest gsonRequest = new GsonRequest(Request.Method.GET,
                url,
                response -> {
                    logger.networkActivity("Response", response, url);
                    responseConsumer.consume(gson.fromJson(response, QuarantineResponse.class));
                },
                error -> logger.error("Get Quarantine Response ERROR", error));

        gsonRequest.setTag(tag);
        this.queue.add(gsonRequest);
    }

    public void setLogger(SurveyLogger logger) {
        this.logger = logger;
    }
}