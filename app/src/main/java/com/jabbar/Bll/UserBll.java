package com.jabbar.Bll;

import android.content.Context;
import android.database.Cursor;

import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bean.FavoriteBean;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Mydb;
import com.jabbar.Utils.Utils;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

/**
 * Created by hardikjani on 6/14/17.
 */

public class UserBll {

    public Context context;

    public UserBll(Context context) {
        this.context = context;
    }

    public void InsertContact(ArrayList<ContactsBean> contactsBeanArrayList) {
        try {

            Mydb mydb = new Mydb(this.context);
            mydb.execute("delete from user_tb");
            mydb.close();
            mydb = null;
            System.gc();

            for (ContactsBean contactsBean : contactsBeanArrayList) {
                insertContact(contactsBean);

            }

        } catch (Exception e) {
            Log.print("=======InsertContact Exception========" + e.toString());
        } finally {

        }
    }


    // Insert new records.
    public void insertContact(ContactsBean contactsBean) {
        Mydb dbHelper = null;
        String sql = null;

        try {
            sql = "INSERT INTO user_tb (userid,name,mobile_number,status,avatar,location,last_seen,is_favorite)"
                    + " VALUES (" + contactsBean.userid + ",'" + Mydb.getDBStr(contactsBean.name) + "','" + Mydb.getDBStr(contactsBean.mobile_number) + "','" + Mydb.getDBStr(StringEscapeUtils.unescapeJava(contactsBean.status)) + "','" + Mydb.getDBStr(contactsBean.avatar) + "','" + Mydb.getDBStr(contactsBean.location) + "','" + Mydb.getDBStr(contactsBean.last_seen) + "'," + contactsBean.isFavorite + ")";

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
            contactsBean = null;
            System.gc();
        }
    }

    public void updateContact(ContactsBean contactsBean) {
        Mydb dbHelper = null;
        String sql = null;
        try {

            dbHelper = new Mydb(this.context);
            sql = "UPDATE user_tb SET  name = '" + Mydb.getDBStr(contactsBean.name) + "' WHERE  mobile_number= '" + contactsBean.mobile_number + "'";
            dbHelper.execute(sql);


        } catch (Exception e) {
            Log.print(this.getClass() + " :: update()" + "===" + e);
        } finally {
            if (dbHelper != null)
                dbHelper.close();
            // release
            dbHelper = null;
            sql = null;
            contactsBean = null;
            System.gc();
        }
    }

    public void updateFavoriteContact(int userid, int isFavorite) {
        Mydb dbHelper = null;
        String sql = null;
        try {

            dbHelper = new Mydb(this.context);
            sql = "UPDATE user_tb SET  is_favorite = " + isFavorite + " WHERE  userid= '" + userid + "'";
            dbHelper.execute(sql);


        } catch (Exception e) {
            Log.print(this.getClass() + " :: update()" + "===" + e);
        } finally {
            if (dbHelper != null)
                dbHelper.close();
            // release
            dbHelper = null;
            sql = null;
            System.gc();
        }
    }

    public void updateFavoriteContact(FavoriteBean favoriteBean) {
        Mydb dbHelper = null;
        String sql = null;
        try {

            dbHelper = new Mydb(this.context);
            sql = "UPDATE user_tb SET location='" + favoriteBean.location + "',status='" + favoriteBean.status + "', is_favorite = 1 WHERE  userid= " + favoriteBean.userid + "";
            dbHelper.execute(sql);


        } catch (Exception e) {
            Log.print(this.getClass() + " :: update()" + "===" + e);
        } finally {
            if (dbHelper != null)
                dbHelper.close();
            // release
            dbHelper = null;
            sql = null;
            System.gc();
        }
    }

    public void UpdateDirectContact(ArrayList<ContactsBean> contactsBeanArrayList) {
        for (ContactsBean contactsBean : contactsBeanArrayList) {
            updateContact(contactsBean);
        }
    }


    public ArrayList<ContactsBean> geBuddiestList(boolean isFavorite) {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ArrayList<ContactsBean> contactBeanArrayList = null;
        ContactsBean contactsBean;

        try {
            if (isFavorite) {
                sql = "SELECT userid,name,mobile_number,status,avatar,location,last_seen,is_favorite,(select count (*) from message_tb where( message_tb.userid = user_tb.userid) AND (message_tb.isread=0)) as unread_msg from user_tb where is_favorite=1 ORDER BY NAME ASC";
            } else {
                sql = "SELECT userid,name,mobile_number,status,avatar,location,last_seen,is_favorite,(select count (*) from message_tb where( message_tb.userid = user_tb.userid) AND (message_tb.isread=0)) as unread_msg from user_tb ORDER BY NAME ASC";
            }
            contactBeanArrayList = new ArrayList<>();
            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                Log.print("====cursor=====" + cursor.getCount());
                while (cursor.moveToNext()) {
                    contactsBean = new ContactsBean();
                    contactsBean.userid = cursor.getInt(0);
                    contactsBean.name = cursor.getString(1);
                    contactsBean.mobile_number = cursor.getString(2);
                    contactsBean.status = cursor.getString(3);
                    contactsBean.avatar = cursor.getString(4);
                    contactsBean.location = cursor.getString(5);
                    contactsBean.last_seen = Utils.convertStringDateToStringDate(Config.WebDateFormatter, Config.AppChatDateFormatter, cursor.getString(6));
                    contactsBean.isFavorite = cursor.getInt(7);
                    contactsBean.cntUnReasMsg = cursor.getInt(8);
                    contactBeanArrayList.add(contactsBean);
                    Log.print("====mobile_number=====" + contactsBean.mobile_number + "=======name=======" + contactsBean.name + "========= contactsBean.last_seen======" + contactsBean.last_seen);
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
