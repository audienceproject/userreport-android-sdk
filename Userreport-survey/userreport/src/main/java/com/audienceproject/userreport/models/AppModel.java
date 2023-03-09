package com.audienceproject.userreport.models;

/**
 * POJO model describing application and sdk
 */
public class AppModel {
    /**
     * Version of survey sdk
     */
    public String sdk;

    /**
     * Version of application inside which sdk running (actually here will be bundleId with version).
     */
    public String version;
}
