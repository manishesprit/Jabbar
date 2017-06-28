package com.jabbar.Ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jabbar.Bll.MessageBll;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;


public class StatusActivity extends BaseActivity {

    private Toolbar toolbar;
    private ListView lvStatus;
    private TextView txtStatus;
    public MessageBll messageBll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        lvStatus = (ListView) findViewById(R.id.lvStatus);
        setToolbar(toolbar, true);

        txtStatus.setText(Pref.getValue(this, Config.PREF_STATUS, ""));

        messageBll = new MessageBll(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}
