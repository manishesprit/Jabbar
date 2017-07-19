package com.jabbar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jabbar.Bean.ContactsBean;
import com.jabbar.R;
import com.jabbar.Ui.ChatNewActivity;
import com.jabbar.Ui.ProfileActivity;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static com.jabbar.Ui.HomeActivity.CODE_CHAT;


public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.MyHolder> {

    private ArrayList<ContactsBean> contactBeanArrayList;
    private Context context;

    public ChatsAdapter(Context context, ArrayList<ContactsBean> contactBeanArrayList) {
        this.context = context;
        this.contactBeanArrayList = contactBeanArrayList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chat, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {

        holder.txtName.setText(contactBeanArrayList.get(position).name);
        holder.txtstatus.setText(contactBeanArrayList.get(position).msg);
        holder.txtTime.setText(contactBeanArrayList.get(position).create_time);
        holder.txtstatus.setVisibility(View.VISIBLE);

        if (contactBeanArrayList.get(position).isread == 0) {
            if (contactBeanArrayList.get(position).users == false) {
                holder.txtTime.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                holder.txtNoMsg.setText("" + contactBeanArrayList.get(position).cntUnReasMsg);
                holder.txtNoMsg.setVisibility(View.VISIBLE);
            } else {
                holder.imgUnsend.setVisibility(View.VISIBLE);
                holder.txtTime.setTextColor(context.getResources().getColor(R.color.color_title_sub));
                holder.txtNoMsg.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.txtTime.setTextColor(context.getResources().getColor(R.color.color_title_sub));
            holder.txtNoMsg.setVisibility(View.INVISIBLE);
            holder.imgUnsend.setVisibility(View.GONE);
        }

        holder.rlRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).startActivityForResult(new Intent(context, ChatNewActivity.class).putExtra("data", contactBeanArrayList.get(position)), CODE_CHAT);
            }
        });

        holder.imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("data", contactBeanArrayList.get(position)));
            }
        });

        if (!contactBeanArrayList.get(position).avatar.equalsIgnoreCase(""))
            Utils.setGlideImage(context, contactBeanArrayList.get(position).avatar, holder.imgAvatar);

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
        public TextView txtTime;
        public ImageView imgUnsend;
        public EmojiconTextView txtstatus;

        public MyHolder(View view) {
            super(view);
            rlRow = (RelativeLayout) view.findViewById(R.id.rlRow);
            imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
            txtNoMsg = (TextView) view.findViewById(R.id.txtNoMsg);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtTime = (TextView) view.findViewById(R.id.txtTime);
            imgUnsend = (ImageView) view.findViewById(R.id.imgUnsend);
            txtstatus = (EmojiconTextView) view.findViewById(R.id.txtstatus);
        }
    }
}
