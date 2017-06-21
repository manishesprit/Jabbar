package com.jabbar.API;

import android.content.Context;

import com.jabbar.Bean.AuthenticationBean;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;


/**
 * Created by admin on 10/1/17.
 */

public class AuthenticationAPI {
    public Context context;
    public ResponseListener responseListener;

    public AuthenticationAPI(final Context context, final ResponseListener responseListener, String mobile_number) {
        this.context = context;
        this.responseListener = responseListener;
        HashMap<String, String> mParams = new HashMap<String, String>();
        mParams.put("mobile_number", mobile_number);
        mParams.put("udid", Pref.getValue(context, Config.PREF_UDID, ""));
        mParams.put("location", Pref.getValue(context, Config.PREF_LOCATION, "0,0"));

        Log.print("======AuthenticationAPI====" + mParams);

        AuthenticationRoutAPI apiMethod = Utils.getRetrofit().create(AuthenticationRoutAPI.class);
        Call<AuthenticationBean> call = apiMethod.getBean(mParams);

        call.enqueue(new Callback<AuthenticationBean>() {
            @Override
            public void onResponse(Call<AuthenticationBean> call, Response<AuthenticationBean> response) {

                if (response.code() == 200) {
                    if (response.body().code == 0) {
                        Pref.setValue(context, Config.PREF_USERID, response.body().userid);
                        Pref.setValue(context, Config.PREF_NAME, response.body().name);
                        Pref.setValue(context, Config.PREF_AVATAR, response.body().avatar);
                        Pref.setValue(context, Config.PREF_STATUS, response.body().status);
                        Pref.setValue(context, Config.PREF_PRIVACY, response.body().privacy);
                        responseListener.onResponce(Config.TAG_AUTHENTICATION, Config.API_SUCCESS, response.body());
                    } else {
                        responseListener.onResponce(Config.TAG_AUTHENTICATION, Config.API_FAIL, response.body().message);
                    }
                } else {
                    responseListener.onResponce(Config.TAG_AUTHENTICATION, Config.API_FAIL, context.getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(Call<AuthenticationBean> call, Throwable t) {
                Log.print(t.getMessage());
                responseListener.onResponce(Config.TAG_AUTHENTICATION, Config.API_FAIL, context.getString(R.string.server_error));

            }
        });


    }

    public interface AuthenticationRoutAPI {
        @POST(Config.API_AUTHENTICATION)
        Call<AuthenticationBean> getBean(@Body Map<String, String> params);

    }
}
