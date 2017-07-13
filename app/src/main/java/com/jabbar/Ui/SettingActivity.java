package com.jabbar.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jabbar.R;
import com.jabbar.Utils.Utils;


public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView txtAccount;
    private TextView txtChat;
    private TextView txtProfile;
    private TextView txtStatus;
    private TextView txtAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtAccount = (TextView) findViewById(R.id.txtAccount);
        txtChat = (TextView) findViewById(R.id.txtChat);
        txtProfile = (TextView) findViewById(R.id.txtProfile);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtAbout = (TextView) findViewById(R.id.txtAbout);

        setToolbar(toolbar, true);

        txtAccount.setOnClickListener(this);
        txtChat.setOnClickListener(this);
        txtProfile.setOnClickListener(this);
        txtStatus.setOnClickListener(this);
        txtAbout.setOnClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtAccount:
                startActivity(new Intent(SettingActivity.this, AccountActivity.class));
                break;

            case R.id.txtChat:
                startActivity(new Intent(SettingActivity.this, ChatSettingActivity.class));
                break;

            case R.id.txtProfile:
                startActivity(new Intent(SettingActivity.this, ChangeAvatarActivity.class));
                break;

            case R.id.txtStatus:
                startActivity(new Intent(SettingActivity.this, StatusActivity.class));
                break;

            case R.id.txtAbout:

                break;
        }
    }

}
