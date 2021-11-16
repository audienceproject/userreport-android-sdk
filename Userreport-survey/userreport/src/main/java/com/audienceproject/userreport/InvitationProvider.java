package com.audienceproject.userreport;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;

import com.audienceproject.userreport.models.InvitationRequest;
import com.audienceproject.userreport.models.User;
import com.audienceproject.userreport.models.UserInfo;
import com.audienceproject.userreport.models.VisitRequest;

/**
 * This class form data for JSON requests to collect api.
 */
class InvitationProvider implements VisitRequestDataProvider {

    private String mediaId;
    private String companyId;
    private AaIdProvider aaIdProvider;
    private User user;

    InvitationProvider(String mediaId, User user) {
        this.mediaId = mediaId;
        if (user == null) {this.user = new User();} else { this.user = user; }
        this.aaIdProvider = new AaIdProvider();
    }

    public void setUser(User user) {
        this.user = user;
    }

    void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void createVisit(Context context, VisitRequestReadyCallBack callBack) {
        VisitRequest request = new VisitRequest();
        fillWithData(context, request);
        loadAaid(context, request, callBack);
    }

    public void createInvitation(Context context, VisitRequestReadyCallBack callBack) {
        InvitationRequest request = new InvitationRequest();
        fillWithData(context, request);
        request.customization.hideCloseButton = false;
        request.customization.isCustomTab = true;
        this.loadAaid(context, request, callBack);
    }

    private void loadAaid(Context context, final VisitRequest request, final VisitRequestReadyCallBack callBack) {
        aaIdProvider.loadAaId(context, new AaIdLoadedCallback() {
            public void onSuccess(String aaid) {
                request.userInfo.setAdid(aaid);
                callBack.onReady(request);
            }

            public void onFailed(Exception e) {
                callBack.onReady(request);
            }
        });
    }

    private void fillWithData(Context context, VisitRequest request) {
        if (user != null) {
            request.userInfo = new UserInfo(user);
        }

        request.media.bundleId = this.getPackageName(context);
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
        return Math.sqrt(heightInches * heightInches + widthInches * widthInches);
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

    private String getPackageName(Context context) {
        return context.getPackageName();
    }

    private String getThisLibraryVersion() {
        return BuildConfig.VERSION_NAME;
    }
}