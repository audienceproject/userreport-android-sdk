package com.audienceproject.userreport.models;

/**
 * Invitation response from server
 */
public class InvitationResponse {
    /**
     * Will be true if user should be invited to survey.
     */
    public Boolean invite;

    /**
     * Url with survey which need to be loaded
     */
    public String invitationUrl;

    public String userId;

    public String invitationId;
}
