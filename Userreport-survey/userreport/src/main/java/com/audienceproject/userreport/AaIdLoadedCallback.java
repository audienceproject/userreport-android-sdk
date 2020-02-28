package com.audienceproject.userreport;

interface AaIdLoadedCallback {
    void onSuccess(String aaid);
    void onFailed(Exception e);
}
