package com.audienceproject.userreport;

import android.content.Context;

interface VisitRequestDataProvider {
    void setUserInfo(UserIdentificationType type, String value);

    void createInvitation(Context context, VisitRequestReadyCallBack callBack);

    void createVisit(Context context, VisitRequestReadyCallBack callBack);
}
