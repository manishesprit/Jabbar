package com.jabbar.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hardikjani on 6/14/17.
 */

public class Mydb extends SQLiteOpenHelper {

    private Context context;

    public Mydb(Context context) {
        super(context, Config.DB_NAME, null, 33);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor query(String statment) {
        Cursor cur = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Log.print("===statment===" + statment);
            cur = db.rawQuery(statment, null);
            cur.moveToPosition(-1);
        } catch (Exception e) {
           /* FirebaseCrash.report(new Exception(e.toString()));*/
            Log.print(e.toString());
        } finally {

            db.close();
            db = null;
        }

        return cur;
    }

    public void execute(String statment) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Log.print(" :: query() :: " + statment);
            db.execSQL(statment);

        } catch (Exception e) {
            Log.print("======Exception=======" + e.toString());
        } finally {
            db.close();
            db = null;
        }
    }

    public void Update() {

        int level = Pref.getValue(context, Config.PREF_DB_LEVEL, 0);

        if (level == 0) {
            doUpdate0();
        } else if (level == 1) {
            doUpdate1();
        } else if (level == 2) {
            doUpdate2();
        }
    }

    public void doUpdate0() {
        Log.print("=======level====" + Pref.getValue(context, Config.PREF_DB_LEVEL, 0));
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS user_tb (userid INTEGER,name TEXT,mobile_number TEXT,status TEXT,avatar TEXT,location TEXT,last_seen TEXT,is_favorite INTEGER DEFAULT 0)");
        int level = Pref.getValue(context, Config.PREF_DB_LEVEL, 0) + 1;
        Pref.setValue(context, Config.PREF_DB_LEVEL, level);
        doUpdate1();
    }

    public void doUpdate1() {
        Log.print("=======level====" + Pref.getValue(context, Config.PREF_DB_LEVEL, 0));
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS message_tb (id INTEGER,userid INTEGER,message TEXT,create_time TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS default_status (id INTEGER PRIMARY KEY AUTOINCREMENT,status TEXT)");

        db.execSQL("INSERT INTO default_status (status) values ('Hi! i\'m using Jabbar')");
        db.execSQL("INSERT INTO default_status (status) values ('No message, only call')");
        db.execSQL("INSERT INTO default_status (status) values ('Right now i\'m busy')");
        db.execSQL("INSERT INTO default_status (status) values ('I\'m sleeping now)");
        db.execSQL("INSERT INTO default_status (status) values ('can\'t talk only message)");


        int level = Pref.getValue(context, Config.PREF_DB_LEVEL, 0) + 1;
        Pref.setValue(context, Config.PREF_DB_LEVEL, level);
        doUpdate2();
    }

    public void doUpdate2() {
        Log.print("=======level====" + Pref.getValue(context, Config.PREF_DB_LEVEL, 0));
    }

    public static String getDBStr(String str) {

        str = str != null ? str.replaceAll("'", "''") : null;
        str = str != null ? str.replaceAll("&#039;", "''") : null;
        str = str != null ? str.replaceAll("&amp;", "&") : null;

        return str;

    }
}
