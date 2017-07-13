package com.jabbar.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jabbar.R;
import com.jabbar.Utils.Utils;


public class ChatSettingActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private TextView txtSetWallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_setting);
        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtSetWallpaper = (TextView) findViewById(R.id.txtSetWallpaper);

        setToolbar(toolbar, true);

        txtSetWallpaper.setOnClickListener(this);

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
            case R.id.txtSetWallpaper:
                startActivity(new Intent(ChatSettingActivity.this, SetWallpaperActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.trimCache(this);
    }
}
