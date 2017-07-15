package com.jabbar.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.jabbar.Bean.StoryBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 26/6/17.
 */

public class StoryFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<StoryBean> storyBeanArrayList;
    private StoryFragment storyFragment;
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;

    public StoryFragmentAdapter(Context context, FragmentManager fragmentManager, ArrayList<StoryBean> storyBeanArrayList) {
        super(fragmentManager);
        this.storyBeanArrayList = storyBeanArrayList;
        this.mFragmentManager = fragmentManager;
        mFragmentTags = new HashMap<Integer, String>();
    }

    @Override
    public Fragment getItem(int position) {
        return storyFragment.addNewFragment(storyBeanArrayList.get(position));
    }

    @Override
    public int getCount() {
        return storyBeanArrayList.size();
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
            mFragmentTags.put(position, tag);
        }
        return object;
    }

    public Fragment getFragment(int position) {
        Fragment fragment = null;
        String tag = mFragmentTags.get(position);
        if (tag != null) {
            fragment = mFragmentManager.findFragmentByTag(tag);
        }
        return fragment;
    }
}
