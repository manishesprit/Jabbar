package com.jabbar.Ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jabbar.API.UpdateStatusAPI;
import com.jabbar.Listener.ResponseListener;
import com.jabbar.R;
import com.jabbar.Uc.JabbarDialog;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Mydb;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import org.apache.commons.lang3.StringEscapeUtils;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class AddStatusActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private LinearLayout rootView;
    private EmojiconEditText edit_msg;
    private ImageView img_emoji;
    private TextView txtCanel;
    private TextView txtOk;
    private EmojIconActions emojIcon;
    private ProgressDialog progressDialog;
    private TextView txtCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_status);
        Utils.addActivities(this);

        rootView = (LinearLayout) findViewById(R.id.root_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        edit_msg = (EmojiconEditText) findViewById(R.id.edit_msg);
        img_emoji = (ImageView) findViewById(R.id.img_emoji);
        txtCanel = (TextView) findViewById(R.id.txtCanel);
        txtOk = (TextView) findViewById(R.id.txtOk);
        txtCounter = (TextView) findViewById(R.id.txtCounter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        setToolbar(toolbar, true);

        emojIcon = new EmojIconActions(this, rootView, edit_msg, img_emoji);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("NO", "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("NO", "Keyboard closed");
            }
        });

        edit_msg.setText(Pref.getValue(this, Config.PREF_STATUS, ""));
        edit_msg.setSelection(edit_msg.getText().length());

        txtCounter.setText(edit_msg.getText().toString().length() + "/100");

        edit_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtCounter.setText(edit_msg.getText().toString().length() + "/100");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        if(edit_msg.getText().toString().length()>100){
//            edit_msg.setText(edit_msg);
//        }

        txtCanel.setOnClickListener(this);
        txtOk.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtCanel:
                finish();
                break;

            case R.id.txtOk:
                if (!edit_msg.getText().toString().trim().equalsIgnoreCase(Pref.getValue(this, Config.PREF_STATUS, ""))) {
                    if (!edit_msg.getText().toString().trim().equalsIgnoreCase("")) {
                        if (Utils.isOnline(this)) {
                            if (!progressDialog.isShowing())
                                progressDialog.show();
                            new UpdateStatusAPI(this, new ResponseListener() {
                                @Override
                                public void onResponce(String tag, int result, Object obj) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    if (tag.equalsIgnoreCase(Config.TAG_UPDATE_STATUS) && result == 0) {
                                        Pref.setValue(AddStatusActivity.this, Config.PREF_STATUS, edit_msg.getText().toString().trim());
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        new JabbarDialog(AddStatusActivity.this, obj.toString()).show();
                                    }

                                }
                            }, Mydb.getDBStr(Mydb.getDBStr(StringEscapeUtils.escapeJava(edit_msg.getText().toString().trim()))));
                        } else {
                            new JabbarDialog(AddStatusActivity.this, getString(R.string.no_internet)).show();
                        }
                    }
                } else {
                    finish();
                }
                break;
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
