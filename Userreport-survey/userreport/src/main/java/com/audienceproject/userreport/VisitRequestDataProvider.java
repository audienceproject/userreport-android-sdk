package com.audienceproject.userreport;

import android.content.Context;

import com.audienceproject.userreport.models.User;

interface VisitRequestDataProvider {
    void setUser(User user);

    void createInvitation(Context context, VisitRequestReadyCallBack callBack);

    void createVisit(Context context, VisitRequestReadyCallBack callBack);
}
