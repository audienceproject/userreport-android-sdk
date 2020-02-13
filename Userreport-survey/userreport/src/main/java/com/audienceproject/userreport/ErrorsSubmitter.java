package com.audienceproject.userreport;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;

// Needed to send errors from sdk to our athena db.
class SdkLog {
    public SdkLog(String type, String message, String stackTrace, String url) {
        this.type = type;
        this.stackTrace = stackTrace;
        this.url = url;
        this.timestamp = "$TIMESTAMP_ATHENA";
        this.message = message;
    }

    public String type;
    public String stackTrace;
    public String url;
    public String timestamp;
    public String message;
}

class ErrorsSubmitter {
    private final RequestQueue queue;
    private String url;

    public ErrorsSubmitter(Context context, String url) {
        this.queue = Volley.newRequestQueue(context);
        this.url = url;
    }

    public void logError(Exception ex, String url) {
        StringWriter writer = new StringWriter();
        ex.printStackTrace(new PrintWriter(writer));

        SdkLog log = new SdkLog("Error", ex.toString(), writer.toString(), url);
        this.sendMessage(log);
    }

    public void logMessage(String message) {
        SdkLog log = new SdkLog("Error", message, "", "");
        this.sendMessage(log);
    }

    private void sendMessage(SdkLog log) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String json = gson.toJson(log);

        GsonRequest gsonRequest = new GsonRequest(Request.Method.POST, url, json, response -> {
        }, error -> {
        });

        this.queue.add(gsonRequest);
    }
}
