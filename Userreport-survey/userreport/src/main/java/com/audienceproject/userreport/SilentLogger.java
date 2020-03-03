package com.audienceproject.userreport;

import com.audienceproject.userreport.interfaces.SurveyLogger;

/**
 * Default SurveyLogger
 */
class SilentLogger implements SurveyLogger {
    @Override
    public void networkActivity(String type, String data, String url) {

    }

    @Override
    public void error(String message, Exception ex) {

    }
}
