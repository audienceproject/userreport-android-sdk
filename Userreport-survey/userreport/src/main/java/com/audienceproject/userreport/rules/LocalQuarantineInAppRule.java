package com.audienceproject.userreport.rules;

import com.audienceproject.userreport.models.Session;

import java.util.Date;

public class LocalQuarantineInAppRule implements IInvitationRule<Integer> {

    private final int localQuarantineDays;
    private final Session session;

    public LocalQuarantineInAppRule(int localQuarantineDays, Session session) {
        this.localQuarantineDays = localQuarantineDays;
        this.session = session;
    }

    @Override
    public boolean isTriggered() {
        Date lq = session.getLocalQuarantineDate();

        if (lq == null) {
            return true;
        }

        return session.getCurrentDate().compareTo(lq) > 0;
    }

    @Override
    public Integer getValue() {
        return this.localQuarantineDays;
    }
}
