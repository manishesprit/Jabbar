package com.jabbar.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jabbar.R;


public class ChangeAvatarActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ImageView imgAvatar;
    private ImageView imgSelectImage;
    private TextView txtStatus;
    private TextView txtnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        imgSelectImage = (ImageView) findViewById(R.id.imgSelectImage);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        txtnumber = (TextView) findViewById(R.id.txtnumber);


        setToolbar(toolbar, true);

        txtStatus.setOnClickListener(this);
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
        if (v.getId() == R.id.txtStatus) {
            startActivity(new Intent(ChangeAvatarActivity.this, StatusActivity.class));
        }
    }
}
