package com.jabbar.API;

import android.content.Context;

import com.google.gson.Gson;
import com.jabbar.Bean.DataBean;
import com.jabbar.Bean.MessageBean;
import com.jabbar.Bean.SendMessageBean;
import com.jabbar.Bll.MessageBll;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * Created by admin on 10/1/17.
 */

public class SendMessageNewAPI {
    public Context context;
    public ResponseListener responseListener;
    public MessageBll messageBll;
    public static boolean isCallAPI = false;

    public SendMessageNewAPI(final Context context, final ResponseListener responseListener, String data) {
        this.context = context;
        this.responseListener = responseListener;
        isCallAPI = true;
        SendNewMessageRoutAPI apiMethod = Utils.getRetrofit().create(SendNewMessageRoutAPI.class);
        Call<SendMessageBean> call = apiMethod.getBean(String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)), data);

        call.enqueue(new Callback<SendMessageBean>() {
            @Override
            public void onResponse(Call<SendMessageBean> call, Response<SendMessageBean> response) {

                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response).toString()).getJSONObject("body");
                    Log.print("=====jsonObject=====" + jsonObject.toString());
                } catch (Exception e) {
                    isCallAPI = false;
                }

                if (response.code() == 200) {
                    if (response.body().code == 0) {
                        messageBll = new MessageBll(context);
                        for (DataBean dataBean : response.body().data) {
                            for (MessageBean messageBean : dataBean.messages) {
                                messageBll.UpdateMessage(messageBean, dataBean.friendid);
                            }
                        }
                        isCallAPI = false;
                        responseListener.onResponce(Config.TAG_SEND_MESSAGE, Config.API_SUCCESS, response.body());
                    } else {
                        isCallAPI = false;
                        responseListener.onResponce(Config.TAG_SEND_MESSAGE, Config.API_FAIL, response.body().message);
                    }
                } else {
                    isCallAPI = false;
                    responseListener.onResponce(Config.TAG_SEND_MESSAGE, Config.API_FAIL, context.getString(R.string.server_error));
                }

            }

            @Override
            public void onFailure(Call<SendMessageBean> call, Throwable t) {
                isCallAPI = false;
                Log.print(t.getMessage());
                responseListener.onResponce(Config.TAG_SEND_MESSAGE, Config.API_FAIL, context.getString(R.string.server_error));

            }
        });


    }

    public interface SendNewMessageRoutAPI {
        @FormUrlEncoded
        @POST(Config.API_SEND_NEW_MESSAGE)
        Call<SendMessageBean> getBean(@Field("userid") String userid, @Field("data") String message);

    }
}
