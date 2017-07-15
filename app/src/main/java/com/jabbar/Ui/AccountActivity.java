package com.jabbar.Ui;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jabbar.R;
import com.jabbar.Utils.BadgeUtils;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Mydb;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import static com.jabbar.R.id.txtAbout;


public class AccountActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView txtPrivacy;
    private TextView txtChange_number;
    private TextView txtRemove_account;
    //    private GridLayout gridLayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtPrivacy = (TextView) findViewById(R.id.txtPrivacy);
        txtChange_number = (TextView) findViewById(R.id.txtChange_number);
        txtRemove_account = (TextView) findViewById(R.id.txtRemove_account);

        setToolbar(toolbar, true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        txtPrivacy.setOnClickListener(this);
        txtChange_number.setOnClickListener(this);
        txtRemove_account.setOnClickListener(this);

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

            case R.id.txtChange_number:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to change number?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        progressDialog.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Pref.setValue(AccountActivity.this, Config.PREF_USERID, 0);
                                Pref.setValue(AccountActivity.this, Config.PREF_NAME, "");
                                Pref.setValue(AccountActivity.this, Config.PREF_MOBILE_NUMBER, "");
                                Pref.setValue(AccountActivity.this, Config.PREF_AVATAR, "");
                                Pref.setValue(AccountActivity.this, Config.PREF_STATUS, "");
                                Pref.setValue(AccountActivity.this, Config.PREF_PRIVACY, "0,0,0,0");
                                Pref.setValue(AccountActivity.this, Config.PREF_UDID, "");
                                Pref.setValue(AccountActivity.this, Config.PREF_PUSH_ID, "");
                                Pref.setValue(AccountActivity.this, Config.PREF_LOCATION, "0,0");

                                Mydb mydb = new Mydb(AccountActivity.this);
                                mydb.execute("delete from message_tb");
                                mydb.execute("delete from user_tb");
                                mydb.execute("delete from story");
                                mydb.close();

                                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.cancelAll();
                                BadgeUtils.clearBadge(AccountActivity.this);

                                progressDialog.dismiss();
                                Utils.closeAllScreens();
                                finish();
                                startActivity(new Intent(AccountActivity.this, InputDataActivity.class));
                            }
                        }, 3000);
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();

                break;

            case R.id.txtRemove_account:
//                startActivity(new Intent(AccountActivity.this, StatusActivity.class));
                break;

            case txtAbout:

                break;
        }
    }

}
