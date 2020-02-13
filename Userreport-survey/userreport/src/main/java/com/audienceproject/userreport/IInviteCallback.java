package com.audienceproject.userreport;

import com.audienceproject.userreport.models.InvitationResponse;

interface IInviteCallback{
    void processInviteResult(InvitationResponse response);
    void processInviteFail(int httpStatusCode, String message);
}
