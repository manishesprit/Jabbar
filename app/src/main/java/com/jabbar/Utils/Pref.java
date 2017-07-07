package com.jabbar.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jabbar.Bean.ExitsContactBean;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Pref {
    private static SharedPreferences sharedPreferences = null;

    public static void openPref(Context context) {
        sharedPreferences = context.getSharedPreferences(Config.PREF_FILE, Context.MODE_PRIVATE);
    }

    public static String getValue(Context context, String key,
                                  String defaultValue) {
        Pref.openPref(context);
        String result = Pref.sharedPreferences.getString(key, defaultValue);
        Pref.sharedPreferences = null;
        return result;
    }

    public static void setValue(Context context, String key, String value) {
        Pref.openPref(context);
        Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
        prefsPrivateEditor.putString(key, value);
        prefsPrivateEditor.commit();
        prefsPrivateEditor = null;
        Pref.sharedPreferences = null;
    }

    public static int getValue(Context context, String key,
                               int defaultValue) {
        Pref.openPref(context);
        int result = Pref.sharedPreferences.getInt(key, defaultValue);
        Pref.sharedPreferences = null;
        return result;
    }

    public static void setValue(Context context, String key, int value) {
        Pref.openPref(context);
        Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
        prefsPrivateEditor.putInt(key, value);
        prefsPrivateEditor.commit();
        prefsPrivateEditor = null;
        Pref.sharedPreferences = null;
    }

    public static void remove(Context context, String key) {
        Pref.openPref(context);
        Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
        prefsPrivateEditor.remove(key);
        prefsPrivateEditor.commit();
        prefsPrivateEditor = null;
        Pref.sharedPreferences = null;
    }

    public static void setLongValue(Context context, String key, long value) {
        Pref.openPref(context);
        Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
        prefsPrivateEditor.putLong(key, value);
        prefsPrivateEditor.commit();
        prefsPrivateEditor = null;
        Pref.sharedPreferences = null;
    }

    public static long getLongValue(Context context, String key,
                                    long defaultValue) {
        Pref.openPref(context);
        long result = Pref.sharedPreferences.getLong(key, defaultValue);
        Pref.sharedPreferences = null;
        return result;
    }

//    @SuppressWarnings("unchecked")
//    public static ArrayList<ExitsContactBean> getArrayValue(Context context, String key, ArrayList<ExitsContactBean> defaultValue) {
//        Pref.openPref(context);
//        ArrayList<ExitsContactBean> set = null;
//        try {
//            set = (ArrayList<ExitsContactBean>) ObjectSerializer.deserialize(Pref.sharedPreferences.getString(key, ObjectSerializer.serialize(defaultValue)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Pref.sharedPreferences = null;
//        return set;
//    }
//
//    public static void setArrayValue(Context context, String key, ArrayList<ExitsContactBean> value) {
//        Pref.openPref(context);
//        Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
//        ArrayList<ExitsContactBean> s = new ArrayList<ExitsContactBean>();
//        s.addAll(value);
//
//        try {
//            prefsPrivateEditor.putString(key, ObjectSerializer.serialize(s));
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        }
//        prefsPrivateEditor.commit();
//        Pref.sharedPreferences = null;
//
//    }


    public static void setArrayValue(Context context, String key, ArrayList<ExitsContactBean> value) {
        Pref.openPref(context);
        Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        prefsPrivateEditor.putString(key, json);
        prefsPrivateEditor.commit();
        prefsPrivateEditor = null;
        Pref.sharedPreferences = null;
    }

    public static ArrayList<ExitsContactBean> getArrayValue(Context context, String key, ArrayList<ExitsContactBean> value) {
        Pref.openPref(context);
        String result = Pref.sharedPreferences.getString(key, null);
        Pref.sharedPreferences = null;
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ExitsContactBean>>() {
        }.getType();
        ArrayList<ExitsContactBean> arrayList = gson.fromJson(result, type);
        return arrayList;
    }


}