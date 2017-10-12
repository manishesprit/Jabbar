package com.jabbar.Adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jabbar.Bean.ContactsBean;
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


public class ConversionAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MessageBean> chatBeanArrayList;
    private Context context;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ContactsBean contactsBean;


    public ConversionAdpater(Context context, ArrayList<MessageBean> chatBeanArrayList, ContactsBean contactsBean) {

        this.context = context;
        this.contactsBean = contactsBean;
        this.chatBeanArrayList = chatBeanArrayList;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_chat_header, null);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_conv,  null);
            return new ViewHolder(view);
        }
    }

    public void onBindViewHolder(final RecyclerView.ViewHolder viewholder, int position) {



        if (viewholder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewholder;

            headerViewHolder.txtAddContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, contactsBean.mobile_number);
                    context.startActivity(intent);
                }
            });

        } else {
            final ViewHolder holder = (ViewHolder) viewholder;
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

                if (chatBeanArrayList.get(position).isread == 0) {
                    holder.txt_time_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_outline, 0);
                } else {
                    holder.txt_time_right.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

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

    }

    @Override
    public int getItemCount() {
        return chatBeanArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_time_left, txt_time_right;
        LinearLayout lin_left, lin_right;
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


    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView txtAddContact;
        public TextView txtBlock;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            txtAddContact = (TextView) itemView.findViewById(R.id.txtAddContact);
            txtBlock = (TextView) itemView.findViewById(R.id.txtBlock);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            if (contactsBean != null && contactsBean.mobile_number.equalsIgnoreCase(contactsBean.name))
                return TYPE_HEADER;
            else
                return TYPE_ITEM;
        } else
            return TYPE_ITEM;
    }

}
