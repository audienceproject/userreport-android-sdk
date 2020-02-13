package com.audienceproject.userreport.rules;

public interface IInvitationRule<T> {

    boolean isTriggered();

    T getValue();
}


