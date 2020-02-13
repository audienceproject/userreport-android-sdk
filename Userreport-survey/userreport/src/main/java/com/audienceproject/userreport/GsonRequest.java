package com.audienceproject.userreport;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

class GsonRequest extends Request<String> {
    private final Response.Listener<String> mListener;
    private String data;

    public GsonRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }

    public GsonRequest(int method, String url, String data, Listener<String> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.data = data;
        mListener = listener;
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody() {
        try {
            if (data != null && data.length() > 0){
                return this.data.getBytes("utf-8");
            } else{
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }


    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String responseString = "";
        if (response != null && response.statusCode >= 200 && response.statusCode <= 299) {
            if (response.data != null && response.data.length > 0){
                responseString = new String(response.data);
            }

            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
        } else{
            return Response.error(new VolleyError(response));
        }
    }
}
