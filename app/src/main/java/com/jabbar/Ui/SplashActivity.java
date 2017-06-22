package com.jabbar.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Mydb;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Utils.getDeviceID(this);

        Mydb mydb = new Mydb(this);
        mydb.Update();

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
        }, 2500);
    }
}