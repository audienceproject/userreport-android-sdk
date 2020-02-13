package com.audienceproject.userreport.models;


/**
 * POJO model combining info about user, media, device and app versions.
 */
public class VisitRequest {
    public VisitRequest(){
        this.user = new User();
        this.media = new Media();
        this.device = new Device();
        this.app = new AppModel();
    }

    public User user;
    public Media media;
    public Device device;
    public AppModel app;
}
