package com.jabbar.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jabbar.R;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.jabbar.Ui.InputDataActivity.PERMISSION_CODE;


public class HomeActivity extends BaseActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private FavoriteFragment favoriteFragment;
    private BuddiesFragment buddiesFragment;

    public static boolean isFavoriteUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        setToolbar(toolbar, false);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        if (favoriteFragment == null) {
            favoriteFragment = (FavoriteFragment) adapter.getItem(0);
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0 && adapter != null) {
                    if (favoriteFragment == null) {
                        favoriteFragment = (FavoriteFragment) adapter.getItem(0);
                    }
                    if (isFavoriteUpdate) {
                        isFavoriteUpdate = false;
                        favoriteFragment.UpdateFavorite(true);
                    }
                }
                if (position == 1 && adapter != null) {
                    if (buddiesFragment == null) {
                        buddiesFragment = (BuddiesFragment) adapter.getItem(1);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FavoriteFragment(), "FAVORITE");
        adapter.addFragment(new BuddiesFragment(), "BUDDIES");
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE && grantResults.length == 2) {
            favoriteFragment.UpdateFavorite(false);
        } else {
            Toast.makeText(this, "Location Permission is required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_profile:
                startActivity(new Intent(this, AccountActivity.class));
                break;

            case R.id.menu_refresh:
                if (viewPager.getCurrentItem() == 0) {
                    favoriteFragment.UpdateFavorite(false);
                } else {
                    buddiesFragment.OnUpdate();
                }
                break;

        }

        return true;
    }
}