package com.audienceproject.userreport;

/**
 * This enum used to specify what kind of additional information you want to provide
 */
public enum UserIdentificationType {
    /***
     * In case if you want provide user email
     */
    Email,
    /***
     * In case if you want provide md5 hash of user email
     */
    EmailMd5,
    /***
     * In case if you want provide 160 bits length sha1 hash of user email
     */
    EmailSha1,
    /***
     * In case if you want provide 256 bits length sha256 hash of user email
     */
    EmailSha256
}
