package com.jabbar.API;

import android.content.Context;

import com.google.gson.Gson;
import com.jabbar.Bean.ResponseBean;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * Created by admin on 10/1/17.
 */

public class UpdateStatusAPI {
    public Context context;
    public ResponseListener responseListener;

    public UpdateStatusAPI(final Context context, final ResponseListener responseListener, final String status) {
        this.context = context;
        this.responseListener = responseListener;
        HashMap<String, String> mParams = new HashMap<String, String>();
        mParams.put("userid", String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)));
        mParams.put("status", status);
        mParams.put("udid", Pref.getValue(context, Config.PREF_UDID, ""));
        mParams.put("location", Pref.getValue(context, Config.PREF_LOCATION, "0,0"));

        Log.print("======UpdateStatusAPI====" + mParams);

        UpdateStatusRoutAPI apiMethod = Utils.getRetrofit().create(UpdateStatusRoutAPI.class);
        Call<ResponseBean> call = apiMethod.getBean(String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)), status, Pref.getValue(context, Config.PREF_UDID, ""), Pref.getValue(context, Config.PREF_LOCATION, "0,0"));

        call.enqueue(new Callback<ResponseBean>() {
            @Override
            public void onResponse(Call<ResponseBean> call, Response<ResponseBean> response) {

                Log.print("====onResponse====response.code()=" + response.code() + "=====url=====" + call.request().url());
                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response).toString()).getJSONObject("body");
                    Log.print("=====jsonObject=====" + jsonObject.toString());
                } catch (Exception e) {
                }

                if (response.code() == 200) {
                    if (response.body().code == 0) {
                        Pref.setValue(context, Config.PREF_STATUS, StringEscapeUtils.unescapeJava(status));
                        responseListener.onResponce(Config.TAG_UPDATE_STATUS, Config.API_SUCCESS, response.body());
                    } else {
                        responseListener.onResponce(Config.TAG_UPDATE_STATUS, Config.API_FAIL, response.body().message);
                    }
                } else {
                    responseListener.onResponce(Config.TAG_UPDATE_STATUS, Config.API_FAIL, context.getString(R.string.server_error));
                }

            }

            @Override
            public void onFailure(Call<ResponseBean> call, Throwable t) {
                Log.print(t.getMessage());
                responseListener.onResponce(Config.TAG_UPDATE_STATUS, Config.API_FAIL, context.getString(R.string.server_error));

            }
        });


    }

    public interface UpdateStatusRoutAPI {
        @FormUrlEncoded
        @POST(Config.API_UPDATE_STATUS)
        Call<ResponseBean> getBean(@Field("userid") String userid, @Field("status") String status, @Field("udid") String udid, @Field("location") String location);

    }
}
