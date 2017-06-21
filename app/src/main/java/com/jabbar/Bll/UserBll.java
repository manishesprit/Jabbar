package com.jabbar.Bll;

import android.content.Context;
import android.database.Cursor;

import com.jabbar.Bean.ContactBean;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Mydb;

import java.util.ArrayList;

/**
 * Created by hardikjani on 6/14/17.
 */

public class UserBll {

    public Context context;

    public UserBll(Context context) {
        this.context = context;
    }

    public ArrayList<ContactBean> geBuddiestList(boolean isFavorite) {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ArrayList<ContactBean> contactBeanArrayList = null;

        try {
            if (isFavorite) {
                sql = "SELECT user_id,name,number,status,avatar,location,last_seen,favorite from user_tb";
            } else {
                sql = "SELECT user_id,name,number,status,avatar,location,last_seen,favorite from user_tb where favorite=1";
            }
            Log.print("===========Business_Hours====" + sql);
            contactBeanArrayList = new ArrayList<>();
            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    contactBeanArrayList.add(new ContactBean(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7)));
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
        return contactBeanArrayList;
    }

}
