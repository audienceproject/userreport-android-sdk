package com.audienceproject.userreport;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class HashingHelper {
    public static String MD5(String str) {
        return MakeHash(str, "MD5", 32);
    }

    public static String SHA1(String str) {
        return MakeHash(str, "SHA-1", 40);
    }

    public static String SHA256(String str) {
        return MakeHash(str, "SHA-256", 64);
    }

    private static String MakeHash(String str, String algo, int length) {
        try {
            MessageDigest md = MessageDigest.getInstance(algo);
            byte[] hashBytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
            BigInteger no = new BigInteger(1, hashBytes);
            String format = String.format(Locale.ROOT,"%%0%dx", length);
            return String.format(format, no);
        } catch (NoSuchAlgorithmException ex) {
            //we can't convert so we will just ignore error and send null;
        }
        return null;
    }
}
