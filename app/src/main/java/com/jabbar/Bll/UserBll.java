package com.jabbar.Bll;

import android.content.Context;
import android.database.Cursor;

import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bean.StoryBean;
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
            mydb.execute("update user_tb set is_contact=0,name=''");
            mydb.close();
            mydb = null;
            System.gc();

            for (ContactsBean contactsBean : contactsBeanArrayList) {
                verify(contactsBean);
            }

        } catch (Exception e) {
            Log.print("=======InsertContact Exception========" + e.toString());
        } finally {

        }
    }

    public void verify(ContactsBean contactsBean) {
        String sql = "";
        Mydb dbHelper = null;
        Cursor cursor;
        try {
            sql = "SELECT userid FROM user_tb where userid=" + contactsBean.userid;
            System.out.println("=====sql====" + sql);
            dbHelper = new Mydb(context);
            cursor = dbHelper.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                updateContact(contactsBean);
            } else {
                insertContact(contactsBean);
            }
        } catch (Exception e) {

        }
    }

    // Insert new records.
    public void insertContact(ContactsBean contactsBean) {
        Mydb dbHelper = null;
        String sql = null;

        try {
            sql = "INSERT INTO user_tb (userid,name,mobile_number,status,avatar,location,last_seen,is_favorite)"
                    + " VALUES (" + contactsBean.userid + ",'" + Mydb.getDBStr(contactsBean.name) + "','" + Mydb.getDBStr(contactsBean.mobile_number) + "','" + StringEscapeUtils.unescapeJava(contactsBean.status) + "','" + Mydb.getDBStr(contactsBean.avatar) + "','" + Mydb.getDBStr(contactsBean.location) + "','" + Mydb.getDBStr(contactsBean.last_seen) + "',0)";

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
            sql = "UPDATE user_tb SET name='" + Mydb.getDBStr(contactsBean.name) + "', status='" + StringEscapeUtils.unescapeJava(contactsBean.status) + "',avatar='" + Mydb.getDBStr(contactsBean.avatar) + "',last_seen='" + Mydb.getDBStr(contactsBean.last_seen) + "',location='" + Mydb.getDBStr(contactsBean.location) + "',is_contact=1 where userid=" + contactsBean.userid;
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

    public void updateContactOnline(ContactsBean contactsBean) {
        Mydb dbHelper = null;
        String sql = null;
        try {

            dbHelper = new Mydb(this.context);
            sql = "UPDATE user_tb SET status='" + StringEscapeUtils.unescapeJava(contactsBean.status) + "',avatar='" + Mydb.getDBStr(contactsBean.avatar) + "',last_seen='" + Mydb.getDBStr(contactsBean.last_seen) + "',location='" + Mydb.getDBStr(contactsBean.location) + "' where userid=" + contactsBean.userid;
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


    public void updateUserName(ContactsBean contactsBean) {
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
                    contactsBean.name = cursor.getString(1).toString().equalsIgnoreCase("") ? cursor.getString(2) : cursor.getString(1);
                    contactsBean.mobile_number = cursor.getString(2);
                    contactsBean.status = cursor.getString(3);
                    contactsBean.avatar = cursor.getString(4);
                    contactsBean.location = cursor.getString(5);
                    contactsBean.last_seen = Utils.convertStringDateToStringDate(Config.WebDateFormatter, Config.AppChatDateFormatter, cursor.getString(6));
                    contactsBean.isFavorite = cursor.getInt(7);
                    contactsBean.cntUnReasMsg = cursor.getInt(8);
                    if (isFavorite) {
                        if (!contactsBean.location.equalsIgnoreCase(""))
                            contactBeanArrayList.add(contactsBean);
                    } else {
                        contactBeanArrayList.add(contactsBean);
                    }
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

    public ContactsBean getUserDetail(int userid) {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ContactsBean contactsBean = null;

        try {

            sql = "SELECT userid,status,avatar,location,last_seen,name,mobile_number from user_tb where userid=" + userid;

            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                contactsBean = new ContactsBean();
                contactsBean.userid = cursor.getInt(0);
                contactsBean.status = cursor.getString(1);
                contactsBean.avatar = cursor.getString(2);
                contactsBean.location = cursor.getString(3);
                contactsBean.last_seen = Utils.getLastSeen(Config.WebDateFormatter, cursor.getString(4));
                contactsBean.name = cursor.getString(5);
                contactsBean.mobile_number = cursor.getString(6);
                Log.print("=======contactsBean.last_seen=======" + contactsBean.last_seen);

            }
        } catch (Exception e) {
            Log.print(this.getClass() + " ::Exception ()" + " " + e);
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
        return contactsBean;
    }

}
