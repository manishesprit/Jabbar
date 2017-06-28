package com.jabbar.API;

import android.content.Context;

import com.google.gson.Gson;
import com.jabbar.Bean.MessageBean;
import com.jabbar.Bean.ResponseBean;
import com.jabbar.Bll.MessageBll;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * Created by admin on 10/1/17.
 */

public class SendMessageAPI {
    public Context context;
    public ResponseListener responseListener;

    public SendMessageAPI(final Context context, final ResponseListener responseListener, int friendid, final String message) {
        this.context = context;
        this.responseListener = responseListener;

        UpdateStatusRoutAPI apiMethod = Utils.getRetrofit().create(UpdateStatusRoutAPI.class);
        Call<MessageBean> call = apiMethod.getBean(String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)), String.valueOf(friendid), message);

        call.enqueue(new Callback<MessageBean>() {
            @Override
            public void onResponse(Call<MessageBean> call, Response<MessageBean> response) {

                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response).toString()).getJSONObject("body");
                    Log.print("=====jsonObject=====" + jsonObject.toString());
                } catch (Exception e) {
                }

                if (response.code() == 200) {
                    if (response.body().code == 0) {
                        responseListener.onResponce(Config.TAG_SEND_MESSAGE, Config.API_SUCCESS, response.body());
                    } else {
                        responseListener.onResponce(Config.TAG_SEND_MESSAGE, Config.API_FAIL, response.body().message);
                    }
                } else {
                    responseListener.onResponce(Config.TAG_SEND_MESSAGE, Config.API_FAIL, context.getString(R.string.server_error));
                }

            }

            @Override
            public void onFailure(Call<MessageBean> call, Throwable t) {
                Log.print(t.getMessage());
                responseListener.onResponce(Config.TAG_SEND_MESSAGE, Config.API_FAIL, context.getString(R.string.server_error));

            }
        });


    }

    public interface UpdateStatusRoutAPI {
        @FormUrlEncoded
        @POST(Config.API_SEND_MESSAGE)
        Call<MessageBean> getBean(@Field("userid") String userid, @Field("friendid") String friendid, @Field("message") String message);

    }
}
