package com.jabbar.Ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jabbar.API.AddStoryAPI;
import com.jabbar.Listener.ResponseListener;
import com.jabbar.MasterCrop.Crop;
import com.jabbar.R;
import com.jabbar.Uc.JabbarDialog;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by hardikjani on 7/6/17.
 */

public class PreviewStoryImage extends BaseActivity {

    private RelativeLayout root_view;
    private ProgressDialog progressDialog;
    private ImageView imgCrop;
    private RelativeLayout rlBack;
    private ImageView conversation_contact_photo;
    private ImageView mImage;
    Bitmap bitmap = null;
    private EmojiconEditText edit_msg;
    private ImageView img_emoji;
    private RelativeLayout rel_send;
    private ImageView imgSend;
    private EmojIconActions emojIcon;
    private String fileName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview_story_image);
        Utils.addActivities(this);
        Bundle args = getIntent().getExtras();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        root_view = (RelativeLayout) findViewById(R.id.root_view);
        mImage = (ImageView) findViewById(R.id.image);
        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        imgCrop = (ImageView) findViewById(R.id.imgCrop);
        conversation_contact_photo = (ImageView) findViewById(R.id.conversation_contact_photo);

        edit_msg = (EmojiconEditText) findViewById(R.id.edit_msg);
        img_emoji = (ImageView) findViewById(R.id.img_emoji);
        rel_send = (RelativeLayout) findViewById(R.id.rel_send);
        imgSend = (ImageView) findViewById(R.id.img_send);

        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnline(PreviewStoryImage.this)) {
                    progressDialog.show();
                    Log.print("====fileName====" + fileName);
                    new AddStoryAPI(PreviewStoryImage.this, fileName, StringEscapeUtils.escapeJava(edit_msg.getText().toString().trim()), new ResponseListener() {
                        @Override
                        public void onResponce(String tag, int result, Object obj) {
                            progressDialog.dismiss();
                            if (tag.equalsIgnoreCase(Config.TAG_ADD_STORY) && result == 0) {
                                setResult(RESULT_OK);
                                finish();

                            } else {
                                new JabbarDialog(PreviewStoryImage.this, obj.toString()).show();
                            }
                        }
                    }).execute();
                } else {
                    new JabbarDialog(PreviewStoryImage.this, getString(R.string.no_internet)).show();
                }
            }
        });

        imgCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.of(Uri.fromFile(new File(fileName)), Uri.fromFile(new File(fileName))).start(PreviewStoryImage.this);
            }
        });
        if (!Pref.getValue(this, Config.PREF_AVATAR, "").equalsIgnoreCase(""))
            Utils.setGlideImage(this, Pref.getValue(this, Config.PREF_AVATAR, ""), conversation_contact_photo);

        emojIcon = new EmojIconActions(this, root_view, edit_msg, img_emoji);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.print("NO" + "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.print("NO" + "Keyboard closed");
            }
        });


        fileName = getIntent().getStringExtra("path");
        bitmap = BitmapFactory.decodeFile(fileName);
        if (bitmap != null) {
            mImage.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_CROP) {
                bitmap = BitmapFactory.decodeFile(fileName);
                if (bitmap != null) {
                    mImage.setImageBitmap(bitmap);
                }
            }
        }
    }
}
