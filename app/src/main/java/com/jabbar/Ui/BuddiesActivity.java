package com.jabbar.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.jabbar.API.GetContactAPI;
import com.jabbar.Adapter.BuddiesAdapter;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bean.ExitsContactBean;
import com.jabbar.Bll.UserBll;
import com.jabbar.Listener.MyClickListener;
import com.jabbar.Listener.ResponseListener;
import com.jabbar.R;
import com.jabbar.Uc.JabbarDialog;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.GetLocation;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.UpdateContact1;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;


public class BuddiesActivity extends BaseActivity implements GetLocation.MyLocationListener, ResponseListener {

    private Toolbar toolbar;
    public ArrayList<ContactsBean> contactsBeanArrayList;

    private RecyclerView rlFriendList;
    private BuddiesAdapter buddiesAdapter;
    private LinearLayoutManager mLayoutManager;

    private ProgressBar progress_refresh;
    private GetLocation getLocation;

    private Handler handler;
    private Runnable runnable;
    int Attempt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddies);
        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setToolbar(toolbar, true);

        progress_refresh = (ProgressBar) findViewById(R.id.progress_refresh);
        rlFriendList = (RecyclerView) findViewById(R.id.rlFriendList);
        rlFriendList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        rlFriendList.setLayoutManager(mLayoutManager);
        contactsBeanArrayList = new UserBll(this).geBuddiestList();
        buddiesAdapter = new BuddiesAdapter(this, contactsBeanArrayList, myClickListener);
        rlFriendList.setAdapter(buddiesAdapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;

            case R.id.menu_refresh:
                getLocation = new GetLocation(this, this);
                getLocation.UpdateLocation();
                break;

            case R.id.menu_new_broadcast:

                break;

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.menu_new_broadcast).setVisible(false);
        return true;
    }


    public MyClickListener myClickListener = new MyClickListener() {
        @Override
        public void onClick(int pos) {
            new UserBll(BuddiesActivity.this).updateFavoriteContact(contactsBeanArrayList.get(pos).userid, contactsBeanArrayList.get(pos).isFavorite == 0 ? 1 : 0);
            contactsBeanArrayList.get(pos).isFavorite = contactsBeanArrayList.get(pos).isFavorite == 0 ? 1 : 0;
            buddiesAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void getLoc(boolean isUpdate) {
        if (progress_refresh.getVisibility() == View.GONE) {
            progress_refresh.setVisibility(View.VISIBLE);
            if (UpdateContact1.SyncOn == false) {
                if (Utils.isOnline(this)) {
                    ArrayList<ExitsContactBean> exitsContactBeanArrayList = Pref.getArrayValue(this, Config.PREF_CONTACT, new ArrayList<ExitsContactBean>());
                    if (exitsContactBeanArrayList != null && exitsContactBeanArrayList.size() > 0) {
                        new GetContactAPI(this, this, exitsContactBeanArrayList);
                    } else {
                        progress_refresh.setVisibility(View.GONE);
                        new JabbarDialog(this, getString(R.string.no_contact)).show();
                    }

                } else {
                    progress_refresh.setVisibility(View.GONE);
                    new JabbarDialog(this, getString(R.string.no_internet)).show();
                }
            } else {
                Attempt = 0;
                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {

                        if (UpdateContact1.SyncOn == false) {
                            handler.removeCallbacks(runnable);
                            Attempt = 0;
                            if (Utils.isOnline(BuddiesActivity.this)) {
                                ArrayList<ExitsContactBean> exitsContactBeanArrayList = Pref.getArrayValue(BuddiesActivity.this, Config.PREF_CONTACT, new ArrayList<ExitsContactBean>());
                                if (exitsContactBeanArrayList != null && exitsContactBeanArrayList.size() > 0) {
                                    new GetContactAPI(BuddiesActivity.this, BuddiesActivity.this, exitsContactBeanArrayList);
                                } else {
                                    progress_refresh.setVisibility(View.GONE);
                                    new JabbarDialog(BuddiesActivity.this, getString(R.string.no_contact)).show();
                                }
                            } else {
                                progress_refresh.setVisibility(View.GONE);
                                new JabbarDialog(BuddiesActivity.this, getString(R.string.no_internet)).show();
                            }

                        } else {
                            if (Attempt < 20) {
                                Attempt++;
                                handler.postDelayed(runnable, 1000);
                            } else {
                                handler.removeCallbacks(runnable);
                                Attempt = 0;
                                new JabbarDialog(BuddiesActivity.this, getString(R.string.sync_fail)).show();
                                progress_refresh.setVisibility(View.GONE);
                            }
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        }
    }

    @Override
    public void onResponce(String tag, int result, Object obj) {
        progress_refresh.setVisibility(View.GONE);

        if (tag.equalsIgnoreCase(Config.TAG_GET_CONTACT_LIST) && result == 0) {
            contactsBeanArrayList.clear();
            contactsBeanArrayList.addAll(new UserBll(this).geBuddiestList());
            buddiesAdapter.notifyDataSetChanged();
        } else {
            new JabbarDialog(this, obj.toString()).show();
        }
    }

}
