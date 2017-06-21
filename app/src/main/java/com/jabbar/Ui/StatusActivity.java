package com.jabbar.Ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.jabbar.R;


public class StatusActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView imgUpdate;
    private RecyclerView recyclerviewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgUpdate = (TextView) findViewById(R.id.imgUpdate);
        recyclerviewStatus = (RecyclerView) findViewById(R.id.recyclerview_status);
        setToolbar(toolbar, true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }


    private EditText getEdtStatus() {
        return (EditText) findViewById(R.id.edtStatus);
    }

    private CheckBox getCheckboxShare() {
        return (CheckBox) findViewById(R.id.checkboxShare);
    }
}
