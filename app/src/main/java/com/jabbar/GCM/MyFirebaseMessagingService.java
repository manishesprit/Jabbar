package com.jabbar.GCM;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.jabbar.Bean.MessageBean;
import com.jabbar.Bll.MessageBll;
import com.jabbar.Ui.AuthenticationAlertActivity;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import org.json.JSONObject;

/**
 * Created by hardikjani on 6/23/17.
 */

public class MyFirebaseMessagingService extends GcmListenerService {

    private static final String TAG = "MyFirebaseMsgService";
    private Intent intent;
    private String msg = "";
    private int notificationid;

    //egkzOgwkdq8:APA91bEHJ062jtOBbEmr_seKBhAPyWCMUFdVQFjwl1g3q1hSnZ7z3ttz2dXFD-GwEZrp4v7Oi9uLC_huTWN0KCZc_uZz0sstz8Kb2jBy0g3C2kdyku_tHZB5tzgW48LBzY1po61c6OMI

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.print("=========From=========== " + from);
        Log.print("===========data===========" + data.get("data"));
        try {
            JSONObject jsonObject = new JSONObject(data.get("data").toString());
            if (jsonObject.has("type") && jsonObject.getInt("type") == 1 && Pref.getValue(getApplicationContext(), Config.PREF_USERID, 0) != 0) {
                Utils.closeAllScreens();
                Intent intent = new Intent(getApplicationContext(), AuthenticationAlertActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (jsonObject.has("type") && jsonObject.getInt("type") == 2) {
                if (jsonObject.has("userid") && jsonObject.getInt("userid") == Pref.getValue(getApplicationContext(), Config.PREF_USERID, 0)) {

                    JSONObject message = jsonObject.getJSONObject("message");
                    MessageBean messageBean = new MessageBean();
                    messageBean.id = message.getInt("id");
                    messageBean.userid = message.getInt("userid");
                    messageBean.friendid = Pref.getValue(getApplicationContext(), Config.PREF_USERID, 0);
                    messageBean.msg = message.getString("message");
                    messageBean.create_time = message.getString("create_time");
                    new MessageBll(getApplicationContext()).InsertMessage(messageBean);

//                    if (ChatActivity.chatActivity != null) {
//
//                    } else {
//
//                    }

                }
            }
        } catch (Exception e) {
            Log.print("========Exception:======= " + e.toString());
        }

    }
}