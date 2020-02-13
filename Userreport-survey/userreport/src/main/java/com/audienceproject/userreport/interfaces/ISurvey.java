package com.audienceproject.userreport.interfaces;


/**
 * Implementation of this interface will be returned by builder and same instance passed to ISurveyInvoker.
 */
public interface ISurvey {
    /**
     * Will send invitation request to backend. Depending on response will invite to take survey or not.
     */
    boolean tryInvite();

    /**
     * All resources used by survey will be freed. After call to this method further in app activity tracking impossible.
     */
    void destroy();

    /**
     * You can pass onError handler which will be called in case if something went wrong during survey processing.
     * This method will called in both cases if user passed survey and if he canceled it.
     * @param onError - your implementation of ISurveyError
     */
    void setOnError(ISurveyError onError);

    void setSurveyOnFinished(ISurveyFinished callback);
}