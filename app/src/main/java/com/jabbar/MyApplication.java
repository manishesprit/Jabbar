package com.jabbar;

import android.app.Application;
import android.os.Handler;

import com.jabbar.API.GetOnlineAPI;
import com.jabbar.API.SendMessageNewAPI;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

/**
 * Created by hardikjani on 7/13/17.
 */

public class MyApplication extends Application implements Foreground.Listener {

    public Handler handler;
    public Runnable runnable;

    public static boolean isAppRuning = false;

    @Override
    public void onCreate() {
        super.onCreate();

        Foreground.init(this).addListener(this);


    }

    public void CallHandler(int time) {
        if (handler != null && runnable != null) {
            handler.postDelayed(runnable, time);
        } else {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    CallHandler(60000);
                    if (Utils.isOnline(getApplicationContext()) && Pref.getValue(getApplicationContext(), Config.PREF_USERID, 0) != 0) {
                        if (GetOnlineAPI.isOnlineCall == false && SendMessageNewAPI.isCallAPI == false) {
                            new GetOnlineAPI(getApplicationContext());
                        }
                    }
                }
            };

            handler.postDelayed(runnable, time);
        }
    }

    @Override
    public void onBecameForeground() {
        Log.print("====onBecameForeground====");
        isAppRuning = true;
        CallHandler(2000);

    }

    @Override
    public void onBecameBackground() {
        Log.print("====onBecameBackground====");
        isAppRuning = false;
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
