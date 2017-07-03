package com.jabbar.Ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Mydb;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Utils.addActivities(this);
        Utils.getDeviceID(this);


        Mydb mydb = new Mydb(this);
        mydb.Update();

//        Pref.setValue(this, Config.PREF_USERID, 5);
//        Pref.setValue(this, Config.PREF_AVATAR, "avatar_5.jpg");
//        Pref.setValue(this, Config.PREF_MOBILE_NUMBER, "9904841033");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Pref.getValue(SplashActivity.this, Config.PREF_USERID, 0) == 0) {
                    startActivity(new Intent(SplashActivity.this, InputDataActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }
            }
        }, 1500);
    }
}
