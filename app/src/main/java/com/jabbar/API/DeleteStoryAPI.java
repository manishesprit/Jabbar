package com.jabbar.API;

import android.content.Context;

import com.jabbar.Bean.ResponseBean;
import com.jabbar.Bll.StoryBll;
import com.jabbar.Listener.ResponseListener;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class DeleteStoryAPI {
    public Context context;
    public ResponseListener responseListener;

    public DeleteStoryAPI(final Context context, final ResponseListener responseListener, final int story_id) {
        this.context = context;
        this.responseListener = responseListener;
        HashMap<String, String> mParams = new HashMap<String, String>();

        Log.print("======DeleteStoryAPI====" + mParams);

        DeleteStoryRoutAPI apiMethod = Utils.getRetrofit().create(DeleteStoryRoutAPI.class);
        Call<ResponseBean> call = apiMethod.getBean(String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)), String.valueOf(story_id));

        call.enqueue(new Callback<ResponseBean>() {
            @Override
            public void onResponse(Call<ResponseBean> call, Response<ResponseBean> response) {
                if (response.code() == 200) {
                    if (response.body().code == 0) {

                        new StoryBll(context).DeleteStory(story_id);
                        responseListener.onResponce(Config.TAG_DELETE_STORY, Config.API_SUCCESS, null);
                    } else {
                        responseListener.onResponce(Config.TAG_DELETE_STORY, Config.API_FAIL, response.body().message);
                    }
                } else {
                    responseListener.onResponce(Config.TAG_DELETE_STORY, Config.API_FAIL, context.getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(Call<ResponseBean> call, Throwable t) {
                Log.print(t.getMessage());
                responseListener.onResponce(Config.TAG_DELETE_STORY, Config.API_FAIL, context.getString(R.string.server_error));

            }
        });
    }

    public interface DeleteStoryRoutAPI {
        @FormUrlEncoded
        @POST(Config.API_DELETE_STORY)
        Call<ResponseBean> getBean(@Field("userid") String userid, @Field("story_id") String story_id);

    }
}
