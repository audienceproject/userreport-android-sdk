package com.audienceproject.userreport.interfaces;

/**
 * Purpose of this interface is to provide clear distinction between general errors and error happen exactly during Survey request
 */
public interface SurveyErrorCallback {
    /**
     * This method will be called if error happen during invitation to survey
     *
     * @param httpStatusCode response http code.
     * @param message exception message
     */
    void handle(int httpStatusCode, String message);
}
