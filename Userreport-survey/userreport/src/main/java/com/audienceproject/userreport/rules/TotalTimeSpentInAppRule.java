package com.audienceproject.userreport.rules;

import com.audienceproject.userreport.models.Session;

// Counts total time spent in app during all user sessions
public class TotalTimeSpentInAppRule implements InvitationRule<Integer> {

    private final int totalSecondsInApp;
    private final Session session;

    public TotalTimeSpentInAppRule(int totalSecondsInApp, Session session) {
        this.totalSecondsInApp = totalSecondsInApp;
        this.session = session;
    }

    @Override
    public boolean isTriggered() {
        return this.totalSecondsInApp <= 0 || (this.session.getTotalSecondsInApp() >= this.totalSecondsInApp);
    }

    @Override
    public Integer getValue() {
        return totalSecondsInApp;
    }
}