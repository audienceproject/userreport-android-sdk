package com.audienceproject.userreport.invokers;

import com.audienceproject.userreport.interfaces.Survey;
import com.audienceproject.userreport.interfaces.SurveyInvoker;

/**
 * Provide possibility to track events by yourself.
 * <p>
 * Which events to track you decide by yourself. When event happen call method makeEvent().
 * If amount of events equal to passed in constructor eventsCount than it calls survey.tryInvite().
 */

public class ManualEventSurveyInvoker implements SurveyInvoker {
    private Survey survey;
    private int requiredEventsCount;
    private int eventsHappen;
    private Boolean trackEvents;

    public ManualEventSurveyInvoker(int eventsCount){
        this.requiredEventsCount = eventsCount;
        this.trackEvents = true;
    }

    @Override
    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    @Override
    public void stop() {
        this.trackEvents = false;
    }

    @Override
    public void destroy() {
        this.survey.destroy();
    }

    /**
     * Call this method when your custom event happen.
     */
    public void makeEvent(){
        this.eventHappen();
    }

    private void eventHappen(){
        this.eventsHappen++;
        if (this.eventsHappen == this.requiredEventsCount && this.trackEvents) {
            this.survey.tryInvite();
        }
    }
}