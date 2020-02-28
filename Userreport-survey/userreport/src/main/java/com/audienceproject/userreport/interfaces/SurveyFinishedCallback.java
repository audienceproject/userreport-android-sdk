package com.audienceproject.userreport.interfaces;

/**
 * Implementation of this interface can be provided to builder.
  */
public interface SurveyFinishedCallback {
    /**
     * Will be called when survey activity become inactive.
     */
    void onFinished();
}
