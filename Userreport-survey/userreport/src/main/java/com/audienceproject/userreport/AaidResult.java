package com.audienceproject.userreport;

class AaidResult {

    private Exception exception;
    private String aaid;

    public AaidResult(String aaid) {
        this.aaid = aaid;
    }

    public AaidResult(Exception ex) {
        this.exception = ex;
        aaid = "00000000-0000-0000-0000-000000000000";
    }

    public Boolean isSuccessful() {
        return this.aaid != null && this.aaid.length() > 0;
    }

    public String getAaid() {
        return this.aaid;
    }

    public Exception getException() {
        return this.exception;
    }
}
