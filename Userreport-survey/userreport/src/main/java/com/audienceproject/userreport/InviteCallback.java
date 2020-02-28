package com.audienceproject.userreport;

import com.audienceproject.userreport.models.InvitationResponse;

interface InviteCallback {
    void processInviteResult(InvitationResponse response);
    void processInviteFail(int httpStatusCode, String message);
}
