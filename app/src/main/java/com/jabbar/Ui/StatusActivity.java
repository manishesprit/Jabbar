package com.jabbar.Ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jabbar.API.UpdateStatusAPI;
import com.jabbar.Bll.MessageBll;
import com.jabbar.Bll.StatusBll;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.JabbarDialog;
import com.jabbar.Utils.Mydb;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


public class StatusActivity extends BaseActivity {

    private Toolbar toolbar;
    private ListView lvStatus;
    private EmojiconTextView txtStatus;
    public StatusBll statusBll;
    public ArrayAdapter<String> statusArrayAdapter;
    public ArrayList<String> defaultStatusList;
    public static final int CHANGE_STATUS_CODE = 500;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtStatus = (EmojiconTextView) findViewById(R.id.txtStatus);
        lvStatus = (ListView) findViewById(R.id.lvStatus);
        setToolbar(toolbar, true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        txtStatus.setText(Pref.getValue(this, Config.PREF_STATUS, ""));

        statusBll = new StatusBll(this);
        defaultStatusList = statusBll.getDefaultStatusList();
        statusArrayAdapter = new ArrayAdapter<String>(this, R.layout.row_status, defaultStatusList);
        lvStatus.setAdapter(statusArrayAdapter);

        txtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(StatusActivity.this, AddStatusActivity.class), CHANGE_STATUS_CODE);
            }
        });

        lvStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (!txtStatus.getText().toString().trim().equalsIgnoreCase(defaultStatusList.get(position))) {
                    if (Utils.isOnline(StatusActivity.this)) {
                        if (!progressDialog.isShowing())
                            progressDialog.show();
                        new UpdateStatusAPI(StatusActivity.this, new ResponseListener() {
                            @Override
                            public void onResponce(String tag, int result, Object obj) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                if (tag.equalsIgnoreCase(Config.TAG_UPDATE_STATUS) && result == 0) {
                                    Pref.setValue(StatusActivity.this, Config.PREF_STATUS, defaultStatusList.get(position));
                                    txtStatus.setText(defaultStatusList.get(position));
                                    setResult(RESULT_OK);
                                } else {
                                    new JabbarDialog(StatusActivity.this, obj.toString()).show();
                                }

                            }
                        }, Mydb.getDBStr(StringEscapeUtils.escapeJava(defaultStatusList.get(position))));
                    } else {
                        new JabbarDialog(StatusActivity.this, getString(R.string.no_internet)).show();
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHANGE_STATUS_CODE && resultCode == RESULT_OK) {
            txtStatus.setText(Pref.getValue(this, Config.PREF_STATUS, ""));
            statusBll.insertStatus(txtStatus.getText().toString());
            defaultStatusList.add(0, txtStatus.getText().toString());
            statusArrayAdapter.notifyDataSetChanged();
            setResult(RESULT_OK);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}
