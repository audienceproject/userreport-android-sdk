package com.audienceproject.userreport.invokers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.audienceproject.userreport.interfaces.ISurvey;
import com.audienceproject.userreport.interfaces.ISurveyInvoker;

/**
 * Use this invoker if user should be invite to survey after some amount of activity changes.
 * <p>
 * This invoker listen to activity life time events. If activity resumes `eventsCount` times,
 * than it calls survey.tryInvite().
 */
public class ActivityChangesSurveyInvoker implements ISurveyInvoker, Application.ActivityLifecycleCallbacks {
    private ISurvey survey;
    private int requiredEventsCount;
    private static int eventsHappen;
    private String skipActivityWithClass;
    private Application applicationContext;

    /**
     * @param context - Main activity context
     * @param eventsCount - Amount of resumes after which try invite user to survey.
     * @param skipActivityWithClass - If this parameter passed than resumes on this activity will not be count (pass YouActivity.class.getName()).
     */
    public ActivityChangesSurveyInvoker(Context context, final int eventsCount, final String skipActivityWithClass){
        this.requiredEventsCount = eventsCount;
        this.skipActivityWithClass = skipActivityWithClass;

        this.applicationContext = ((Application)context.getApplicationContext());
        this.applicationContext.registerActivityLifecycleCallbacks(this);
    }

    private void eventHappen() {
        this.eventsHappen++;

        if (this.eventsHappen == requiredEventsCount){
            this.survey.tryInvite();
        }
    }

    @Override
    public void setSurvey(ISurvey survey) {
        this.survey = survey;
    }

    @Override
    public void stop() {
        if (this.applicationContext != null) {
            this.applicationContext.unregisterActivityLifecycleCallbacks(this);
            this.applicationContext = null;
        }
    }

    @Override
    public void destroy() {
        this.stop();
        this.survey.destroy();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        String className = activity.getComponentName().getClassName();
        if (className.equals(this.skipActivityWithClass) == false){
            eventHappen();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
}
