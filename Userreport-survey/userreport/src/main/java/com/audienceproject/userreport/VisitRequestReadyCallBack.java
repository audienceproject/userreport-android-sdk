package com.audienceproject.userreport;

import com.audienceproject.userreport.models.VisitRequest;

interface VisitRequestReadyCallBack {
    void onReady(VisitRequest request);
}