package com.jabbar.Bll;

import android.content.Context;
import android.database.Cursor;

import com.jabbar.Bean.ContactsBean;
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

    public void Verify(ContactsBean contactBean) {

    }

    public ArrayList<ContactsBean> geBuddiestList(boolean isFavorite) {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ArrayList<ContactsBean> contactBeanArrayList = null;
        ContactsBean contactsBean;

        try {
            if (isFavorite) {
                sql = "SELECT userid,name,mobile_number,status,avatar,location,last_seen,is_favorite from user_tb";
            } else {
                sql = "SELECT userid,name,mobile_number,status,avatar,location,last_seen,is_favorite from user_tb where is_favorite=1";
            }
            Log.print("===========Business_Hours====" + sql);
            contactBeanArrayList = new ArrayList<>();
            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    contactsBean = new ContactsBean();
                    contactsBean.userid = cursor.getInt(0);
                    contactsBean.name = cursor.getString(1);
                    contactsBean.mobile_number = cursor.getString(2);
                    contactsBean.status = cursor.getString(3);
                    contactsBean.avatar = cursor.getString(4);
                    contactsBean.location = cursor.getString(5);
                    contactsBean.last_seen = cursor.getString(6);
                    contactBeanArrayList.add(contactsBean);
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
