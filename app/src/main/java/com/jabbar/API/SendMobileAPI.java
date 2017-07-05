package com.jabbar.API;

import android.content.Context;
import android.os.AsyncTask;

import com.jabbar.Utils.Log;
import com.jabbar.Utils.Utils;

import org.json.JSONObject;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SendMobileAPI extends AsyncTask<String, Void, Boolean> {

    JSONObject j_object = null;
    String str = "";
    InputStream is = null;
    Context context;

    public Utils.MyListener myListener;
    public String mobile;
    public String detail;

    public SendMobileAPI(Context context, String mobile, Utils.MyListener myListener) {
        this.mobile = mobile;
        this.myListener = myListener;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        try {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(Utils.getSENDAPI(mobile)).build();
            Log.print("======url=====" + request.url());
            Response response = client.newCall(request).execute();

            Log.print("======response=====" + response);
            if (response.isSuccessful() && response.code() == 200) {
                Log.print("=====response.code()=====" + response.code());
                j_object = new JSONObject(response.body().string());
                if (j_object != null) {
                    if (j_object.has("Status") && j_object.getString("Status").equalsIgnoreCase("Success")) {
                        detail = j_object.getString("Details");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        myListener.OnResponse(aBoolean, aBoolean ? detail : null);
    }
}
