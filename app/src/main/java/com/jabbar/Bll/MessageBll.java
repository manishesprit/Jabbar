package com.jabbar.Bll;

import android.content.Context;

import com.jabbar.Bean.MessageBean;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Mydb;

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
            sql = "INSERT INTO message_tb (id,userid,message,create_time) values (" + messageBean.id + "," + messageBean.userid + ",'" + Mydb.getDBStr(messageBean.message) + "','" + messageBean.create_time + "')";
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

}
