package com.jabbar.API;

import android.content.Context;

import com.google.gson.Gson;
import com.jabbar.Bean.ContactListBean;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bll.UserBll;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class GetOnlineAPI {
    public Context context;
    public static boolean isOnlineCall = false;

    public GetOnlineAPI(final Context context) {
        this.context = context;
        isOnlineCall = true;
        Log.print("======GetOnlineAPI====");

        GetOnlineRoutAPI apiMethod = Utils.getRetrofit().create(GetOnlineRoutAPI.class);
        Call<ContactListBean> call = apiMethod.getBean(String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)), Pref.getValue(context, Config.PREF_LOCATION, "0,0"));

        call.enqueue(new Callback<ContactListBean>() {
            @Override
            public void onResponse(Call<ContactListBean> call, Response<ContactListBean> response) {

                Log.print("====onResponse====response.code()=" + response.code() + "=====url=====" + call.request().url());
                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response).toString()).getJSONObject("body");
                    Log.print("=====jsonObject=====" + jsonObject.toString());
                } catch (Exception e) {
                }

                isOnlineCall = false;
                if (response.code() == 200) {
                    if (response.body().code == 0) {
                        if (response.body().buddies_list != null && response.body().buddies_list.size() > 0) {
                            UserBll userBll = new UserBll(context);
                            for (ContactsBean contactsBean : response.body().buddies_list) {
                                userBll.updateContactOnline(contactsBean);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ContactListBean> call, Throwable t) {
                isOnlineCall = false;
                Log.print(t.getMessage());
            }
        });
    }

    public interface GetOnlineRoutAPI {
        @FormUrlEncoded
        @POST(Config.API_GET_ONLINE)
        Call<ContactListBean> getBean(@Field("userid") String userid, @Field("location") String location);

    }
}
