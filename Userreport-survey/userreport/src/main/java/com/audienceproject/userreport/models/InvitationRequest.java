package com.audienceproject.userreport.models;

/**
 * Adds Customization to VisitRequest
 */
public class InvitationRequest extends VisitRequest {
    public InvitationRequest(){
        this.customization =  new Customization();
    }

    public Customization customization;
}
