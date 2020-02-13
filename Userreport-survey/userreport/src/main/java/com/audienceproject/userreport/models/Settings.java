package com.audienceproject.userreport.models;

/**
 * UserReport survey settings.
 */
public class Settings {

    private Integer localQuarantineDays;
    private Integer inviteAfterNSecondsInApp;
    private Integer inviteAfterTotalScreensViewed;
    private Integer sessionScreensView;
    private Integer sessionNSecondsLength;

    public Integer getLocalQuarantineDays() {
        return localQuarantineDays;
    }

    public void setLocalQuarantineDays(Integer localQuarantineDays) {
        this.localQuarantineDays = localQuarantineDays;
    }

    public Integer getInviteAfterNSecondsInApp() {
        return inviteAfterNSecondsInApp;
    }

    public void setInviteAfterNSecondsInApp(Integer inviteAfterNSecondsInApp) {
        this.inviteAfterNSecondsInApp = inviteAfterNSecondsInApp;
    }

    public Integer getInviteAfterTotalScreensViewed() {
        return inviteAfterTotalScreensViewed;
    }

    public void setInviteAfterTotalScreensViewed(Integer inviteAfterTotalScreensViewed) {
        this.inviteAfterTotalScreensViewed = inviteAfterTotalScreensViewed;
    }

    public Integer getSessionScreensView() {
        return sessionScreensView;
    }

    public void setSessionScreensView(Integer sessionScreensView) {
        this.sessionScreensView = sessionScreensView;
    }

    public Integer getSessionNSecondsLength() {
        return sessionNSecondsLength;
    }

    public void setSessionNSecondsLength(Integer sessionNSecondsLength) {
        this.sessionNSecondsLength = sessionNSecondsLength;
    }

    public static Settings getDefaultSettings() {
        Settings settings = new Settings();
        settings.setLocalQuarantineDays(7);
        settings.setInviteAfterNSecondsInApp(60);
        settings.setInviteAfterTotalScreensViewed(5);
        settings.setSessionScreensView(3);
        settings.setSessionNSecondsLength(3);

        return settings;
    }
}
