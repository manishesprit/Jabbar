package com.jabbar.Bll;

import android.content.Context;
import android.database.Cursor;

import com.jabbar.Bean.MessageBean;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Mydb;
import com.jabbar.Utils.Pref;

import java.util.ArrayList;

/**
 * Created by hardikjani on 6/27/17.
 */

public class MessageBll {

    public Context context;

    public MessageBll(Context context) {
        this.context = context;
    }

    public void InsertMessage(MessageBean messageBean) {
        Mydb dbHelper = null;
        String sql = null;

        try {
            sql = "INSERT INTO message_tb (id,userid,friendid,message,create_time) values (" + messageBean.id + "," + messageBean.userid + "," + messageBean.friendid + ",'" + Mydb.getDBStr(messageBean.message) + "','" + messageBean.create_time + "')";
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
            messageBean = null;
            System.gc();
        }
    }

    public ArrayList<MessageBean> geMessageList(int friendid) {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ArrayList<MessageBean> messageBeanArrayList = null;
        MessageBean messageBean;

        try {

            sql = "SELECT id,userid,friendid,message,create_time from message_tb where userid=" + Pref.getValue(context, Config.PREF_USERID, 0) + " AND friendid=" + friendid;

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
                    messageBean.message = cursor.getString(3);
                    messageBean.create_time = cursor.getString(4);
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

}
