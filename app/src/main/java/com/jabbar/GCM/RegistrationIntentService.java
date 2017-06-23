package com.jabbar.GCM;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;

/**
 * Created by hardikjani on 6/23/17.
 */

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
        Log.print("=======RegistrationIntentService========");

    }

    @Override
    public void onHandleIntent(Intent intent) {

        Log.print("=======RegistrationIntentService onHandleIntent========");

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.print("=========token======" + token);
            Pref.setValue(getApplicationContext(), Config.PREF_PUSH_ID, token);
        } catch (Exception e) {
            Log.print("=========Exception======" + e.toString());
        }
    }

}
