package com.audienceproject.userreport;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

class AaIdProvider {

    private AaidResult aaid;

    public AaIdProvider() {
        aaid = null;
    }

    public void loadAaId(Context context, final AaIdLoadedCallback callback) {
        if (this.aaid != null) {
            callback.onSuccess(aaid.getAaid());
        } else {
            AsyncTask<Context, Void, AaidResult> task = new AsyncTask<Context, Void, AaidResult>() {
                @Override
                protected AaidResult doInBackground(Context... params) {
                    try {
                        AdvertisingIdClient.Info idInfo = AdvertisingIdClient.getAdvertisingIdInfo(params[0]);
                        return new AaidResult(idInfo.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new AaidResult(e);
                    }
                }

                @Override
                protected void onPostExecute(AaidResult result) {
                    aaid = result;
                    if (result.isSuccessful()) {
                        callback.onSuccess(result.getAaid());
                    } else {
                        callback.onFailed(result.getException());
                    }
                }
            };
            task.execute(context);
        }
    }
}
