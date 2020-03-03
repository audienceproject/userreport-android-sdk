package com.audienceproject.userreport.rules;

public interface InvitationRule<T> {

    boolean isTriggered();

    T getValue();
}


