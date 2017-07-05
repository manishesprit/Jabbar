package com.jabbar.Utils;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by hardikjani on 6/8/17.
 */

public class Config {


    //http://jabbar.rf.gd/Jabbar/change_avatar.php
//    public static String HOST = "http://jabbar.rf.gd/Jabbar/";
    public static String HOST = "https://manishpaytm001.000webhostapp.com/jabbar/";

    public static String AVATAR_HOST = HOST + "avatar/";

    public static final String API_CHANGE_FAVORITE = "change_favorite.php";
    public static String TAG_CHANGE_FAVORITE = "TAG_CHANGE_FAVORITE";

    public static final String API_GET_FAVORITE = "get_favorite_list.php";
    public static String TAG_GET_FAVORITE = "TAG_CHANGE_FAVORITE";

    public static final String API_UPDATE_STATUS = "update_status.php";
    public static String TAG_UPDATE_STATUS = "TAG_UPDATE_STATUS";

    public static final String API_GET_CONTACT_LIST = "get_contact_list.php";
    public static String TAG_GET_CONTACT_LIST = "TAG_GET_CONTACT_LIST";

    public static final String API_AUTHENTICATION = "authentication_buddies.php";
    public static String TAG_AUTHENTICATION = "TAG_AUTHENTICATION";

    public static final String API_CHANGE_AVATAR = "change_avatar.php";
    public static final String TAG_CHANGE_AVATAR = "TAG_CHANGE_AVATAR";

    public static final String API_SEND_MESSAGE = "send_message.php";
    public static String TAG_SEND_MESSAGE = "TAG_SEND_MESSAGE";

    public static final String API_CHANGE_PRIVACY = "change_privacy.php";
    public static String TAG_CHANGE_PRIVACY = "TAG_CHANGE_PRIVACY";


    public static final SimpleDateFormat WebDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat AppDateFormatter = new SimpleDateFormat("HH:mm  dd,MMM");
    public static final SimpleDateFormat AppChatDateFormatter = new SimpleDateFormat("dd,MMM yy  HH:mm");

    public static int TIMEOUT_CONNECTION = 4;

    public static int API_SUCCESS = 0;
    public static int API_FAIL = 1;

    public static ArrayList<Activity> screen_al;

    public static final String DB_NAME = "jabbar.db";
    public static final String PREF_FILE = "jabbar";
    public static final String OtpAPIKey = "e4f54f17-4c09-11e7-94da-0200cd936042";

    public static String PREF_USERID = "PREF_USERID";
    public static String PREF_NAME = "PREF_NAME";
    public static String PREF_MOBILE_NUMBER = "PREF_MOBILE_NUMBER";
    public static String PREF_AVATAR = "PREF_AVATAR";
    public static String PREF_STATUS = "PREF_STATUS";
    public static String PREF_PRIVACY = "PREF_PRIVACY";
    public static String PREF_DB_LEVEL = "PREF_DB_LEVEL";
    public static String PREF_UDID = "PREF_UDID";
    public static String PREF_PUSH_ID = "PREF_PUSH_ID";
    public static String PREF_LOCATION = "PREF_LOCATION";

    public static LatLng currentLatLong;


}
