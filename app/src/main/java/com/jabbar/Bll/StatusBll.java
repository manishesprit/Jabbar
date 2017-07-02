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
 * Created by Manish on 02-07-2017.
 */

public class StatusBll {

    public Context context;

    public StatusBll(Context context) {
        this.context = context;
    }

    public void insertStatus(String status) {
        Mydb dbHelper = null;
        String sql = null;

        try {
            sql = "INSERT INTO default_status (status) values ('" + status + "')";
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
    }

    public ArrayList<String> getDefaultStatusList() {
        Mydb mydb = null;
        String sql = null;
        Cursor cursor = null;
        ArrayList<String> defaultStatusList = new ArrayList<>();

        try {

            sql = "SELECT status from default_status order by id desc";
            mydb = new Mydb(this.context);
            cursor = mydb.query(sql);

            if (cursor != null && cursor.getCount() > 0) {
                Log.print("====cursor=====" + cursor.getCount());
                while (cursor.moveToNext()) {
                    defaultStatusList.add(cursor.getString(0));
                }
            }
        } catch (Exception e) {
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
        return defaultStatusList;
    }

}
