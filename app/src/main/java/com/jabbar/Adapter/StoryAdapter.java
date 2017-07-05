package com.jabbar.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jabbar.Bean.StoryBean;
import com.jabbar.R;
import com.jabbar.Ui.AddStoryActivity;
import com.jabbar.Utils.Log;

import java.util.ArrayList;

/**
 * Created by user on 22/6/17.
 */

public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<StoryBean> storyBeanArrayList;
    private StoryBean statusBean;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public StoryAdapter(Context context, ArrayList<StoryBean> storyBeanArrayList) {
        this.context = context;
        this.storyBeanArrayList = storyBeanArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_story, parent, false);
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
            headerViewHolder.txvName.setText("Add Status");
            headerViewHolder.imgStatusPic.setImageResource(R.drawable.ic_camera_black);
            headerViewHolder.imgStatusPic.setPadding(30, 30, 30, 30);

            headerViewHolder.imgStatusPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, AddStoryActivity.class));
                }
            });

        } else {
            statusBean = getItem(position);
            final ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.txvName.setText(statusBean.userName);
            Log.print("========profilePic========== " + statusBean.userAvatar);
            Glide.with(context).load(statusBean.userAvatar).asBitmap().placeholder(R.drawable.default_user).into(new BitmapImageViewTarget(viewHolder.imgStatusPic) {
                @Override
                protected void setResource(Bitmap resource) {
                    super.setResource(resource);
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    roundedBitmapDrawable.setCircular(true);
                    viewHolder.imgStatusPic.setImageDrawable(roundedBitmapDrawable);
                }
            });
            viewHolder.imgStatusPic.setTag(statusBean);
            viewHolder.imgStatusPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, ViewStatusActivity.class);
//                    intent.putExtra("userBeanArrayList", userBeanArrayList);
//                    intent.putExtra("pos", position);
//                    context.startActivity(intent);
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

        public ImageView imgStatusPic;
        public TextView txvName;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            imgStatusPic = (ImageView) itemView.findViewById(R.id.imgStatusPic);
            txvName = (TextView) itemView.findViewById(R.id.txvName);
        }
    }
}
