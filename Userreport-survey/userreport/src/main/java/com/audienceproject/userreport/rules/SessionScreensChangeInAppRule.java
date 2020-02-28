package com.audienceproject.userreport.rules;

import com.audienceproject.userreport.models.Session;

// Needed to count screen changes during one user session
public class SessionScreensChangeInAppRule implements InvitationRule<Long> {

    private final long screenChanges;
    private final Session session;

    public SessionScreensChangeInAppRule(long screenChanges, Session session) {
        this.screenChanges = screenChanges;
        this.session = session;
    }

    @Override
    public boolean isTriggered() {
        return this.session.getScreenView() >= this.screenChanges;
    }

    @Override
    public Long getValue() {
        return screenChanges;
    }
}
