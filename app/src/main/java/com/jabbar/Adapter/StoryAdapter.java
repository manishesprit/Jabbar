package com.jabbar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jabbar.Bean.StoryBean;
import com.jabbar.Bll.StoryBll;
import com.jabbar.MyCamera.MyCameraActivity;
import com.jabbar.R;
import com.jabbar.Ui.ViewStoryActivity;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;

/**
 * Created by user on 22/6/17.
 */

public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<StoryBean> storyBeanArrayList;
    private ArrayList<StoryBean> yourstoryBeanArrayList;
    private StoryBean statusBean;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public static final int STORY_CODE = 500;

    public StoryAdapter(Context context, ArrayList<StoryBean> storyBeanArrayList) {
        this.context = context;
        this.storyBeanArrayList = storyBeanArrayList;

        if (new StoryBll(context).getMyStory()) {
            StoryBean storyBean = new StoryBean();
            storyBean.userid = Pref.getValue(context, Config.PREF_USERID, 0);
            storyBean.userName = "You";
            storyBean.userAvatar = Pref.getValue(context, Config.PREF_AVATAR, "");
            yourstoryBeanArrayList = new ArrayList<>();
            yourstoryBeanArrayList.add(storyBean);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hader_story, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_story, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            headerViewHolder.imgStatusPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) context).startActivityForResult(new Intent(context, MyCameraActivity.class), STORY_CODE);
                }
            });


            if (yourstoryBeanArrayList != null) {
                headerViewHolder.llYourStory.setVisibility(View.VISIBLE);

                if (!Pref.getValue(context, Config.PREF_AVATAR, "").equalsIgnoreCase(""))
                    Utils.setGlideImage(context, Pref.getValue(context, Config.PREF_AVATAR, ""), headerViewHolder.imgyour);

                headerViewHolder.imgyour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ViewStoryActivity.class);
                        intent.putExtra("userBeanArrayList", yourstoryBeanArrayList);
                        intent.putExtra("pos", 1);
                        ((Activity) context).startActivityForResult(intent, STORY_CODE);
                    }
                });
            } else {
                headerViewHolder.llYourStory.setVisibility(View.GONE);
            }
        } else {
            statusBean = getItem(position);
            final ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.txvName.setText(statusBean.userName);
            Log.print("========profilePic========== " + statusBean.userAvatar);

            if (!statusBean.userAvatar.equalsIgnoreCase(""))
                Utils.setGlideImage(context, statusBean.userAvatar, viewHolder.imgStatusPic);

            viewHolder.imgStatusPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewStoryActivity.class);
                    intent.putExtra("userBeanArrayList", storyBeanArrayList);
                    intent.putExtra("pos", position);
                    context.startActivity(intent);
                }
            });
        }
    }

    private StoryBean getItem(int position) {
        return storyBeanArrayList.get(position - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return storyBeanArrayList.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgStatusPic;
        public TextView txvName;

        public ViewHolder(View itemView) {
            super(itemView);

            imgStatusPic = (ImageView) itemView.findViewById(R.id.imgStatusPic);
            txvName = (TextView) itemView.findViewById(R.id.txvName);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout llYourStory;
        public ImageView imgyour;
        public ImageView imgStatusPic;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            llYourStory = (LinearLayout) itemView.findViewById(R.id.llYourStory);
            imgStatusPic = (ImageView) itemView.findViewById(R.id.imgStatusPic);
            imgyour = (ImageView) itemView.findViewById(R.id.imgyour);
        }
    }
}
