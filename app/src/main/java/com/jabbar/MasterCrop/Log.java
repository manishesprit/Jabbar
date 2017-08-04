package com.jabbar.MasterCrop;

class Log {

    private static final String TAG = "android-crop";

    public static void e(String msg) {
        com.jabbar.Utils.Log.print(msg);
    }

    public static void e(String msg, Throwable e) {
        com.jabbar.Utils.Log.print(msg);
    }

}
