package com.audienceproject.userreport;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;

import com.audienceproject.userreport.models.InvitationRequest;
import com.audienceproject.userreport.models.VisitRequest;

import java.util.HashMap;

/**
 * This class form data for JSON requests to collect api.
 */
class InvintationProvider implements VisitRequestDataProvider {

    private String mediaId;
    private String companyId;
    private AaIdProvider aaIdProvider;
    private HashMap<UserIdentificationType, String> knownUserInfo;

    InvintationProvider(String mediaId) {
        this.mediaId = mediaId;
        this.knownUserInfo = new HashMap<>();
        this.aaIdProvider = new AaIdProvider();
    }

    public void setUserInfo(UserIdentificationType type, String value) {
        if (type != null) {
            this.knownUserInfo.put(type, value);
        }
    }

    void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void createVisit(Context context, VisitRequestReadyCallBack callBack) {
        VisitRequest r = new VisitRequest();

        this.fillWithData(context, r);
        this.loadAaid(context, r, callBack);
    }

    public void createInvitation(Context context, VisitRequestReadyCallBack callBack) {
        InvitationRequest r = new InvitationRequest();

        this.fillWithData(context, r);
        r.customization.hideCloseButton = false;
        r.customization.isCustomTab = true;
        this.loadAaid(context, r, callBack);
    }

    private void loadAaid(Context context, final VisitRequest request, final VisitRequestReadyCallBack callBack) {
        aaIdProvider.loadAaId(context, new AaIdLoadedCallback() {
            public void onSuccess(String aaid) {
                request.user.adid = aaid;
                callBack.onReady(request);
            }

            public void onFailed(Exception e) {
                callBack.onReady(request);
            }
        });
    }

    private void fillWithData(Context context, VisitRequest request) {
        request.user.email = this.knownUserInfo.get(UserIdentificationType.Email);
        request.user.emailMd5 = this.knownUserInfo.get(UserIdentificationType.EmailMd5);
        request.user.emailSha1 = this.knownUserInfo.get(UserIdentificationType.EmailSha1);
        request.user.emailSha256 = this.knownUserInfo.get(UserIdentificationType.EmailSha256);

        request.media.bundleId = this.getPackageFullName(context);
        request.media.mediaId = this.mediaId;
        request.media.companyId = this.companyId;

        request.device.type = "";
        request.device.os = "Android";
        request.device.osVersion = Build.VERSION.RELEASE;
        request.device.brand = Build.BRAND;
        request.device.manufacturer = Build.MANUFACTURER;
        request.device.model = Build.MODEL;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        request.device.screenHeight = displayMetrics.heightPixels;
        request.device.screenWidth = displayMetrics.widthPixels;
        request.device.screenDpi = displayMetrics.densityDpi;
        request.device.type = this.getDeviseDiagonal(displayMetrics) > 6.0 ? "tablet" : "mobile";

        request.app.version = this.getBundleVersion(context);
        request.app.sdk = this.getThisLibraryVersion();
    }

    private double getDeviseDiagonal(DisplayMetrics displayMetrics) {
        double heightInches = displayMetrics.heightPixels / displayMetrics.ydpi;
        double widthInches = displayMetrics.widthPixels / displayMetrics.xdpi;
        double diagonal = Math.sqrt(heightInches * heightInches + widthInches * widthInches);
        return diagonal;
    }

    private PackageInfo getPackage(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo p = packageManager.getPackageInfo(context.getPackageName(), 0);
            return p;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private String getBundleVersion(Context context) {
        PackageInfo p = this.getPackage(context);
        if (p != null) {
            return p.versionName;
        }
        return "";
    }

    private String getThisLibraryVersion() {
        return BuildConfig.VERSION_NAME;
    }

    private String getPackageFullName(Context context) {
        String result = context.getPackageName();
        result = result + " v." + this.getBundleVersion(context);

        return result;
    }
}