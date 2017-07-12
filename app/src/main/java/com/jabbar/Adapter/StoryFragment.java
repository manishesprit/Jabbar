package com.jabbar.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.jabbar.Bean.StoryBean;
import com.jabbar.Bll.StoryBll;
import com.jabbar.OnNextSlideChange;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by user on 26/6/17.
 */

public class StoryFragment extends Fragment {

    private View view;

    private RelativeLayout rlMain;
    private LinearLayout llstoryProgress;
    private ImageView imgAvatar;
    public TextView txtName;
    private TextView txtTime;

    private ImageView imgStoryPic;
    private EmojiconTextView txtCaption;

    private StoryBean storyBean;
    private OnNextSlideChange onNextSlideChange;
    private ProgressBar progress;
    private ArrayList<StoryBean> storyBeanArrayList;
    private ProgressBar progressBar;
    public int pos = 0;
    private Handler handler;
    private Runnable runnable;
    private int width;
    public boolean isPlay = true;

    public static StoryFragment addNewFragment(StoryBean storyBean) {

        System.out.println("=====StatusFragment======" + storyBean.userid);
        StoryFragment viewImageFragment = new StoryFragment();
        viewImageFragment.storyBean = storyBean;

        return viewImageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("=====onCreateView======" + storyBean.userid);
        view = inflater.inflate(R.layout.fragment_story, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        System.out.println("=====onActivityCreated======" + storyBean.userid);
        rlMain = (RelativeLayout) view.findViewById(R.id.rlMain);
        llstoryProgress = (LinearLayout) view.findViewById(R.id.llstatusProgress);
        imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
        txtName = (TextView) view.findViewById(R.id.txtName);
        txtTime = (TextView) view.findViewById(R.id.txtTime);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        imgStoryPic = (ImageView) view.findViewById(R.id.imgStatusPic);
        txtCaption = (EmojiconTextView) view.findViewById(R.id.txtCaption);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;

        rlMain.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    System.out.println("=======ACTION_UP=======");
                    isPlay = true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    System.out.println("========ACTION_DOWN=====");
                    isPlay = false;
                }

                return true;
            }
        });

        storyBeanArrayList = new StoryBll(getContext()).getStoryListByUserId(storyBean.userid);


        txtName.setText(storyBean.userName);

        Utils.setGlideImage(getContext(), storyBean.userAvatar, imgAvatar, true);

        imgStoryPic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    System.out.println("======ACTION_UP======");
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    System.out.println("======ACTION_DOWN======");
                }
                return false;
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();

        System.out.println("=====onDetach======" + storyBean.userid);

        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);

        System.out.println("=====onAttachFragment======" + storyBean.userid);
    }

    public void setPush() {
        if (llstoryProgress != null) {
            llstoryProgress.removeAllViews();
        }

        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

    public void setPlay() {

        isPlay = true;
        System.out.println("==========setPlay==========");
//        if (handler != null) {
//            handler.removeCallbacks(runnable);
//            handler = null;
//        }

        System.out.println("========statusBeanArrayList========" + storyBeanArrayList);
        if (storyBeanArrayList != null && llstoryProgress != null) {

            System.out.println("====statusBeanArrayList======" + storyBeanArrayList.size() + "====" + storyBean.userid);
            for (int i = 0; i < storyBeanArrayList.size(); i++) {
                ProgressBar progressBar = (ProgressBar) LayoutInflater.from(getContext()).inflate(R.layout.row_progress, null);
                progressBar.setProgress(0);
                LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams((width / storyBeanArrayList.size()), 15);
                progressBar.setLayoutParams(layoutParams);
                progressBar.setPadding(5, 3, 5, 3);
                llstoryProgress.addView(progressBar);
            }

            handler = new Handler();

            runnable = new Runnable() {

                @Override
                public void run() {

                    if (isPlay) {
                        if (llstoryProgress != null) {
                            if (progressBar.getProgress() < 100) {
                                progressBar.setProgress(progressBar.getProgress() + 1);
                                handler.postDelayed(runnable, 40);
                            } else {
                                handler.removeCallbacks(runnable);
                                pos++;
                                if (pos < storyBeanArrayList.size()) {
                                    progressBar = (ProgressBar) llstoryProgress.getChildAt(pos);
                                    progressBar.setProgress(0);

                                    try {
                                        txtTime.setText(storyBeanArrayList.get(pos).create_time);
                                        txtCaption.setText(storyBeanArrayList.get(pos).caption);
                                        System.out.println("============statusBeanArrayList.get(pos).statusPic=========" + storyBeanArrayList.get(pos).story_image);
                                        Glide.with(getContext()).load(Config.STORY_HOST + storyBeanArrayList.get(pos).story_image).asBitmap().placeholder(R.drawable.ic_gallery).into(new ImageViewTarget<Bitmap>(imgStoryPic) {
                                            @Override
                                            protected void setResource(Bitmap resource) {
                                                progress.setVisibility(View.GONE);
                                                imgStoryPic.setImageBitmap(resource);
                                                if (handler != null) {
                                                    handler.postDelayed(runnable, 40);
                                                } else {
                                                    System.out.println("========handler != null======");
                                                }
                                            }

                                            @Override
                                            public void onLoadStarted(Drawable placeholder) {
                                                super.onLoadStarted(placeholder);
                                                progress.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    } catch (Exception e) {
                                    }

                                } else {
                                    if (onNextSlideChange != null) {
                                        pos = 0;
                                        onNextSlideChange.next();
                                    }
                                }
                            }
                        }
                    } else {
                        handler.postDelayed(runnable, 40);
                    }
                }
            };

            pos = 0;
            progressBar = (ProgressBar) llstoryProgress.getChildAt(pos);
            progressBar.setProgress(0);
            try {
                txtTime.setText(storyBeanArrayList.get(pos).create_time);
                txtCaption.setText(storyBeanArrayList.get(pos).caption);
                System.out.println("============statusBeanArrayList.get(pos).statusPic=========" + storyBeanArrayList.get(pos).story_image);
                Glide.with(getContext()).load(Config.STORY_HOST + storyBeanArrayList.get(pos).story_image).asBitmap().placeholder(R.drawable.ic_gallery).into(new ImageViewTarget<Bitmap>(imgStoryPic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        progress.setVisibility(View.GONE);
                        imgStoryPic.setImageBitmap(resource);
                        if (handler != null) {
                            handler.postDelayed(runnable, 40);
                        } else {
                            System.out.println("========handler != null======");
                        }
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        progress.setVisibility(View.VISIBLE);
                    }
                });

            } catch (Exception e) {
            }


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.print("====onResume=====" + storyBean.userid);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onNextSlideChange = (OnNextSlideChange) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

}
