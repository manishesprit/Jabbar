package com.jabbar.API;

import android.content.Context;
import android.os.AsyncTask;

import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Listener.ResponseListener;

import org.json.JSONObject;

import java.io.File;


/**
 * Created by krunal on 16/1/17.
 */


public class AddStoryAPI extends AsyncTask<Void, Void, Integer> {

    public Context context;
    public ResponseListener handler;
    public MultipartRequest multipartReq;
    public File file = null;
    private String imagename;
    public int code = 1;
    public String message = "";
    public String caption;

    public AddStoryAPI(Context context, String imagename, String caption, ResponseListener handler) {
        this.context = context;
        this.handler = handler;
        this.imagename = imagename;
        this.caption = caption;
    }

    protected Integer doInBackground(Void... params) {

        int result = 1;
        try {

            multipartReq = new MultipartRequest(context);

            if (imagename != null && (imagename != "" || !imagename.equals("") || !imagename.equals("null"))) {
                file = new File(imagename);
                if (file.exists()) {
                    multipartReq.addFile("story", file.toString(), file.getName(), "image/jpeg");
                    multipartReq.addString("caption", caption);
                    multipartReq.addString("userid", String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)));
                    multipartReq.addString("location", String.valueOf(Pref.getValue(context, Config.PREF_LOCATION, "0,0")));
                    Log.print("=========url=========" + Config.HOST + Config.API_ADD_STORY);

                    result = parse(multipartReq.execute(Config.HOST + Config.API_ADD_STORY));

                } else {
                    result = 1;
                    message = "Unable to upload please try again.";
                }
            } else {
                result = 1;
                message = "Unable to upload please try again.";
            }


        } catch (Exception e) {
            result = 1;
            message = "Unable to upload please try again.";
            Log.print("" + e.toString());
        }
        return result;
    }


    protected void onPostExecute(Integer result) {

        if (result == 0) {
            this.handler.onResponce(Config.TAG_ADD_STORY, Config.API_SUCCESS, null);
        } else {
            this.handler.onResponce(Config.TAG_ADD_STORY, Config.API_FAIL, message);
        }
    }

    public int parse(String response) {
        Log.print("=========== RESPONSE ========" + response);

        JSONObject jsonDoc = null;

        try {

            jsonDoc = new JSONObject(response);
            code = jsonDoc.getInt("code");

            if (code == 0) {

                if (file != null && file.exists() && file.getAbsolutePath().contains(context.getCacheDir().toString() + "/story")) {
                    file.delete();
                }
            }
            file = null;
            Log.print("==============code===============" + code);

        } catch (Exception e) {
            Log.print("=========================e=========" + e.toString());
            e.printStackTrace();
        } finally {
            response = null;
            jsonDoc = null;
        }
        return code;
    }
}