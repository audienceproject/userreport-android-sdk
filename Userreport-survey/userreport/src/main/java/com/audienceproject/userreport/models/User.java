package com.audienceproject.userreport.models;

/**
 * POJO model describing known user info
 */
public class User {
    private String email;
    private String emailMd5;
    private String emailSha1;
    private String emailSha256;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailMd5() {
        return emailMd5;
    }

    public void setEmailMd5(String emailMd5) {
        this.emailMd5 = emailMd5;
    }

    public String getEmailSha1() {
        return emailSha1;
    }

    public void setEmailSha1(String emailSha1) {
        this.emailSha1 = emailSha1;
    }

    public String getEmailSha256() {
        return emailSha256;
    }

    public void setEmailSha256(String emailSha256) {
        this.emailSha256 = emailSha256;
    }
}
