package com.audienceproject.userreport;

import android.util.Log;

import com.audienceproject.userreport.interfaces.SurveyLogger;

/**
 * Default SurveyLogger
 */
class DefaultSurveyLogger implements SurveyLogger {
    @Override
    public void networkActivity(String type, String data, String url) {

    }

    @Override
    public void error(String message, Exception ex) {
        Log.e("User Report", message, ex);
    }

    @Override
    public void message(String message) {
        Log.v("User Report", message);
    }
}
