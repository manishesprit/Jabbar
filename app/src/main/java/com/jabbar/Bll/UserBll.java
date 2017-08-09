package com.jabbar.Bll;

import android.content.Context;
import android.database.Cursor;

import com.jabbar.Bean.ContactsBean;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Mydb;
import com.jabbar.Utils.Pref;
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
            Log.print("=====sql====" + sql);
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


    public void getUser(int userid, String mobile_number) {
        String sql = "";
        Mydb dbHelper = null;
        Cursor cursor;
        try {
            sql = "SELECT userid FROM user_tb where userid=" + userid;
            Log.print("=====sql====" + sql);
            dbHelper = new Mydb(context);
            cursor = dbHelper.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
            } else {
                insertDirect(userid, mobile_number);
            }
        } catch (Exception e) {

        }
    }


    public void insertDirect(int userid, String mobile_number) {
        Log.print("===insertDirect==");
        Mydb dbHelper = null;
        String sql = null;

        try {
            sql = "INSERT INTO user_tb (userid,name,mobile_number,status,avatar,location,last_seen,is_favorite,is_contact) VALUES (" + userid + ",'','" + mobile_number + "','','','','',0,0)";
            Log.print("===sql==" + sql);
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
        //
    }

    public ArrayList<ContactsBean> getChatList() {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ArrayList<ContactsBean> contactBeanArrayList = null;
        ContactsBean contactsBean;

        try {
            //id,message,create_time,userid,avatar,name,mobile_number,status,isread

            sql = "select \n" +
                    "ifnull(res.id,0),res.message,res.create_time,res.userid,\n" +
                    "res.avatar,res.name,res.mobile_number,res.status,res.isread,\n" +
                    "ifnull(sen.id,0),sen.message,sen.create_time,sen.friendid,\n" +
                    "sen.avatar,sen.name ,sen.mobile_number,sen.status,sen.isread ,\n" +
                    "case when res.create_time is null then sen.create_time when sen.create_time is null then res.create_time when res.create_time > sen.create_time then res.create_time else sen.create_time end as create_time\n" +
                    "from \n" +
                    "(select id,message,create_time,message_tb.friendid,user_tb.avatar,user_tb.name,user_tb.mobile_number,user_tb.status,isread from message_tb join user_tb on (message_tb.friendid=user_tb.userid) where message_tb.userid=" + Pref.getValue(context, Config.PREF_USERID, 0) + " group by friendid) sen\n" +
                    "LEFT join \n" +
                    "(select id,message,create_time,message_tb.userid,user_tb.avatar,user_tb.name,user_tb.mobile_number,user_tb.status,isread \n" +
                    "from message_tb join user_tb on (message_tb.userid=user_tb.userid) where friendid=" + Pref.getValue(context, Config.PREF_USERID, 0) + " group by message_tb.userid) res \n" +
                    " on (res.userid=sen.friendid)\n" +
                    "UNION\n" +
                    "select \n" +
                    "ifnull(res.id,0),res.message,res.create_time,res.userid,\n" +
                    "res.avatar,res.name,res.mobile_number,res.status,res.isread,\n" +
                    "ifnull(sen.id,0),sen.message,sen.create_time,sen.friendid,\n" +
                    "sen.avatar,sen.name ,sen.mobile_number,sen.status,sen.isread ,\n" +
                    "case when res.create_time is null then sen.create_time when sen.create_time is null then res.create_time when res.create_time > sen.create_time then res.create_time else sen.create_time end as create_time\n" +
                    "from \n" +
                    "(select id,message,create_time,message_tb.userid,user_tb.avatar,user_tb.name,user_tb.mobile_number,user_tb.status,isread \n" +
                    "from message_tb join user_tb on (message_tb.userid=user_tb.userid) where friendid=" + Pref.getValue(context, Config.PREF_USERID, 0) + " group by message_tb.userid) res\n" +
                    "LEFT join \n" +
                    "(select id,message,create_time,message_tb.friendid,user_tb.avatar,user_tb.name,user_tb.mobile_number,user_tb.status,isread from message_tb join user_tb on (message_tb.friendid=user_tb.userid) where message_tb.userid=" + Pref.getValue(context, Config.PREF_USERID, 0) + " group by friendid) sen\n" +
                    "on (res.userid=sen.friendid) order by create_time desc\n";

            contactBeanArrayList = new ArrayList<>();
            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                Log.print("====cursor=====" + cursor.getCount());
                while (cursor.moveToNext()) {
                    contactsBean = new ContactsBean();
                    if (cursor.getInt(0) == 0) {
                        contactsBean.msg = cursor.getString(10);
                        contactsBean.create_time = Utils.gettime(Config.WebDateFormatter, cursor.getString(11));
                        contactsBean.userid = cursor.getInt(12);
                        contactsBean.avatar = cursor.getString(13);
                        contactsBean.name = cursor.getString(14).toString().equalsIgnoreCase("") ? cursor.getString(15) : cursor.getString(14);
                        contactsBean.mobile_number = cursor.getString(15);
                        contactsBean.status = cursor.getString(16);
                        contactsBean.isread = cursor.getInt(17);
                        contactsBean.users = true;
                        contactsBean.cntUnReasMsg = 0;

                    } else if (cursor.getInt(9) == 0) {
                        contactsBean.msg = cursor.getString(1);
                        contactsBean.create_time = Utils.gettime(Config.WebDateFormatter, cursor.getString(2));
                        contactsBean.userid = cursor.getInt(3);
                        contactsBean.avatar = cursor.getString(4);
                        contactsBean.name = cursor.getString(5).toString().equalsIgnoreCase("") ? cursor.getString(6) : cursor.getString(5);
                        contactsBean.mobile_number = cursor.getString(6);
                        contactsBean.status = cursor.getString(7);
                        contactsBean.isread = cursor.getInt(8);
                        contactsBean.users = false;
                        contactsBean.cntUnReasMsg = 1;

                        sql = "select count(id) from message_tb where (userid=" + cursor.getInt(3) + " AND friendid=" + Pref.getValue(context, Config.PREF_USERID, 0) + ") AND isread=0 ";
                        Cursor cursor1 = mydb.query(sql);
                        if (cursor1 != null && cursor1.getCount() > 0) {
                            cursor1.moveToFirst();
                            contactsBean.cntUnReasMsg = cursor1.getInt(0);
                            cursor1.close();
                        }
                    } else {
                        if (cursor.getInt(0) < cursor.getInt(9)) {
                            contactsBean.msg = cursor.getString(10);
                            contactsBean.create_time = Utils.gettime(Config.WebDateFormatter, cursor.getString(11));
                            contactsBean.userid = cursor.getInt(12);
                            contactsBean.avatar = cursor.getString(13);
                            contactsBean.name = cursor.getString(14).toString().equalsIgnoreCase("") ? cursor.getString(15) : cursor.getString(14);
                            contactsBean.mobile_number = cursor.getString(15);
                            contactsBean.status = cursor.getString(16);
                            contactsBean.isread = cursor.getInt(17);
                            contactsBean.users = true;
                            contactsBean.cntUnReasMsg = 0;

                        } else {
                            contactsBean.msg = cursor.getString(1);
                            contactsBean.create_time = Utils.gettime(Config.WebDateFormatter, cursor.getString(2));
                            contactsBean.userid = cursor.getInt(3);
                            contactsBean.avatar = cursor.getString(4);
                            contactsBean.name = cursor.getString(5).toString().equalsIgnoreCase("") ? cursor.getString(6) : cursor.getString(5);
                            contactsBean.mobile_number = cursor.getString(6);
                            contactsBean.status = cursor.getString(7);
                            contactsBean.isread = cursor.getInt(8);
                            contactsBean.users = false;
                            contactsBean.cntUnReasMsg = 1;

                            sql = "select count(id) from message_tb where (userid=" + cursor.getInt(3) + " AND friendid=" + Pref.getValue(context, Config.PREF_USERID, 0) + ") AND isread=0 ";
                            Cursor cursor1 = mydb.query(sql);
                            if (cursor1 != null && cursor1.getCount() > 0) {
                                cursor1.moveToFirst();
                                contactsBean.cntUnReasMsg = cursor1.getInt(0);
                                cursor1.close();
                            }

                        }
                    }
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

    public ArrayList<ContactsBean> geFavoriteList() {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ArrayList<ContactsBean> contactBeanArrayList = null;
        ContactsBean contactsBean;

        try {

            sql = "SELECT userid,name,status,location,avatar from user_tb where is_favorite=1 ORDER BY name ASC";

            contactBeanArrayList = new ArrayList<>();
            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                Log.print("====cursor=====" + cursor.getCount());
                while (cursor.moveToNext()) {
                    contactsBean = new ContactsBean();
                    contactsBean.userid = cursor.getInt(0);
                    contactsBean.name = cursor.getString(1).toString();
                    contactsBean.status = cursor.getString(2);
                    contactsBean.location = cursor.getString(3);
                    contactsBean.avatar = cursor.getString(4);
                    contactBeanArrayList.add(contactsBean);
                }

            }
        } catch (Exception e) {
            Log.print(this.getClass() + " :: contactBeanArrayList()" + " " + e);
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

    public ArrayList<ContactsBean> geBuddiestList() {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ArrayList<ContactsBean> contactBeanArrayList = null;
        ContactsBean contactsBean;

        try {

            sql = "SELECT userid,name,mobile_number,status,avatar,location,last_seen,is_favorite from user_tb where is_contact = 1 ORDER BY name ASC";

            contactBeanArrayList = new ArrayList<>();
            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                Log.print("====cursor=====" + cursor.getCount());
                while (cursor.moveToNext()) {
                    contactsBean = new ContactsBean();
                    contactsBean.userid = cursor.getInt(0);
                    contactsBean.name = cursor.getString(1).toString();
                    contactsBean.mobile_number = cursor.getString(2);
                    contactsBean.status = cursor.getString(3);
                    contactsBean.avatar = cursor.getString(4);
                    contactsBean.location = cursor.getString(5);
                    contactsBean.last_seen = Utils.convertStringDateToStringDate(Config.WebDateFormatter, Config.AppChatDateFormatter, cursor.getString(6));
                    contactsBean.isFavorite = cursor.getInt(7);

                    contactBeanArrayList.add(contactsBean);

                    Log.print("====mobile_number=====" + contactsBean.mobile_number + "=======name=======" + contactsBean.name + "========= contactsBean.last_seen======" + contactsBean.last_seen);
                }

            }
        } catch (Exception e) {
            Log.print(this.getClass() + " :: contactBeanArrayList()" + " " + e);
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

    public void update_alert_privacy(int userid, int alert_status) {
        Mydb dbHelper = null;
        String sql = null;
        try {

            dbHelper = new Mydb(this.context);
            sql = "UPDATE user_tb SET  alert_status = " + alert_status + " WHERE  userid= '" + userid + "'";
            dbHelper.execute(sql);

        } catch (Exception e) {
            Log.print(this.getClass() + " :: update_alert_privacy()" + "===" + e);
        } finally {
            if (dbHelper != null)
                dbHelper.close();
            // release
            dbHelper = null;
            sql = null;
            System.gc();
        }
    }

    public int getAlertStatus(int userid) {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ContactsBean contactsBean = null;

        try {

            sql = "SELECT alert_status from user_tb where userid=" + userid;

            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getInt(0);

            } else {
                return 1;
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
        return 1;
    }


    public String getUsername(int userid) {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ContactsBean contactsBean = null;

        try {

            sql = "SELECT name,mobile_number from user_tb where userid=" + userid;

            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                if (!cursor.getString(0).equalsIgnoreCase("")) {
                    return cursor.getString(0);
                } else {
                    return cursor.getString(1);
                }

            } else {
                return null;
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
        return null;
    }

    public ContactsBean getUserDetail(int userid) {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ContactsBean contactsBean = null;

        try {

            sql = "SELECT userid,status,avatar,location,last_seen,name,mobile_number,is_favorite from user_tb where userid=" + userid;

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
                contactsBean.isFavorite = cursor.getInt(7);

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
