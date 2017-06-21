package com.jabbar.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jabbar.Bean.ContactBean;
import com.jabbar.R;

import java.util.ArrayList;


public class BuddiesAdapter extends RecyclerView.Adapter<BuddiesAdapter.MyHolder> {

    private ArrayList<ContactBean> contactBeanArrayList;
    private Context context;

    public BuddiesAdapter(Context context, ArrayList<ContactBean> contactBeanArrayList) {
        this.context = context;
        this.contactBeanArrayList = contactBeanArrayList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_buddie, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        holder.txtName.setText(contactBeanArrayList.get(position).name);
        if (!contactBeanArrayList.get(position).status.equalsIgnoreCase("")) {
            holder.txtstatus.setText(contactBeanArrayList.get(position).status);
            holder.txtstatus.setVisibility(View.VISIBLE);
        } else {
            holder.txtstatus.setVisibility(View.GONE);
        }


        holder.rlRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        Glide.with(context).load("").asBitmap().placeholder(R.drawable.default_user).error(R.drawable.default_user).into(new BitmapImageViewTarget(holder.imgAvatar) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                super.onResourceReady(resource, glideAnimation);
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.imgAvatar.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactBeanArrayList.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rlRow;
        public ImageView imgAvatar;
        public TextView txtName;
        public TextView txtstatus;
        public ImageView imgfavorite;

        public MyHolder(View view) {
            super(view);
            rlRow = (RelativeLayout) view.findViewById(R.id.rlRow);
            imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtstatus = (TextView) view.findViewById(R.id.txtstatus);
            imgfavorite = (ImageView) view.findViewById(R.id.imgfavorite);
        }
    }
}
