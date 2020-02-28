package com.audienceproject.userreport.rules;

import com.audienceproject.userreport.models.Session;

// Counts total screen changed during all user sessions
public class TotalScreensChangeInAppRule implements InvitationRule<Integer> {

    private final int totalScreenChanges;
    private final Session session;

    public TotalScreensChangeInAppRule(int totalScreenChanges, Session session) {
        this.totalScreenChanges = totalScreenChanges;
        this.session = session;
    }

    @Override
    public boolean isTriggered() {
        return this.totalScreenChanges <= 0 || (this.session.getTotalScreenView() >= this.totalScreenChanges);
    }

    @Override
    public Integer getValue() {
        return totalScreenChanges;
    }
}
