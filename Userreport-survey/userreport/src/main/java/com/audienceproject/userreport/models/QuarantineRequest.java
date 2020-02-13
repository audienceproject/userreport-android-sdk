package com.audienceproject.userreport.models;

public class QuarantineRequest {

    private String reason;
    private String mediaId;
    private String invitationId;
    private String userId;

    public QuarantineRequest(String reason, String mediaId, String invitationId, String userId) {
        this.reason = reason;
        this.mediaId = mediaId;
        this.invitationId = invitationId;
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public String getMediaId() {
        return mediaId;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public String getUserId() {
        return userId;
    }
}
