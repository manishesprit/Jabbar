package com.jabbar.API;

import android.content.Context;

import com.jabbar.Bean.ResponseBean;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Listener.ResponseListener;
import com.jabbar.Utils.Utils;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;



public class ChangePrivacyAPI {
    public Context context;
    public ResponseListener responseListener;

    public ChangePrivacyAPI(final Context context, final ResponseListener responseListener, final String privacy) {
        this.context = context;
        this.responseListener = responseListener;
        HashMap<String, String> mParams = new HashMap<String, String>();

        Log.print("======ChangeFavoriteAPI====" + mParams);

        ChangeFavoriteRoutAPI apiMethod = Utils.getRetrofit().create(ChangeFavoriteRoutAPI.class);
        Call<ResponseBean> call = apiMethod.getBean(String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)), Pref.getValue(context, Config.PREF_LOCATION, "0,0"), privacy);

        call.enqueue(new Callback<ResponseBean>() {
            @Override
            public void onResponse(Call<ResponseBean> call, Response<ResponseBean> response) {

                if (response.code() == 200) {
                    if (response.body().code == 0) {
                        responseListener.onResponce(Config.TAG_CHANGE_PRIVACY, Config.API_SUCCESS, privacy);
                    } else {
                        responseListener.onResponce(Config.TAG_CHANGE_PRIVACY, Config.API_FAIL, response.body().message);
                    }
                } else {
                    responseListener.onResponce(Config.TAG_CHANGE_PRIVACY, Config.API_FAIL, context.getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(Call<ResponseBean> call, Throwable t) {
                Log.print(t.getMessage());
                responseListener.onResponce(Config.TAG_CHANGE_PRIVACY, Config.API_FAIL, context.getString(R.string.server_error));

            }
        });
    }

    public interface ChangeFavoriteRoutAPI {
        @FormUrlEncoded
        @POST(Config.API_CHANGE_PRIVACY)
        Call<ResponseBean> getBean(@Field("userid") String userid, @Field("location") String location, @Field("privacy") String privacy);

    }
}
