package com.audienceproject.userreport;

interface IAaIdLoadedCallback {
    void onSuccess(String aaid);
    void onFailed(Exception e);
}
