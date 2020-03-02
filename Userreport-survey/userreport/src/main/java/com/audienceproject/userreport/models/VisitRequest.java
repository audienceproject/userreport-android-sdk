package com.audienceproject.userreport.models;


import com.google.gson.annotations.SerializedName;

/**
 * POJO model combining info about user, media, device and app versions.
 */
public class VisitRequest {

    public VisitRequest() {
        this.userInfo = new UserInfo();
        this.media = new Media();
        this.device = new Device();
        this.app = new AppModel();
    }

    @SerializedName("user")
    public UserInfo userInfo;
    public Media media;
    public Device device;
    public AppModel app;
}
