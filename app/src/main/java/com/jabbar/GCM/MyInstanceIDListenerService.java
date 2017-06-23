package com.jabbar.GCM;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.jabbar.Utils.Log;

public class MyInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {

        Log.print("=======MyInstanceIDListenerService onTokenRefresh========");
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
