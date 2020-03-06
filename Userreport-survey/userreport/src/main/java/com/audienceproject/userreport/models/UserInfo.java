package com.audienceproject.userreport.models;
import android.text.TextUtils;
import com.audienceproject.userreport.HashingHelper;

/**
 * When email is passed to the class we rely on our hashing procedure because user may forget to
 * lowercase or trim the email. Though we leave ability to user set hashes by their own in case they
 * don't want provide us email (email is not sent to the server).
 */
public class UserInfo {
    private String adid;
    private String email;
    private String emailMd5;
    private String emailSha1;
    private String emailSha256;

    UserInfo() {}

    public UserInfo(User user) {
        this.email = user.getEmail();
        this.emailMd5 = user.getEmailMd5();
        this.emailSha1 = user.getEmailSha1();
        this.emailSha256 = user.getEmailSha256();
        UpdateHashedEmails();
    }

    private void UpdateHashedEmails(){
        if(!TextUtils.isEmpty(this.email)){
            String normalizedEmail = this.email.trim().toLowerCase();
            this.emailMd5 = HashingHelper.MD5(normalizedEmail);
            this.emailSha1 = HashingHelper.SHA1(normalizedEmail);
            this.emailSha256 = HashingHelper.SHA256(normalizedEmail);
        }
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }

    public void setEmail(String email) {
        this.email = email;
        UpdateHashedEmails();
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
