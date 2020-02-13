package com.audienceproject.userreport;

import com.audienceproject.userreport.models.VisitRequest;

interface IVisitRequestReadyCallBack{
    void onReady(VisitRequest request);
}