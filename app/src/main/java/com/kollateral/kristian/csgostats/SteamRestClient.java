package com.kollateral.kristian.csgostats;
import com.loopj.android.http.*;

import org.json.JSONObject;

/**
 * Created by Kristian on 2015-08-18.
 */
public class SteamRestClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return  relativeUrl;
    }


}