package com.jabbar.API;

import android.content.Context;

import com.google.gson.Gson;
import com.jabbar.Bean.AuthenticationBean;
import com.jabbar.Bean.ExitsContactBean;
import com.jabbar.Bll.UserBll;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Listener.ResponseListener;
import com.jabbar.Utils.Utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import java.util.ArrayList;
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

public class AuthenticationAPI {

    public AuthenticationAPI(final Context context, final ResponseListener responseListener, final String mobile_number, final ArrayList<ExitsContactBean> exitsContactBeanArrayList) {
        HashMap<String, String> mParams = new HashMap<String, String>();
        mParams.put("mobile_number", mobile_number);
        mParams.put("udid", Pref.getValue(context, Config.PREF_UDID, ""));
        mParams.put("location", Pref.getValue(context, Config.PREF_LOCATION, "0,0"));
        mParams.put("pushid", Pref.getValue(context, Config.PREF_PUSH_ID, ""));
        mParams.put("contacts", ConvertArrayToString(exitsContactBeanArrayList, mobile_number));

        Log.print("======AuthenticationAPI====" + mParams);

        AuthenticationRoutAPI apiMethod = Utils.getRetrofit().create(AuthenticationRoutAPI.class);
        Call<AuthenticationBean> call = apiMethod.getBean(mobile_number, Pref.getValue(context, Config.PREF_UDID, ""), Pref.getValue(context, Config.PREF_LOCATION, "0,0"), Pref.getValue(context, Config.PREF_PUSH_ID, ""), ConvertArrayToString(exitsContactBeanArrayList, mobile_number));

        call.enqueue(new Callback<AuthenticationBean>() {
            @Override
            public void onResponse(Call<AuthenticationBean> call, Response<AuthenticationBean> response) {

                Log.print("====onResponse====response.code()=" + response.code() + "=====url=====" + call.request().url());
                try {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(response).toString()).getJSONObject("body");
                    Log.print("=====jsonObject=====" + jsonObject.toString());
                } catch (Exception e) {
                }

                if (response.code() == 200) {
                    if (response.body().code == 0) {
                        Pref.setValue(context, Config.PREF_USERID, response.body().userid);
                        Pref.setValue(context, Config.PREF_NAME, response.body().name);
                        Pref.setValue(context, Config.PREF_MOBILE_NUMBER, mobile_number);
                        Pref.setValue(context, Config.PREF_AVATAR, response.body().avatar);
                        Pref.setValue(context, Config.PREF_STATUS, StringEscapeUtils.unescapeJava(response.body().status));
                        Pref.setValue(context, Config.PREF_PRIVACY, response.body().privacy);
                        if (response.body().buddies_list != null) {
                            new UserBll(context).InsertContact(Utils.getUpdateNameList(response.body().buddies_list, exitsContactBeanArrayList));
                        }
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
                Log.print("=========" + t.getMessage());
                responseListener.onResponce(Config.TAG_AUTHENTICATION, Config.API_FAIL, context.getString(R.string.server_error));

            }
        });


    }

    public interface AuthenticationRoutAPI {
        @FormUrlEncoded
        @POST(Config.API_AUTHENTICATION)
        Call<AuthenticationBean> getBean(@Field("mobile_number") String mobile_number, @Field("udid") String udid, @Field("location") String location, @Field("pushid") String pushid, @Field("contacts") String contacts);

    }

    public String ConvertArrayToString(ArrayList<ExitsContactBean> exitsContactBeanArrayList, String mobile_number) {
        String str = "";

        for (ExitsContactBean exitsContactBean : exitsContactBeanArrayList) {
            if (!mobile_number.equalsIgnoreCase(exitsContactBean.mobile_number)) {
                str += "," + exitsContactBean.mobile_number;
            }
        }
        return str.substring(1, str.length());
    }
}
