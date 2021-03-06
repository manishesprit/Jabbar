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
    public static String STORY_HOST = HOST + "story/";

    public static final String API_UPDATE_STATUS = "update_status.php";
    public static String TAG_UPDATE_STATUS = "TAG_UPDATE_STATUS";

    public static final String API_GET_CONTACT_LIST = "get_contact_list.php";
    public static String TAG_GET_CONTACT_LIST = "TAG_GET_CONTACT_LIST";

    public static final String API_AUTHENTICATION = "authentication_buddies.php";
    public static String TAG_AUTHENTICATION = "TAG_AUTHENTICATION";

    public static final String API_CHANGE_AVATAR = "change_avatar.php";
    public static final String TAG_CHANGE_AVATAR = "TAG_CHANGE_AVATAR";

    public static final String API_SEND_MESSAGE = "send_message.php";
    public static final String API_SEND_NEW_MESSAGE = "send_message_new.php";
    public static String TAG_SEND_MESSAGE = "TAG_SEND_MESSAGE";

    public static final String API_CHANGE_PRIVACY = "change_privacy.php";
    public static String TAG_CHANGE_PRIVACY = "TAG_CHANGE_PRIVACY";

    public static final String API_ADD_STORY = "add_story.php";
    public static final String TAG_ADD_STORY = "TAG_ADD_STORY";

    public static final String API_GET_STORY_LIST = "get_story.php";
    public static String TAG_GET_STORY_LIST = "TAG_GET_STORY_LIST";

    public static final String API_GET_ONLINE = "get_online.php";
    public static String TAG_GET_ONLINE = "TAG_GET_ONLINE";

    public static final String API_DELETE_STORY = "remove_story.php";
    public static String TAG_DELETE_STORY = "TAG_DELETE_STORY";


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
    public static String PREF_WALLPAPER = "PREF_WALLPAPER";
    public static String PREF_STATUS = "PREF_STATUS";
    public static String PREF_PRIVACY = "PREF_PRIVACY";
    public static String PREF_DB_LEVEL = "PREF_DB_LEVEL";
    public static String PREF_UDID = "PREF_UDID";
    public static String PREF_PUSH_ID = "PREF_PUSH_ID";
    public static String PREF_LOCATION = "PREF_LOCATION";
    public static String PREF_CONTACT = "PREF_CONTACT";
    public static String PREF_WHEN_ALERT = "PREF_WHEN_ALERT";

    public static LatLng currentLatLong;

    public static final String magic_jabbar_id = "8967452301";

    public static final String magic_rain_jabbar_code = "magic_rain_jabbar_code";
    public static final String magic_alert_jabbar_code = "magic_alert_jabbar_code";
    public static final String magic_heart_jabbar_code = "magic_heart_jabbar_code";


}
