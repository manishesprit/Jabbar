

package com.jabbar.API;

import android.content.Context;

import com.jabbar.R;
import com.jabbar.Utils.Log;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;


@SuppressWarnings("deprecation")
public class MultipartRequest {
    public Context caller;
    public MultipartBuilder builder;
    private OkHttpClient client;

    public MultipartRequest(Context caller) {
        this.caller = caller;
        this.builder = new MultipartBuilder();
        this.builder.type(MultipartBuilder.FORM);
        this.client = new OkHttpClient();
    }

    public void addString(String name, String value) {
        Log.print("========== ADD STRING =======" + name + "::" + value);
        this.builder.addFormDataPart(name, value);
    }

    public void addFile(String param, String filePath, String fileName, String mediatype) {
        Log.print("========== ADD File =======" + param + "====" + filePath);
        this.builder.addFormDataPart(param, fileName, RequestBody.create(MediaType.parse(mediatype), new File(filePath)));
        //"image/jpeg"
    }

    public String execute(String url) {
        RequestBody requestBody = null;
        Request request = null;
        Response response = null;

        int code = 200;
        String strResponse = null;

        try {
            requestBody = this.builder.build();
            request = new Request.Builder().url(url).post(requestBody).build();

            Log.print("::::::: REQ :: " + request);
            Log.print("::::::: HEADER :: " + request.headers());

            response = client.newCall(request).execute();
            Log.print("::::::: response :: " + response);

            if (!response.isSuccessful())
                throw new IOException();

            code = response.networkResponse().code();

            if (response.isSuccessful()) {
                strResponse = response.body().string();
            } else if (code == HttpStatus.SC_NOT_FOUND) {
                // ** "Invalid URL or Server not available, please try again" */
                strResponse = caller.getResources().getString(R.string.error_invalid_URL);
            } else if (code == HttpStatus.SC_REQUEST_TIMEOUT) {
                // * "Connection timeout, please try again", */
                strResponse = caller.getResources().getString(R.string.error_timeout);
            } else if (code == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                // *
                // "Invalid URL or Server is not responding, please try again",
                // */
                strResponse = caller.getResources().getString(R.string.error_server_not_responding);
            }
        } catch (Exception e) {
           /* Log.error("Exception", String.valueOf(e));*/
            Log.print("" + e);
        } finally {
            requestBody = null;
            request = null;
            response = null;
            builder = null;
            if (client != null)
                client = null;
            System.gc();
        }
        return strResponse;
    }
}
