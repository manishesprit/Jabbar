package com.jabbar.Utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by hardikjani on 6/8/17.
 */

public class Config {


    public static String HOST = "https://manishpaytm001.000webhostapp.com/jabbar/";

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


    public static int TIMEOUT_CONNECTION = 20000;

    public static int API_SUCCESS = 0;
    public static int API_FAIL = 1;


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
    public static String PREF_LOCATION = "PREF_LOCATION";

    public static LatLng currentLatLong;


}