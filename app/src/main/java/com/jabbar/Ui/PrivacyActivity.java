package com.jabbar.Ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;


public class PrivacyActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private LinearLayout llStatus;
    private TextView txtStatus;
    private LinearLayout llProfile;
    private TextView txtProfile;
    private LinearLayout llLastSeen;
    private TextView txtLastSeen;

    private String[] privacy;

    public int status_privacy = 0;
    public int profile_privacy = 0;
    public int last_seen_privacy = 0;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setToolbar(toolbar, true);

        llStatus = (LinearLayout) findViewById(R.id.llStatus);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        llProfile = (LinearLayout) findViewById(R.id.llProfile);
        txtProfile = (TextView) findViewById(R.id.txtProfile);
        llLastSeen = (LinearLayout) findViewById(R.id.llLastSeen);
        txtLastSeen = (TextView) findViewById(R.id.txtLastSeen);


        privacy = Pref.getValue(this, Config.PREF_PRIVACY, "0,0,0").split(",");

        setPrivacy();
    }

    public void setPrivacy() {
        // status privacy
        if (privacy[0].equalsIgnoreCase("0")) {
            txtStatus.setText("public");
            status_privacy = 0;
        } else if (privacy[0].equalsIgnoreCase("1")) {
            txtStatus.setText("my contact");
            status_privacy = 1;
        } else if (privacy[0].equalsIgnoreCase("2")) {
            txtStatus.setText("no buddie");
            status_privacy = 2;
        }

        // profile privacy
        if (privacy[1].equalsIgnoreCase("0")) {
            txtProfile.setText("public");
            profile_privacy = 0;
        } else if (privacy[1].equalsIgnoreCase("1")) {
            txtProfile.setText("my contact");
            profile_privacy = 1;
        } else if (privacy[1].equalsIgnoreCase("2")) {
            txtProfile.setText("no buddie");
            profile_privacy = 2;
        }

        // last seen privacy
        if (privacy[2].equalsIgnoreCase("0")) {
            txtLastSeen.setText("public");
            last_seen_privacy = 0;
        } else if (privacy[2].equalsIgnoreCase("1")) {
            txtLastSeen.setText("my contact");
            last_seen_privacy = 1;
        } else if (privacy[2].equalsIgnoreCase("2")) {
            txtLastSeen.setText("no buddie");
            last_seen_privacy = 2;
        }
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
            case R.id.llStatus:
                ShowDialog(0, "Status", status_privacy);
                break;

            case R.id.llProfile:
                ShowDialog(1, "Profile", profile_privacy);
                break;

            case R.id.llLastSeen:
                ShowDialog(2, "Last seen", last_seen_privacy);
                break;
        }
    }

    public void ShowDialog(final int privacyType, String Title, final int current_privacy) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_privacy_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
        txtTitle.setText(Title);
        RadioGroup rgprivacy = (RadioGroup) dialog.findViewById(R.id.rgprivacy);
        RadioButton radioButton = (RadioButton) rgprivacy.getChildAt(current_privacy);
        radioButton.setChecked(true);


        rgprivacy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.rbtpublic && current_privacy != 0) {
                    privacy[privacyType] = String.valueOf(0);
                    setPrivacy();
                } else if (checkedId == R.id.rbtmy_contact && current_privacy != 1) {
                    privacy[privacyType] = String.valueOf(1);
                    setPrivacy();
                } else if (checkedId == R.id.rbtno_buddie && current_privacy != 2) {
                    privacy[privacyType] = String.valueOf(2);
                    setPrivacy();
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }


}
