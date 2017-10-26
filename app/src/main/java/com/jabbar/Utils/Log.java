package com.jabbar.Utils;

public class Log {

    /* Logging and Console */
    public static boolean DO_SOP = false;

    public static void print(String mesg) {
        if (Log.DO_SOP) {
            System.out.println(mesg);
        }
    }

    public static void sendError(Exception e) {
    }


    public static String htmlEncode(String str) {
        return str.replaceAll(">", "&lt;").replaceAll("<", "&gt;")
                .replaceAll("&", "&amp;").replaceAll("\"", "&quot;")
                .replaceAll("'", "&#039;");
    }
}