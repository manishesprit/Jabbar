package com.jabbar.API;

import android.content.Context;

import com.jabbar.Bean.ChangeFavoriteBean;
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
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * Created by admin on 10/1/17.
 */

public class ChangeFavoriteAPI {
    public Context context;
    public ResponseListener responseListener;

    public ChangeFavoriteAPI(final Context context, final ResponseListener responseListener, int friendid) {
        this.context = context;
        this.responseListener = responseListener;
        HashMap<String, String> mParams = new HashMap<String, String>();
        mParams.put("userid", String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)));
        mParams.put("friendid", String.valueOf(friendid));
        mParams.put("udid", Pref.getValue(context, Config.PREF_UDID, ""));
        mParams.put("location", Pref.getValue(context, Config.PREF_LOCATION, "0,0"));

        Log.print("======ChangeFavoriteAPI====" + mParams);

        ChangeFavoriteRoutAPI apiMethod = Utils.getRetrofit().create(ChangeFavoriteRoutAPI.class);
        Call<ChangeFavoriteBean> call = apiMethod.getBean( String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)),String.valueOf(friendid),Pref.getValue(context, Config.PREF_UDID, ""), Pref.getValue(context, Config.PREF_LOCATION, "0,0"));

        call.enqueue(new Callback<ChangeFavoriteBean>() {
            @Override
            public void onResponse(Call<ChangeFavoriteBean> call, Response<ChangeFavoriteBean> response) {

                if (response.code() == 200) {
                    if (response.body().code == 0) {
                        responseListener.onResponce(Config.TAG_CHANGE_FAVORITE, Config.API_SUCCESS, response.body().isFavorite);
                    } else {
                        responseListener.onResponce(Config.TAG_CHANGE_FAVORITE, Config.API_FAIL, response.body().message);
                    }
                } else {
                    responseListener.onResponce(Config.TAG_CHANGE_FAVORITE, Config.API_FAIL, context.getString(R.string.server_error));
                }

            }

            @Override
            public void onFailure(Call<ChangeFavoriteBean> call, Throwable t) {
                Log.print(t.getMessage());
                responseListener.onResponce(Config.TAG_CHANGE_FAVORITE, Config.API_FAIL, context.getString(R.string.server_error));

            }
        });


    }

    public interface ChangeFavoriteRoutAPI {
        @FormUrlEncoded
        @POST(Config.API_CHANGE_FAVORITE)
        Call<ChangeFavoriteBean> getBean(@Field("userid") String userid,@Field("friendid") String friendid,@Field("udid") String udid,@Field("location") String location);

    }
}
