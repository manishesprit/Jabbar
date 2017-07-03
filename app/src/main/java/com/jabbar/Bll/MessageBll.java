package com.jabbar.Bll;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;

import com.jabbar.Bean.MessageBean;
import com.jabbar.Bean.NotificationBean;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Mydb;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;

/**
 * Created by hardikjani on 6/27/17.
 */

public class MessageBll {

    public Context context;

    public MessageBll(Context context) {
        this.context = context;
    }

    public void InsertMessage(MessageBean messageBean, boolean showNotification) {
        Mydb dbHelper = null;
        String sql = null;

        try {
            sql = "INSERT INTO message_tb (id,userid,friendid,message,create_time,isread) values (" + messageBean.id + "," + messageBean.userid + "," + messageBean.friendid + ",'" + Mydb.getDBStr(messageBean.msg) + "','" + messageBean.create_time + "'," + messageBean.isread + ")";
            dbHelper = new Mydb(this.context);
            dbHelper.execute(sql);

        } catch (Exception e) {
            Log.print(this.getClass() + " :: insert()" + " " + e);
        } finally {
            if (dbHelper != null)
                dbHelper.close();
            // release
            dbHelper = null;
            sql = null;
            System.gc();
        }

        if (showNotification) {
            CreateNotification(false);
        }
    }

    public void RemoveReadMessage(int friendid) {
        Mydb dbHelper = null;
        String sql = null;

        try {
            sql = "UPDATE message_tb set  isread=1 where userid=" + friendid + " AND friendid=" + Pref.getValue(context, Config.PREF_USERID, 0);
            dbHelper = new Mydb(this.context);
            dbHelper.execute(sql);

        } catch (Exception e) {
            Log.print(this.getClass() + " :: insert()" + " " + e);
        } finally {
            if (dbHelper != null)
                dbHelper.close();
            // release
            dbHelper = null;
            sql = null;
            System.gc();
        }
        CreateNotification(true);
    }

    public void CreateNotification(boolean isSilent) {
        ArrayList<NotificationBean> notificationBeanArrayList = geUnreadMessageList();

        if (notificationBeanArrayList != null && notificationBeanArrayList.size() > 0) {

            Log.print("===========notificationBeanArrayList===========" + notificationBeanArrayList.size());

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification.Builder notif = new Notification.Builder(context)
                    .setContentTitle(context.getResources().getString(R.string.app_name) + " Message")
                    .setContentText(notificationBeanArrayList.size() + " message")
                    .setSmallIcon(R.drawable.app_icon);

            if (!isSilent) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notif.setSound(notification);
            }

            if (notificationBeanArrayList.size() > 5) {
                Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
                for (int i = 0; i < 5; i++) {
                    inboxStyle.addLine(notificationBeanArrayList.get(i).name + ":" + notificationBeanArrayList.get(i).message);
                }
                inboxStyle.setSummaryText(notificationBeanArrayList.size() + " messages");
                notif.setStyle(inboxStyle);
            } else {
                Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
                for (int i = 0; i < notificationBeanArrayList.size(); i++) {
                    inboxStyle.addLine(notificationBeanArrayList.get(i).name + ":" + notificationBeanArrayList.get(i).message);
                }
                inboxStyle.setSummaryText(notificationBeanArrayList.size() + " messages");
                notif.setStyle(inboxStyle);
            }

            mNotificationManager.notify(0, notif.build());
        }
    }

    public ArrayList<MessageBean> geMessageList(int friendid) {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ArrayList<MessageBean> messageBeanArrayList = null;
        MessageBean messageBean;

        try {

            sql = "SELECT id,userid,friendid,message,create_time from message_tb where (userid=" + Pref.getValue(context, Config.PREF_USERID, 0) + " AND friendid=" + friendid + " ) OR (userid=" + friendid + " AND friendid=" + Pref.getValue(context, Config.PREF_USERID, 0) + ")";

            messageBeanArrayList = new ArrayList<>();
            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                Log.print("====cursor=====" + cursor.getCount());
                while (cursor.moveToNext()) {
                    messageBean = new MessageBean();
                    messageBean.id = cursor.getInt(0);
                    messageBean.userid = cursor.getInt(1);
                    messageBean.friendid = cursor.getInt(2);
                    messageBean.msg = cursor.getString(3);
                    messageBean.create_time = Utils.convertStringDateToStringDate(Config.WebDateFormatter, Config.AppDateFormatter, cursor.getString(4));
                    messageBeanArrayList.add(messageBean);
                }

            }
        } catch (Exception e) {
            Log.print(this.getClass() + " :: getBusiness_hour()" + " " + e);
            Log.sendError(e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (mydb != null)
                mydb.close();
            // release
            mydb = null;
            sql = null;
            cursor = null;
            System.gc();
        }
        return messageBeanArrayList;
    }

    public ArrayList<NotificationBean> geUnreadMessageList() {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ArrayList<NotificationBean> notificationBeanArrayList = new ArrayList<>();
        NotificationBean notificationBean;

        try {

            sql = "select message_tb.userid, message_tb.message,user_tb.name,user_tb.avatar,message_tb.create_time from message_tb join user_tb on message_tb.userid=user_tb.userid where message_tb.isread=0 order by message_tb.create_time desc";

            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                Log.print("====cursor=====" + cursor.getCount());
                while (cursor.moveToNext()) {
                    notificationBean = new NotificationBean();
                    notificationBean.userid = cursor.getInt(0);
                    notificationBean.message = cursor.getString(1);
                    notificationBean.name = cursor.getString(2);
                    notificationBean.avatar = cursor.getString(3);
                    notificationBeanArrayList.add(notificationBean);
                }

            }
        } catch (Exception e) {
            Log.print(this.getClass() + " :: getBusiness_hour()" + " " + e);
            Log.sendError(e);
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (mydb != null)
                mydb.close();
            // release
            mydb = null;
            sql = null;
            cursor = null;
            System.gc();
        }
        return notificationBeanArrayList;
    }

}
