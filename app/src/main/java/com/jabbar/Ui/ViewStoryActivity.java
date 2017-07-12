package com.jabbar.Ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.jabbar.Adapter.StoryFragment;
import com.jabbar.Adapter.StoryFragmentAdapter;
import com.jabbar.Bean.StoryBean;
import com.jabbar.OnNextSlideChange;
import com.jabbar.R;
import com.jabbar.RotateDown;
import com.jabbar.Utils.Log;

import java.util.ArrayList;

/**
 * Created by user on 26/6/17.
 */

public class ViewStoryActivity extends AppCompatActivity implements OnNextSlideChange {
    private Context context;
    private ViewPager vwpImage;
    private StoryFragmentAdapter storyFragmentAdapter;
    private ArrayList<StoryBean> storyBeanArrayList;
    private int pos = 0;
    public boolean isDirect = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);
        context = ViewStoryActivity.this;
        storyBeanArrayList = (ArrayList<StoryBean>) getIntent().getSerializableExtra("userBeanArrayList");
        vwpImage = (ViewPager) findViewById(R.id.vwpImage);
        storyFragmentAdapter = new StoryFragmentAdapter(context, getSupportFragmentManager(), storyBeanArrayList);
        vwpImage.setAdapter(storyFragmentAdapter);
        vwpImage.setPageTransformer(true, new RotateDown());
        pos = getIntent().getIntExtra("pos", 0);
        pos--;
        vwpImage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                System.out.println("====onPageScrolled=====");

            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("=======onpageSelected======" + position);

                StoryFragment fragment = (StoryFragment) storyFragmentAdapter.getFragment(pos);
                if (fragment != null) {
                    fragment.setPush();

                }

                fragment = (StoryFragment) storyFragmentAdapter.getFragment(position);
                if (fragment != null) {
                    fragment.setPlay();

                }
                pos = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                System.out.println("====onPageScrollStateChanged=====");
                if (storyFragmentAdapter != null) {
                    StoryFragment fragment = (StoryFragment) storyFragmentAdapter.getFragment(pos);
                    try {
                        fragment.isPlay = true;
                    } catch (Exception e) {

                    }
                    System.out.println("====Name=====" + fragment.txtName.getText());
                }
            }
        });

        System.out.println("=======onpos=======" + pos);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//                if (pos == 1) {
                StoryFragment fragment = (StoryFragment) storyFragmentAdapter.getFragment(0);
                if (fragment != null) {
                    fragment.setPlay();
                }
//                } else {
//                    vwpImage.setCurrentItem(pos);
//                }

            }
        }, 1000);

    }

    @Override
    public void next() {
        Log.print("=====onpos==== " + pos);
        if ((pos + 1) < storyBeanArrayList.size()) {
            vwpImage.setCurrentItem(pos + 1);
        } else {
            finish();
        }

    }
}