package com.audienceproject.userreport.models;

import com.audienceproject.userreport.DateConverter;
import com.audienceproject.userreport.SharedPreferencesWrapper;

import java.util.Calendar;
import java.util.Date;

public class Session {

    private static final String TOTAL_MLS_IN_APP_KEY = "TOTAL_MLS_IN_APP_KEY";
    private static final String TOTAL_SCREEN_CHANGES_IN_APP_KEY = "TOTAL_SCREEN_CHANGES_IN_APP_KEY";
    private static final String LOCAL_QUARANTINE_DATE = "LOCAL_QUARANTINE_DATE";

    private final SharedPreferencesWrapper preferencesWrapper;
    private int sessionScreensView;

    private boolean isCounting;
    private long lastStartTime;
    private long currentSessionTime;

    public Session(SharedPreferencesWrapper preferencesWrapper) {
        this.preferencesWrapper = preferencesWrapper;
        initDefaultValues();
    }

    private void initDefaultValues() {
        if (getLocalQuarantineDate() == null) {
            setLocalQuarantineDate(new Date());
        }
    }

    public Date getLocalQuarantineDate() {
        return DateConverter.convert(preferencesWrapper.readString(LOCAL_QUARANTINE_DATE));
    }

    public void setLocalQuarantineDate(Date date) {
        this.preferencesWrapper.writeString(LOCAL_QUARANTINE_DATE, DateConverter.asString(date));
    }

    public Integer getScreenView() {
        return sessionScreensView;
    }


    public long getTotalScreenView() {
        return this.preferencesWrapper.readLong(TOTAL_SCREEN_CHANGES_IN_APP_KEY);
    }

    public long getSessionSeconds() {
        long result = currentSessionTime;

        if (isCounting) {
            result = new Date().getTime() - lastStartTime + currentSessionTime;
        }

        return result / 1000;
    }

    public long getTotalSecondsInApp() {
        long result = this.preferencesWrapper.readLong(TOTAL_MLS_IN_APP_KEY);

        if (isCounting) {
            result = new Date().getTime() - lastStartTime + result;
        }

        return result / 1000;
    }

    public void stopCounting() {
        if (isCounting) {
            currentSessionTime += new Date().getTime() - lastStartTime;

            long totalSpentTime = this.preferencesWrapper.readLong(TOTAL_MLS_IN_APP_KEY);
            this.preferencesWrapper.writeLong(TOTAL_MLS_IN_APP_KEY, totalSpentTime + new Date().getTime() - lastStartTime);
        }
        this.isCounting = false;
    }

    public void continueCounting() {
        this.isCounting = true;
        this.lastStartTime = new Date().getTime();
    }

    public void trackScreen() {
        long saved = this.preferencesWrapper.readLong(TOTAL_SCREEN_CHANGES_IN_APP_KEY);
        this.preferencesWrapper.writeLong(TOTAL_SCREEN_CHANGES_IN_APP_KEY, saved + 1);
        this.sessionScreensView++;
    }


    public void updateLocalQuarantine(int daysCount) {
        this.setLocalQuarantineDate(DateConverter.addDays(daysCount));
    }

    public Date getCurrentDate() {
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        return c.getTime();
    }
}
