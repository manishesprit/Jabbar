package com.jabbar.API;

import android.content.Context;

import com.google.gson.Gson;
import com.jabbar.Bean.StoryListBean;
import com.jabbar.Bll.StoryBll;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Listener.ResponseListener;
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

public class GetStoryAPI {
    public Context context;
    public ResponseListener responseListener;
    public StoryBll storyBll;

    public GetStoryAPI(final Context context, final ResponseListener responseListener) {
        this.context = context;
        this.responseListener = responseListener;

        GetStoryRoutAPI apiMethod = Utils.getRetrofit().create(GetStoryRoutAPI.class);
        Call<StoryListBean> call = apiMethod.getBean(String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)), Pref.getValue(context, Config.PREF_LOCATION, "0,0"));

        call.enqueue(new Callback<StoryListBean>() {
            @Override
            public void onResponse(Call<StoryListBean> call, Response<StoryListBean> response) {

                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response).toString()).getJSONObject("body");
                    Log.print("=====jsonObject=====" + jsonObject.toString());
                } catch (Exception e) {
                }

                if (response.code() == 200) {
                    if (response.body().code == 0) {
                        if (response.body().story_list != null) {
                            storyBll = new StoryBll(context);
                            for (int i = 0; i < response.body().story_list.size(); i++) {
                                storyBll.verify(response.body().story_list.get(i));
                            }
                            responseListener.onResponce(Config.TAG_GET_STORY_LIST, Config.API_SUCCESS, response.body());
                        } else {
                            responseListener.onResponce(Config.TAG_GET_STORY_LIST, Config.API_FAIL, context.getString(R.string.no_list_update));
                        }

                    } else {
                        responseListener.onResponce(Config.TAG_GET_STORY_LIST, Config.API_FAIL, response.body().message);
                    }
                } else {
                    responseListener.onResponce(Config.TAG_GET_STORY_LIST, Config.API_FAIL, context.getString(R.string.server_error));
                }

            }

            @Override
            public void onFailure(Call<StoryListBean> call, Throwable t) {
                Log.print(t.getMessage());
                responseListener.onResponce(Config.TAG_GET_STORY_LIST, Config.API_FAIL, context.getString(R.string.server_error));

            }
        });


    }

    public interface GetStoryRoutAPI {
        @FormUrlEncoded
        @POST(Config.API_GET_STORY_LIST)
        Call<StoryListBean> getBean(@Field("userid") String userid, @Field("location") String location);

    }

}
