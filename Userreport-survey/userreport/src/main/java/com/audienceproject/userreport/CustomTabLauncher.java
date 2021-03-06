package com.audienceproject.userreport;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.browser.customtabs.CustomTabsCallback;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;

import com.audienceproject.userreport.interfaces.SurveyFinishedCallback;
import com.audienceproject.userreport.interfaces.SurveyLogger;

import java.util.ArrayList;

class CustomTabLauncher {
    private static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    private CustomTabsClient client;
    private String urlToLoad;
    private Context context;
    private SurveyFinishedCallback onFinished;
    private int color;
    private SurveyLogger logger;

    public CustomTabLauncher(Context context, int color, SurveyFinishedCallback onFinished, SurveyLogger logger) {
        this.context = context;
        this.color = color;
        this.onFinished = onFinished;
        this.logger = logger;
    }

    public void loadPage(String url) {
        this.urlToLoad = url;

        CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                CustomTabLauncher.this.initClient(client);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                CustomTabLauncher.this.disconnectClient();
            }
        };

        boolean isBinded = CustomTabsClient.bindCustomTabsService(this.context, CUSTOM_TAB_PACKAGE_NAME, connection);
        if (!isBinded) {
            logger.message("Was not able to bind to Chrome Custom Tab");
        }
    }

    private void initClient(CustomTabsClient client) {
        this.client = client;
        this.client.warmup(0);
        CustomTabsSession session = this.client.newSession(new CustomTabsCallback() {
            @Override
            public void onNavigationEvent(int navigationEvent, Bundle extras) {
                logger.message("SurveyLauncher[onNav] " + navigationEvent);
                if (navigationEvent == CustomTabsCallback.TAB_HIDDEN && CustomTabLauncher.this.onFinished != null) {
                    CustomTabLauncher.this.onFinished.onFinished();
                }
            }
        });

        Uri uri = Uri.parse(this.urlToLoad);
        if (session != null)
            session.mayLaunchUrl(uri, null, new ArrayList<>());

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(session);
        builder.setToolbarColor(this.color);
        builder.setSecondaryToolbarColor(this.color);
        builder.setShowTitle(true);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.setPackage("com.android.chrome");
        customTabsIntent.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        customTabsIntent.launchUrl(this.context, uri);
    }

    private void disconnectClient() {
        this.client = null;
    }

    void setLogger(SurveyLogger logger) {
        this.logger = logger;
    }
}
