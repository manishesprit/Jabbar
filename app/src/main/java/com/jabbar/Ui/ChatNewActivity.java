package com.jabbar.Ui;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jabbar.API.SendMessageNewAPI;
import com.jabbar.Adapter.ChatAdpater;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bean.MessageBean;
import com.jabbar.Bll.MessageBll;
import com.jabbar.Bll.UserBll;
import com.jabbar.R;
import com.jabbar.Utils.BadgeUtils;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Listener.ResponseListener;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class ChatNewActivity extends BaseActivity implements View.OnClickListener {

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
    private SendMessageNewAPI sendMessageNewAPI;
    private ContactsBean contactsBean;
    public static Activity chatActivity = null;
    private boolean isFirst = true;
    public int lastid = 0;
    private TextView txtNoOfUnreadMsg;
    private int totalUnreadMsg = 0;
    public int totalVisibleMsg;
    public UserBll userBll;
    public Handler handler;
    public Runnable runnable;

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
        try {
            Drawable d = Drawable.createFromStream(getAssets().open(Pref.getValue(this, Config.PREF_WALLPAPER, "wallpaper1.jpg")), null);
            getWindow().setBackgroundDrawable(d);
        } catch (Exception e) {
        }
        Utils.addActivities(this);
        rootView = (LinearLayout) findViewById(R.id.root_view);
        edit_msg = (EmojiconEditText) findViewById(R.id.edit_msg);
        img_emoji = (ImageView) findViewById(R.id.img_emoji);
        rel_send = (RelativeLayout) findViewById(R.id.rel_send);
        imgSend = (ImageView) findViewById(R.id.img_send);
        txtNoOfUnreadMsg = (TextView) findViewById(R.id.txtNoOfUnreadMsg);
        recyclerview_chat = (RecyclerView) findViewById(R.id.recyclerview_chat);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerview_chat.setLayoutManager(linearLayoutManager);

        messageBll = new MessageBll(this);
        userBll = new UserBll(this);
        contactsBean = (ContactsBean) getIntent().getSerializableExtra("data");
        if (contactsBean != null) {

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();
            BadgeUtils.clearBadge(this);

            messageBll.RemoveReadMessage(contactsBean.userid);
            llBuddiesName = (LinearLayout) findViewById(R.id.llBuddiesName);
            txtBuddiesName = (TextView) findViewById(R.id.txtBuddiesName);
            txtLastSeen = (TextView) findViewById(R.id.txtLastSeen);
            conversation_contact_photo = (ImageView) findViewById(R.id.conversation_contact_photo);
            imgBack = (ImageView) findViewById(R.id.imgBack);

            txtBuddiesName.setText(contactsBean.name);
//            txtLastSeen.setText(contactsBean.last_seen);


            Utils.setGlideImage(this, contactsBean.avatar, conversation_contact_photo, true);

            messageBeanArrayList = messageBll.geNewMessageList(contactsBean.userid, lastid);
            chatAdpater = new ChatAdpater(this, messageBeanArrayList);
            recyclerview_chat.setAdapter(chatAdpater);
            recyclerview_chat.smoothScrollToPosition(messageBeanArrayList.size());

            llBuddiesName.setOnClickListener(this);


            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    contactsBean = userBll.getUserDetail(contactsBean.userid);
                    txtLastSeen.setText(contactsBean.last_seen);
                    txtLastSeen.setVisibility(View.VISIBLE);
                    handler.postDelayed(runnable, 15000);
                }
            };

            handler.postDelayed(runnable, 2000);

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

        emojIcon = new EmojIconActions(ChatNewActivity.this, rootView, edit_msg, img_emoji);
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

        recyclerview_chat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                totalVisibleMsg = (visibleItemCount + firstVisibleItemPosition);
                Log.print("=========" + totalVisibleMsg + "=======totalItemCount======" + totalItemCount + "=======firstVisibleItemPosition=======" + firstVisibleItemPosition);

                if (totalVisibleMsg >= totalItemCount && firstVisibleItemPosition >= 0) {
                    Log.print("========INNNNNNNNNNNN=====");
                    txtNoOfUnreadMsg.setVisibility(View.GONE);
                    totalUnreadMsg = 0;
                }
            }
        });

        txtNoOfUnreadMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerview_chat.smoothScrollToPosition(messageBeanArrayList.size());
                txtNoOfUnreadMsg.setVisibility(View.GONE);
                totalUnreadMsg = 0;
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handler != null) {
            handler.removeCallbacks(runnable);
        }

        Utils.trimCache(this);
        Runtime.getRuntime().gc();
        unbindDrawables(rootView);
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
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

                    MessageBean messageBean = new MessageBean();
                    messageBean.userid = Pref.getValue(ChatNewActivity.this, Config.PREF_USERID, 0);
                    messageBean.friendid = contactsBean.userid;
                    messageBean.msg = edit_msg.getText().toString().trim();
                    messageBean.isread = 0;
                    messageBean.id = 0;
                    messageBean.create_time = Config.WebDateFormatter.format(new Date());
                    messageBean.tempId = "" + (System.currentTimeMillis() / 1000);
                    messageBll.InsertNewMessage(messageBean, false);

                    messageBean.create_time = Utils.convertStringDateToStringDate(Config.WebDateFormatter, Config.AppChatDateFormatter, messageBean.create_time);
                    messageBeanArrayList.add(messageBean);
                    chatAdpater.notifyDataSetChanged();
                    recyclerview_chat.smoothScrollToPosition(messageBeanArrayList.size());

                    edit_msg.setText("");

                    if (Utils.isOnline(this)) {
                        String data = messageBll.geUnsendMessageList();
                        if (data != null && !SendMessageNewAPI.isCallAPI)
                            sendMessageNewAPI = new SendMessageNewAPI(this, responseListener, data);
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

            if (tag.equalsIgnoreCase(Config.TAG_SEND_MESSAGE) && result == 0) {
                messageBeanArrayList.clear();
                messageBeanArrayList.addAll(messageBll.geNewMessageList(contactsBean.userid, 0));
                chatAdpater.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        MessageBean messageBean = (MessageBean) intent.getSerializableExtra("messageBean");
        if (messageBean.userid == contactsBean.userid) {
            messageBean.isread = 1;
            messageBll.InsertMessage(messageBean, false);

            messageBean.create_time = Utils.convertStringDateToStringDate(Config.WebDateFormatter, Config.AppChatDateFormatter, messageBean.create_time);
            messageBeanArrayList.add(messageBean);
            chatAdpater.notifyDataSetChanged();
            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.msg_tone);
            mediaPlayer.start();
            ShowUnreadMsgCount();
            recyclerview_chat.smoothScrollToPosition(totalVisibleMsg);

        } else {
            messageBean.isread = 0;
            messageBll.InsertMessage(messageBean, true);
        }
    }

    public void ShowUnreadMsgCount() {
        if (txtNoOfUnreadMsg.getVisibility() == View.GONE) {
            totalUnreadMsg = 1;
            txtNoOfUnreadMsg.setText("" + totalUnreadMsg);
            txtNoOfUnreadMsg.setVisibility(View.VISIBLE);
        } else {
            totalUnreadMsg += 1;
            txtNoOfUnreadMsg.setText("" + totalUnreadMsg);
        }
    }
}
