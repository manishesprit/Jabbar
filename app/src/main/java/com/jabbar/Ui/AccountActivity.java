package com.jabbar.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jabbar.R;
import com.jabbar.Utils.Utils;


public class AccountActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView txtPrivacy;
    private TextView txtProfile;
    private TextView txtStatus;
    private TextView txtAbout;
//    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtPrivacy = (TextView) findViewById(R.id.txtPrivacy);
        txtProfile = (TextView) findViewById(R.id.txtProfile);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtAbout = (TextView) findViewById(R.id.txtAbout);

        setToolbar(toolbar, true);

        txtPrivacy.setOnClickListener(this);
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
            case R.id.txtPrivacy:
                startActivity(new Intent(AccountActivity.this, PrivacyActivity.class));
                break;

            case R.id.txtProfile:
                startActivity(new Intent(AccountActivity.this, ChangeAvatarActivity.class));
                break;

            case R.id.txtStatus:
                startActivity(new Intent(AccountActivity.this, StatusActivity.class));
                break;

            case R.id.txtAbout:

                break;
        }
    }

}
