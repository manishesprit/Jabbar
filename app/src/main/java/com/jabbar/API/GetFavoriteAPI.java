package com.jabbar.API;

import android.content.Context;

import com.jabbar.Bean.FavoriteListBean;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

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

public class GetFavoriteAPI {
    public Context context;
    public ResponseListener responseListener;

    public GetFavoriteAPI(final Context context, final ResponseListener responseListener) {
        this.context = context;
        this.responseListener = responseListener;
        HashMap<String, String> mParams = new HashMap<String, String>();
        mParams.put("userid", String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)));
        mParams.put("location", Pref.getValue(context, Config.PREF_LOCATION, "0,0"));

        Log.print("======GetFavoriteAPI====" + mParams);

        GetFavoriteRoutAPI apiMethod = Utils.getRetrofit().create(GetFavoriteRoutAPI.class);
        Call<FavoriteListBean> call = apiMethod.getBean(String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)), Pref.getValue(context, Config.PREF_LOCATION, "0,0"));

        call.enqueue(new Callback<FavoriteListBean>() {
            @Override
            public void onResponse(Call<FavoriteListBean> call, Response<FavoriteListBean> response) {

                if (response.code() == 200) {
                    if (response.body().code == 0) {
                        responseListener.onResponce(Config.TAG_GET_FAVORITE, Config.API_SUCCESS, response.body());
                    } else {
                        responseListener.onResponce(Config.TAG_GET_FAVORITE, Config.API_FAIL, response.body().message);
                    }
                } else {
                    responseListener.onResponce(Config.TAG_GET_FAVORITE, Config.API_FAIL, context.getString(R.string.server_error));
                }

            }

            @Override
            public void onFailure(Call<FavoriteListBean> call, Throwable t) {
                Log.print(t.getMessage());
                responseListener.onResponce(Config.TAG_GET_FAVORITE, Config.API_FAIL, context.getString(R.string.server_error));

            }
        });


    }

    public interface GetFavoriteRoutAPI {
        @FormUrlEncoded
        @POST(Config.API_GET_FAVORITE)
        Call<FavoriteListBean> getBean(@Field("userid") String userid, @Field("location") String location);

    }
}
