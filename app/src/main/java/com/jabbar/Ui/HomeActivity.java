package com.jabbar.Ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jabbar.MyCamera.MyCameraActivity;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.jabbar.Adapter.StoryAdapter.STORY_CODE;


public class HomeActivity extends BaseActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private FavoriteFragment favoriteFragment;
    private ChatsFragment chatsFragment;
    public static final int CODE_CHAT = 100;
    public Handler handler;
    public Runnable runnable;
    private FloatingActionButton fabButton;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        fabButton = (FloatingActionButton) findViewById(R.id.fabButton);
        setToolbar(toolbar, false);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);

        Log.print("====onCreate HOME ====");

        Log.print("==========" + Utils.getAvatarDir(this));
        Log.print("==========" + Utils.getStoryDir(this));

        Pref.setValue(this, Config.PREF_PUSH_ID, FirebaseInstanceId.getInstance().getToken());
        Log.print("=======PREF_PUSH_ID===========" + FirebaseInstanceId.getInstance().getToken());

        if (chatsFragment == null) {
            chatsFragment = (ChatsFragment) adapter.getItem(0);
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 1 && adapter != null) {
                    fabButton.setImageResource(R.drawable.ic_story);
                    menu.findItem(R.id.menu_refresh).setVisible(true);
                    if (favoriteFragment == null) {
                        favoriteFragment = (FavoriteFragment) adapter.getItem(1);
                    }
                }
                if (position == 0 && adapter != null) {
                    fabButton.setImageResource(R.drawable.ic_chat_white);
                    if (menu != null) {
                        menu.findItem(R.id.menu_refresh).setVisible(false);
                    }
                    if (chatsFragment == null) {
                        chatsFragment = (ChatsFragment) adapter.getItem(0);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0) {
                    startActivity(new Intent(HomeActivity.this, BuddiesActivity.class));
                } else if (viewPager.getCurrentItem() == 1) {
                    startActivityForResult(new Intent(HomeActivity.this, AddStoryActivity.class), STORY_CODE);
                }
            }
        });

        if (getIntent() != null && getIntent().getIntExtra("type", 0) == 2) {
            viewPager.setCurrentItem(0);
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, 1000);

                if (viewPager.getCurrentItem() == 0) {
                    if (chatsFragment != null) {
                        chatsFragment.ListUpdate();
                    }
                } else {
//                    if (favoriteFragment != null) {
//                        favoriteFragment.UpdateFavorite();
//                    }
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler.postDelayed(runnable, 100);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatsFragment(), "CHATS");
        adapter.addFragment(new FavoriteFragment(), "FAVORITE");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.menu_refresh).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;

            case R.id.menu_refresh:
                if (viewPager.getCurrentItem() == 1) {
                    FavoriteFragment.isFirst = true;
                    favoriteFragment.UpdateFavorite();
                }
                break;

            case R.id.menu_new_broadcast:

                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_CHAT && resultCode == RESULT_OK) {
            if (chatsFragment != null) {
                chatsFragment.ListUpdate();
            }
        } else if (requestCode == STORY_CODE && resultCode == RESULT_OK) {
            if (chatsFragment != null) {
                chatsFragment.StoryUpdate();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.print("====onNewIntent HOME ====");

        if (intent != null && intent.getIntExtra("type", 0) == 2) {
            viewPager.setCurrentItem(0);
            if (chatsFragment != null) {
                chatsFragment.ListUpdate();
            }
        }
    }
}