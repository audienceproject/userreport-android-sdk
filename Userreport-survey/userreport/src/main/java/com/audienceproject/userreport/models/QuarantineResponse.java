package com.audienceproject.userreport.models;

public class QuarantineResponse {

    private String inGlobalTill;
    private String inLocalTill;
    private Boolean isInGlobal;
    private Boolean isInLocal;

    public String getInGlobalTill() {
        return inGlobalTill;
    }

    public void setInGlobalTill(String inGlobalTill) {
        this.inGlobalTill = inGlobalTill;
    }

    public String getInLocalTill() {
        return inLocalTill;
    }

    public void setInLocalTill(String inLocalTill) {
        this.inLocalTill = inLocalTill;
    }

    public Boolean getInGlobal() {
        return isInGlobal;
    }

    public void setInGlobal(Boolean inGlobal) {
        isInGlobal = inGlobal;
    }

    public Boolean getInLocal() {
        return isInLocal;
    }

    public void setInLocal(Boolean inLocal) {
        isInLocal = inLocal;
    }
}
