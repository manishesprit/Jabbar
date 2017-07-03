package com.jabbar.Adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jabbar.Bean.MessageBean;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Pref;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by admin on 16/3/17.
 */


public class ChatAdpater extends RecyclerView.Adapter<ChatAdpater.ViewHolder> {
    private ArrayList<MessageBean> chatBeanArrayList;
    private Context context;


    public ChatAdpater(Context context, ArrayList<MessageBean> chatBeanArrayList) {

        this.context = context;
        this.chatBeanArrayList = chatBeanArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_conv, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (chatBeanArrayList.get(position).userid != Pref.getValue(context, Config.PREF_USERID, 0)) {
            holder.lin_left.setVisibility(View.VISIBLE);
            holder.lin_right.setVisibility(View.GONE);
            holder.txt_message_left.setText(chatBeanArrayList.get(position).msg);
            holder.txt_time_left.setText(chatBeanArrayList.get(position).create_time);
            holder.txt_message_left.setUseSystemDefault(false);


        } else {
            holder.lin_left.setVisibility(View.GONE);
            holder.lin_right.setVisibility(View.VISIBLE);
            holder.txt_message_right.setText(chatBeanArrayList.get(position).msg);
            holder.txt_time_right.setText(chatBeanArrayList.get(position).create_time);
            holder.txt_message_right.setUseSystemDefault(false);


        }


        holder.lin_right.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(holder.txt_message_right.getText().toString());
                Toast.makeText(context, "Copy message", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        holder.lin_left.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(holder.txt_message_left.getText().toString());
                Toast.makeText(context, "Copy message", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatBeanArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_time_left, txt_time_right;
        LinearLayout lin_left, lin_right;
        TextView imageView;
        EmojiconTextView txt_message_left, txt_message_right;

        public ViewHolder(View v) {
            super(v);
            txt_message_left = (EmojiconTextView) v.findViewById(R.id.txt_message_left);
            txt_time_left = (TextView) v.findViewById(R.id.txt_time_left);
            txt_message_right = (EmojiconTextView) v.findViewById(R.id.txt_message_right);
            txt_time_right = (TextView) v.findViewById(R.id.txt_time_right);
            lin_left = (LinearLayout) v.findViewById(R.id.lin_left);
            lin_right = (LinearLayout) v.findViewById(R.id.lin_right);
        }
    }


}
