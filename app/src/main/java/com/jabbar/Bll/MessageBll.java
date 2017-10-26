package com.jabbar.Bll;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.jabbar.Bean.MessageBean;
import com.jabbar.Bean.NotificationBean;
import com.jabbar.R;
import com.jabbar.Ui.HomeActivity;
import com.jabbar.Utils.BadgeUtils;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Mydb;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

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

    public void InsertNewMessage(MessageBean messageBean, boolean showNotification) {
        Mydb dbHelper = null;
        String sql = null;

        try {
            sql = "INSERT INTO message_tb (id,userid,friendid,message,create_time,isread,tempId) values (" + messageBean.id + "," + messageBean.userid + "," + messageBean.friendid + ",'" + Mydb.getDBStr(messageBean.msg) + "','" + messageBean.create_time + "'," + messageBean.isread + ",'" + messageBean.tempId + "')";
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

    public void UpdateMessage(MessageBean messageBean, int friendid) {
        Mydb dbHelper = null;
        String sql = null;

        try {
            sql = "UPDATE message_tb set id=" + messageBean.id + ",create_time='" + messageBean.create_time + "', isread=1 where tempId='" + messageBean.tempId + "' AND  friendid = " + friendid;
            dbHelper = new Mydb(this.context);
            dbHelper.execute(sql);

        } catch (Exception e) {
            Log.print(this.getClass() + " :: UPDATE()" + " " + e);
        } finally {
            if (dbHelper != null)
                dbHelper.close();
            // release
            dbHelper = null;
            sql = null;
            System.gc();
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

            ArrayList<Integer> stringIntegerHashMap = new ArrayList<>();
            for (NotificationBean notificationBean : notificationBeanArrayList) {
                if (!stringIntegerHashMap.contains(notificationBean.userid)) {
                    Log.print("====put===" + notificationBean.userid);
                    stringIntegerHashMap.add(notificationBean.userid);
                }
            }

            Log.print("===========notificationBeanArrayList===========" + notificationBeanArrayList.size());
            Log.print("========stringIntegerHashMap===================" + stringIntegerHashMap.size());

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification.Builder notif = new Notification.Builder(context)
                    .setContentTitle(context.getResources().getString(R.string.app_name) + " Message")
                    .setSmallIcon(R.drawable.jabbar);

            if (!isSilent) {
                Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/alert_tone");
//                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notif.setSound(uri);
            }


            Notification.InboxStyle inboxStyle;

            if (notificationBeanArrayList.size() > 5) {
                inboxStyle = new Notification.InboxStyle();
                for (int i = 4; i >= 0; i--) {
                    inboxStyle.addLine(notificationBeanArrayList.get(i).name + " : " + notificationBeanArrayList.get(i).message);
                }

            } else {
                inboxStyle = new Notification.InboxStyle();
                for (int i = (notificationBeanArrayList.size() - 1); i >= 0; i--) {
                    inboxStyle.addLine(notificationBeanArrayList.get(i).name + " : " + notificationBeanArrayList.get(i).message);
                }
            }

            if (stringIntegerHashMap.size() == 1) {
                notif.setContentText(notificationBeanArrayList.size() + " message from " + notificationBeanArrayList.get(0).name);
                inboxStyle.setSummaryText(notificationBeanArrayList.size() + " message from " + notificationBeanArrayList.get(0).name);
                notif.setStyle(inboxStyle);
            } else {
                notif.setContentText(notificationBeanArrayList.size() + " message from " + stringIntegerHashMap.size() + " chats");
                inboxStyle.setSummaryText(notificationBeanArrayList.size() + " message from " + stringIntegerHashMap.size() + " chats");
                notif.setStyle(inboxStyle);
            }

            Intent intent = new Intent(context, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("type", 2);
            notif.setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));

            mNotificationManager.notify(0, notif.build());

            BadgeUtils.setBadge(context, notificationBeanArrayList.size());
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

    public ArrayList<MessageBean> geNewMessageList(int friendid) {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ArrayList<MessageBean> messageBeanArrayList = null;
        MessageBean messageBean;

        try {

            sql = "SELECT id,userid,friendid,message,create_time,tempId,isread from message_tb where ((userid=" + Pref.getValue(context, Config.PREF_USERID, 0) + " AND friendid=" + friendid + " ) OR (userid=" + friendid + " AND friendid=" + Pref.getValue(context, Config.PREF_USERID, 0) + ")) order by id asc ";

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
                    messageBean.tempId = cursor.getString(5);
                    messageBean.isread = cursor.getInt(6);
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

            sql = "select message_tb.userid, message_tb.message,user_tb.name,user_tb.avatar,message_tb.create_time,user_tb.mobile_number from message_tb join user_tb on message_tb.userid=user_tb.userid where message_tb.isread=0 order by message_tb.create_time desc";

            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                Log.print("====cursor=====" + cursor.getCount());
                while (cursor.moveToNext()) {
                    notificationBean = new NotificationBean();
                    notificationBean.userid = cursor.getInt(0);
                    notificationBean.message = cursor.getString(1);
                    notificationBean.name = cursor.getString(2).toString().trim().equalsIgnoreCase("") ? cursor.getString(5) : cursor.getString(2);
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

    public String geUnsendMessageList() {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        int friendid = -1;
        try {

            sql = "select message,tempId,create_time,friendid from message_tb where userid='" + Pref.getValue(context, Config.PREF_USERID, 0) + "' AND isread=0 order by friendid asc";

            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            JSONArray jsonObject = new JSONArray();
            JSONObject jsonList = new JSONObject();
            JSONArray jsonMsgList = new JSONArray();

            if (cursor != null && cursor.getCount() > 0) {
                Log.print("====cursor=====" + cursor.getCount());
                while (cursor.moveToNext()) {
                    if (friendid == -1) {
                        jsonList.put("friendid", cursor.getInt(3));
                        friendid = cursor.getInt(3);
                        jsonMsgList = new JSONArray();

                        JSONObject jsonMsg = new JSONObject();
                        jsonMsg.put("id", cursor.getString(1));
                        jsonMsg.put("msg", cursor.getString(0));
                        jsonMsgList.put(jsonMsg);

                    } else if (friendid != cursor.getInt(3)) {

                        jsonList.put("messages", jsonMsgList);
                        jsonObject.put(jsonList);

                        jsonList = new JSONObject();
                        friendid = cursor.getInt(3);
                        jsonList.put("friendid", cursor.getInt(3));
                        jsonMsgList = new JSONArray();

                        JSONObject jsonMsg = new JSONObject();
                        jsonMsg.put("id", cursor.getString(1));
                        jsonMsg.put("msg", cursor.getString(0));
                        jsonMsgList.put(jsonMsg);

                    } else {
                        friendid = cursor.getInt(3);
                        JSONObject jsonMsg = new JSONObject();
                        jsonMsg.put("id", cursor.getString(1));
                        jsonMsg.put("msg", cursor.getString(0));
                        jsonMsgList.put(jsonMsg);
                    }
                }

                jsonList.put("messages", jsonMsgList);
                jsonObject.put(jsonList);
                JSONObject mainData = new JSONObject();
                mainData.put("users", jsonObject);
                Log.print("======mainData========");
                return mainData.toString();


            }
        } catch (Exception e) {
            Log.print("====Exception======" + e.toString());
//            Log.print(this.getClass() + "()" + " " + e);
//            Log.sendError(e);
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
        return null;
    }

}
