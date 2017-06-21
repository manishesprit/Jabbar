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

public class VerifyCodeAPI extends AsyncTask<String, Void, String> {

    JSONObject j_object = null;
    String str = "";
    InputStream is = null;
    Context context;

    public Utils.MyListener myListener;
    public String code;
    public String session_id;


    public VerifyCodeAPI(Context context, String code, String session_id, Utils.MyListener myListener) {
        this.code = code;
        this.session_id = session_id;
        this.myListener = myListener;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(Utils.getVerifyAPI(code, session_id)).build();
            Log.print("======url=====" + request.url());
            Response response = client.newCall(request).execute();
            Log.print("======response=====" + response);
            if (response.isSuccessful()) {
                j_object = new JSONObject(response.body().string());
                if (j_object != null) {
                    if (j_object.has("Status") && j_object.getString("Status").equalsIgnoreCase("Success")) {
                        myListener.OnResponse(true, j_object.getString("Details"));
                    } else {
                        myListener.OnResponse(false, null);
                    }
                }
            }


        } catch (Exception e) {
            myListener.OnResponse(false, null);
        }

        return null;
    }

}
