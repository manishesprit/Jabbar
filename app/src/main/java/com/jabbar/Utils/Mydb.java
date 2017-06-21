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

    public void Update() {

        int level = Pref.getValue(context, Config.PREF_DB_LEVEL, 0);

        if (level == 0) {
            doUpdate0();
        } else if (level == 1) {
            doUpdate1();
        }
    }

    public void doUpdate0() {
        Log.print("=======level====" + Pref.getValue(context, Config.PREF_DB_LEVEL, 0));
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS user_tb (user_id INTEGER,name TEXT,number TEXT,status TEXT,avatar TEXT,location TEXT,last_seen TEXT,favorite INTEGER DEFAULT 0)");
        db.execSQL("insert into user_tb (user_id,name,number,status,avatar,location,last_seen,favorite) values (1,'Manish rathod','9904841033','nothing','','24.258503_72.190672','09:40',1)");
        int level = Pref.getValue(context, Config.PREF_DB_LEVEL, 0) + 1;
        Pref.setValue(context, Config.PREF_DB_LEVEL, level);
        doUpdate1();
    }

    public void doUpdate1() {
        Log.print("=======level====" + Pref.getValue(context, Config.PREF_DB_LEVEL, 0));
    }

}
