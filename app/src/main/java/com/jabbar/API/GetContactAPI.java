package com.jabbar.API;

import android.content.Context;

import com.jabbar.Bean.ContactListBean;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bll.UserBll;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;
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

public class GetContactAPI {
    public Context context;
    public ResponseListener responseListener;

    public GetContactAPI(final Context context, final ResponseListener responseListener, final ArrayList<ContactsBean> contacts_list) {
        this.context = context;
        this.responseListener = responseListener;
        HashMap<String, String> mParams = new HashMap<String, String>();
        mParams.put("userid", String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)));
        mParams.put("udid", Pref.getValue(context, Config.PREF_UDID, ""));
        mParams.put("location", Pref.getValue(context, Config.PREF_LOCATION, "0,0"));
        mParams.put("contacts", ConvertArrayToString(contacts_list));

        Log.print("======GetContactAPI====" + mParams);

        GetContactRoutAPI apiMethod = Utils.getRetrofit().create(GetContactRoutAPI.class);
        Call<ContactListBean> call = apiMethod.getBean(String.valueOf(Pref.getValue(context, Config.PREF_USERID, 0)), Pref.getValue(context, Config.PREF_UDID, ""), Pref.getValue(context, Config.PREF_LOCATION, "0,0"), ConvertArrayToString(contacts_list));

        call.enqueue(new Callback<ContactListBean>() {
            @Override
            public void onResponse(Call<ContactListBean> call, Response<ContactListBean> response) {

                if (response.code() == 200) {
                    if (response.body().code == 0) {
                        if (response.body().buddies_list != null) {
                            new UserBll(context).InsertContact(Utils.getUpdateNameList(response.body().buddies_list, contacts_list));
                            responseListener.onResponce(Config.TAG_GET_CONTACT_LIST, Config.API_SUCCESS, response.body());
                        } else {
                            responseListener.onResponce(Config.TAG_GET_CONTACT_LIST, Config.API_FAIL, context.getString(R.string.no_list_update));
                        }

                    } else {
                        responseListener.onResponce(Config.TAG_GET_CONTACT_LIST, Config.API_FAIL, response.body().message);
                    }
                } else {
                    responseListener.onResponce(Config.TAG_GET_CONTACT_LIST, Config.API_FAIL, context.getString(R.string.server_error));
                }

            }

            @Override
            public void onFailure(Call<ContactListBean> call, Throwable t) {
                Log.print(t.getMessage());
                responseListener.onResponce(Config.TAG_GET_CONTACT_LIST, Config.API_FAIL, context.getString(R.string.server_error));

            }
        });


    }

    public interface GetContactRoutAPI {
        @FormUrlEncoded
        @POST(Config.API_GET_CONTACT_LIST)
        Call<ContactListBean> getBean(@Field("userid") String userid, @Field("udid") String udid, @Field("location") String location, @Field("contacts") String contacts);

    }

    public String ConvertArrayToString(ArrayList<ContactsBean> contactsBeanArrayList) {
        String str = "";
        for (ContactsBean contactsBean : contactsBeanArrayList) {
            str += "," + contactsBean.mobile_number;
        }
        return str.substring(1, str.length());
    }
}