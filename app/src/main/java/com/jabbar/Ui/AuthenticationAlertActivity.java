package com.jabbar.Ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Pref;

/**
 * Created by hardikjani on 6/23/17.
 */

public class AuthenticationAlertActivity extends Activity {

    Button btnVerify;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_auth_alert);

        Pref.setValue(this, Config.PREF_USERID, 0);
        Pref.setValue(this, Config.PREF_NAME, "");
        Pref.setValue(this, Config.PREF_MOBILE_NUMBER, "");
        Pref.setValue(this, Config.PREF_AVATAR, "");
        Pref.setValue(this, Config.PREF_STATUS, "");
        Pref.setValue(this, Config.PREF_PRIVACY, "0,0,0");
        Pref.setValue(this, Config.PREF_DB_LEVEL, 0);
        Pref.setValue(this, Config.PREF_UDID, "");
        Pref.setValue(this, Config.PREF_PUSH_ID, "");
        Pref.setValue(this, Config.PREF_LOCATION, "0,0");

        btnVerify = (Button) findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(AuthenticationAlertActivity.this, InputDataActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
