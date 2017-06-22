package com.jabbar.Ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Pref;


public class StatusActivity extends BaseActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerview_status;
    private TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        recyclerview_status = (RecyclerView) findViewById(R.id.recyclerview_status);
        setToolbar(toolbar, true);

        txtStatus.setText(Pref.getValue(this, Config.PREF_STATUS, ""));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}
