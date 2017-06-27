package com.jabbar.GCM;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.jabbar.Ui.AuthenticationAlertActivity;
import com.jabbar.Utils.Log;
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
        String message = data.getString("message");
        Log.print("=========From=========== " + from);


        Log.print("===========data===========" + data.get("data"));
        try {
            JSONObject jsonObject = new JSONObject(data.get("data").toString());
            if (jsonObject.has("type") && jsonObject.getInt("type") == 1) {
                Utils.closeAllScreens();
                Intent intent = new Intent(getApplicationContext(), AuthenticationAlertActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (jsonObject.has("type") && jsonObject.getInt("type") == 2) {

            }
        } catch (Exception e) {
            Log.print("========Exception:======= " + e.toString());
        }

    }
}