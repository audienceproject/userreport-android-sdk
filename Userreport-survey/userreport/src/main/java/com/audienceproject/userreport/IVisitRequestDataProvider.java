package com.audienceproject.userreport;

import android.content.Context;

interface IVisitRequestDataProvider {
    void setUserInfo(UserIdentificationType type, String value);

    void createInvitation(Context context, IVisitRequestReadyCallBack callBack);

    void createVisit(Context context, IVisitRequestReadyCallBack callBack);
}
