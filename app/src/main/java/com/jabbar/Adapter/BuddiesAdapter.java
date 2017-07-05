package com.jabbar.Adapter;

import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.MyClickListener;
import com.jabbar.R;
import com.jabbar.Ui.ChatActivity;
import com.jabbar.Ui.ProfileActivity;
import com.jabbar.Utils.Config;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static com.jabbar.Ui.HomeActivity.CODE_CHAT;


public class BuddiesAdapter extends RecyclerView.Adapter<BuddiesAdapter.MyHolder> {

    private ArrayList<ContactsBean> contactBeanArrayList;
    private Context context;
    public MyClickListener myClickListener;

    public BuddiesAdapter(Context context, ArrayList<ContactsBean> contactBeanArrayList, MyClickListener myClickListener) {
        this.context = context;
        this.contactBeanArrayList = contactBeanArrayList;
        this.myClickListener = myClickListener;
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

        if (contactBeanArrayList.get(position).isFavorite == 1) {
            holder.imgfavorite.setImageResource(R.drawable.ic_star_fill);
        } else {
            holder.imgfavorite.setImageResource(R.drawable.ic_star_unfill);
        }

        if (contactBeanArrayList.get(position).cntUnReasMsg > 0) {
            holder.txtNoMsg.setText("" + contactBeanArrayList.get(position).cntUnReasMsg);
            holder.txtNoMsg.setVisibility(View.VISIBLE);
        } else {
            holder.txtNoMsg.setVisibility(View.GONE);
        }

        holder.imgfavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClickListener.onClick(position);
            }
        });

        holder.rlRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).startActivityForResult(new Intent(context, ChatActivity.class).putExtra("data", contactBeanArrayList.get(position)), CODE_CHAT);
            }
        });

        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("data", contactBeanArrayList.get(position)));
            }
        });

        Glide.with(context).load(Config.AVATAR_HOST + contactBeanArrayList.get(position).avatar).asBitmap().placeholder(R.drawable.default_user).error(R.drawable.default_user).into(new BitmapImageViewTarget(holder.imgAvatar) {
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
        public TextView txtNoMsg;
        public TextView txtName;
        public EmojiconTextView txtstatus;
        public ImageView imgfavorite;

        public MyHolder(View view) {
            super(view);
            rlRow = (RelativeLayout) view.findViewById(R.id.rlRow);
            imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
            txtNoMsg = (TextView) view.findViewById(R.id.txtNoMsg);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtstatus = (EmojiconTextView) view.findViewById(R.id.txtstatus);
            imgfavorite = (ImageView) view.findViewById(R.id.imgfavorite);
        }
    }
}
