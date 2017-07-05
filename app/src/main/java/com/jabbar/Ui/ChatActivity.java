package com.jabbar.Ui;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.jabbar.API.SendMessageAPI;
import com.jabbar.Adapter.ChatAdpater;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bean.MessageBean;
import com.jabbar.Bll.MessageBll;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Mydb;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

import org.apache.commons.lang3.StringEscapeUtils;

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
    private EmojIconActions emojIcon;
    private LinearLayout llBuddiesName;
    private TextView txtBuddiesName;
    private TextView txtLastSeen;
    private ImageView conversation_contact_photo;
    private ImageView imgBack;
    private ChatAdpater chatAdpater;
    private ArrayList<MessageBean> messageBeanArrayList;
    private MessageBll messageBll;
    private SendMessageAPI sendMessageAPI;
    private ProgressDialog progressDialog;
    private ContactsBean contactsBean;
    public static Activity chatActivity = null;

    @Override
    protected void onStart() {
        super.onStart();
        Log.print("=====onStart=====");
        chatActivity = this;
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.print("=====onStop=====");
        chatActivity = null;
    }

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

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending...");
        progressDialog.setCancelable(false);

        messageBll = new MessageBll(this);

        contactsBean = (ContactsBean) getIntent().getSerializableExtra("data");
        if (contactsBean != null) {

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();

            messageBll.RemoveReadMessage(contactsBean.userid);
            llBuddiesName = (LinearLayout) findViewById(R.id.llBuddiesName);
            txtBuddiesName = (TextView) findViewById(R.id.txtBuddiesName);
            txtLastSeen = (TextView) findViewById(R.id.txtLastSeen);
            conversation_contact_photo = (ImageView) findViewById(R.id.conversation_contact_photo);
            imgBack = (ImageView) findViewById(R.id.imgBack);

            txtBuddiesName.setText(contactsBean.name);
            txtLastSeen.setText(contactsBean.last_seen);
            txtLastSeen.setVisibility(View.VISIBLE);

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

            llBuddiesName.setOnClickListener(this);
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

        img_emoji.performClick();
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
                if (edit_msg.getText().toString().trim().length() > 0) {
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
                if (!edit_msg.getText().toString().trim().equalsIgnoreCase("")) {
                    if (Utils.isOnline(this)) {
                        Log.print("======Original Msg=====" + edit_msg.getText().toString());
                        Log.print("======Convert Msg=====" + Mydb.getDBStr(StringEscapeUtils.escapeJava(edit_msg.getText().toString().trim())));
                        progressDialog.show();
                        sendMessageAPI = new SendMessageAPI(this, responseListener, contactsBean.userid, Mydb.getDBStr(StringEscapeUtils.escapeJava(edit_msg.getText().toString().trim())));
                    }
                }
                break;

            case R.id.llBuddiesName:
                startActivity(new Intent(this, ProfileActivity.class).putExtra("data", contactsBean));
                break;
        }
    }

    public ResponseListener responseListener = new ResponseListener() {
        @Override
        public void onResponce(String tag, int result, Object obj) {

            progressDialog.dismiss();
            if (tag.equalsIgnoreCase(Config.TAG_SEND_MESSAGE) && result == 0) {
                MessageBean messageBean = (MessageBean) obj;
                messageBean.userid = Pref.getValue(ChatActivity.this, Config.PREF_USERID, 0);
                messageBean.friendid = contactsBean.userid;
                messageBean.msg = edit_msg.getText().toString().trim();
                messageBean.isread = 1;
                messageBll.InsertMessage(messageBean, false);
                messageBeanArrayList.add(messageBean);
                chatAdpater.notifyDataSetChanged();
            }
            edit_msg.setText("");
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        MessageBean messageBean = (MessageBean) intent.getSerializableExtra("messageBean");
        if (messageBean.userid == contactsBean.userid) {
            messageBean.isread = 1;
            messageBll.InsertMessage(messageBean, false);

            messageBeanArrayList.clear();
            messageBeanArrayList.addAll(messageBll.geMessageList(contactsBean.userid));
            chatAdpater.notifyDataSetChanged();
        } else {
            messageBean.isread = 0;
            messageBll.InsertMessage(messageBean, true);
        }
    }
}
