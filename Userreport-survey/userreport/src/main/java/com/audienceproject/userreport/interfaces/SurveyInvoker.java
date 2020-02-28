package com.audienceproject.userreport.interfaces;

/**
 * We have tree predefined survey invokers - ActivityChangesSurveyInvoker, ManualEventSurveyInvoker, StandardInvoker.
 * But if you need to decide when to invite user take a survey you can do it by implementing this interface and passign it to builder.
  */
public interface SurveyInvoker {
    /**
     * Method setSurvey will be called inside builder and instance of survey will be passed.
     * @param survey instance of survey.
     */
    void setSurvey(Survey survey);

    /**
     * Stops event tracking.
     * <p>
     * Each implementation of invoker track its specific events. When invoker tries to invite user to survey, there is no need in further tracking of events.
     */
    void stop();

    /**
     * Call it when application destroys.
     * <p>
     * We assume that you track application context state. This method should be called inside application onDestroy.
     * Also invoker responsible to call destroy on underlying survey.
     */
    void destroy();
}