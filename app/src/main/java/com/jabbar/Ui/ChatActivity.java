package com.jabbar.Ui;


import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jabbar.Adapter.ChatAdpater;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bean.MessageBean;
import com.jabbar.Bll.MessageBll;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class ChatActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout rootView;
    private RecyclerView recyclerview_chat;
    private EmojiconEditText edit_msg;
    private ImageView img_emoji;
    private RelativeLayout rel_send;
    private ImageView imgSend;
    private LinearLayoutManager linearLayoutManager;
    EmojIconActions emojIcon;
    private TextView action_bar_title_1;
    private ImageView conversation_contact_photo;
    private ImageView imgBack;
    private ChatAdpater chatAdpater;
    private ArrayList<MessageBean> messageBeanArrayList;
    private MessageBll messageBll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Utils.addActivities(this);
        rootView = (LinearLayout) findViewById(R.id.root_view);
        edit_msg = (EmojiconEditText) findViewById(R.id.edit_msg);
        img_emoji = (ImageView) findViewById(R.id.img_emoji);
        rel_send = (RelativeLayout) findViewById(R.id.rel_send);
        imgSend = (ImageView) findViewById(R.id.img_send);

        recyclerview_chat = (RecyclerView) findViewById(R.id.recyclerview_chat);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerview_chat.setLayoutManager(linearLayoutManager);

        messageBll = new MessageBll(this);

        ContactsBean contactsBean = (ContactsBean) getIntent().getSerializableExtra("data");
        if (contactsBean != null) {

            action_bar_title_1 = (TextView) findViewById(R.id.action_bar_title_1);
            conversation_contact_photo = (ImageView) findViewById(R.id.conversation_contact_photo);
            imgBack = (ImageView) findViewById(R.id.imgBack);

            action_bar_title_1.setText(contactsBean.name);

            Glide.with(this).load(Config.AVATAR_HOST + contactsBean.avatar).asBitmap().placeholder(R.drawable.default_user).error(R.drawable.default_user).into(new BitmapImageViewTarget(conversation_contact_photo) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    super.onResourceReady(resource, glideAnimation);
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    conversation_contact_photo.setImageDrawable(circularBitmapDrawable);
                }
            });

            messageBeanArrayList = messageBll.geMessageList(contactsBean.userid);
            chatAdpater = new ChatAdpater(this, messageBeanArrayList);
            recyclerview_chat.setAdapter(chatAdpater);
        } else {
            finish();
        }

        imgBack.setOnClickListener(this);
        rel_send.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        emojIcon = new EmojIconActions(this, rootView, edit_msg, img_emoji);
        emojIcon.setUseSystemEmoji(false);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);

        edit_msg.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (edit_msg.getText().toString().length() > 0) {
                    imgSend.setImageResource(R.drawable.ic_send_black_24dp);
                } else {
                    imgSend.setImageResource(R.drawable.ic_mic_black_24dp);
                }
            }
        });


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
            case R.id.imgBack:
                finish();
                break;

            case R.id.rel_send:
                Log.print("======Msg=====" + edit_msg.getText().toString());
                break;
        }
    }
}
