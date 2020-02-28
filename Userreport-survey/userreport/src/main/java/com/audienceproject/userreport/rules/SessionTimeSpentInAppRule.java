package com.audienceproject.userreport.rules;


import com.audienceproject.userreport.models.Session;

// Needed to count time spent in application during one user session
public class SessionTimeSpentInAppRule implements InvitationRule<Long> {

    private final long requiredSessionTime;
    private final Session session;

    public SessionTimeSpentInAppRule(long sessionTimeSeconds, Session session) {
        this.requiredSessionTime = sessionTimeSeconds;
        this.session = session;
    }

    @Override
    public boolean isTriggered() {
        return (this.session.getSessionSeconds()) >= this.requiredSessionTime;
    }

    @Override
    public Long getValue() {
        return requiredSessionTime;
    }

}