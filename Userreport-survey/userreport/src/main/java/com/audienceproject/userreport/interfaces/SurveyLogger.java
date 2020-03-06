package com.audienceproject.userreport.interfaces;

/**
 * General purpose logger
 * <p>
 * Implement this interface if you want to know what sdk send to network and which errors happen. Also this may help during application development.
 */
public interface SurveyLogger {
    /**
     * Will be called when sdk performs network activity
     * @param type Type for the event.
     * @param data Json data
     * @param url Url
     */
    void networkActivity(String type, String data, String url);

    /**
     * Will be called if some error happened
     * @param message
     * @param ex
     */
    void error(String message, Exception ex);

    /**
     * Will be called for additional info
     * @param message
     */
    void message(String message);
}
