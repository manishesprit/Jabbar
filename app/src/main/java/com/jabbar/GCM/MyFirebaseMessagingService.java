package com.jabbar.GCM;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.jabbar.Ui.AuthenticationAlertActivity;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Utils;

import org.json.JSONObject;

/**
 * Created by hardikjani on 6/23/17.
 */

public class MyFirebaseMessagingService extends GcmListenerService {

    private static final String TAG = "MyFirebaseMsgService";
    private Intent intent;
    private String msg = "";
    private int notificationid;
//    private ArrayList<NotificationBean> notificationBeanArrayList;
//    private NotificationBean notificationBean;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.print("=========From=========== " + from);


        Log.print("===========data===========" + data.get("data"));
        try {
            JSONObject jsonObject = new JSONObject(data.get("data").toString());
            if (jsonObject.has("type") && jsonObject.getInt("type") ==1) {
                Utils.closeAllScreens();
                Intent intent = new Intent(getApplicationContext(), AuthenticationAlertActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.print("========Exception:======= " + e.toString());
        }

    }


//    private void sendNotification(JSONObject jsonObject) {
//        try {
//            if (Pref.getValue(this, Config.PREF_USER_ID, 0) != 0) {
//
//                if (jsonObject.getInt("type") == 1) {
//                    notificationBeanArrayList = Pref.getNotificationArray(this, Config.PREF_NOTIFICATION, new ArrayList<NotificationBean>());
//                    notificationBean = new NotificationBean();
//                    notificationBean.feedid = Integer.parseInt(jsonObject.getString("feedid"));
//                    notificationBean.msg = jsonObject.getString("message");
//                    notificationBean.notiid = Integer.parseInt(jsonObject.getString("feedid") + "" + 111);
//                    notificationBean.avatar = jsonObject.getString("image");
//                    notificationBean.type = jsonObject.getInt("type");
//                    notificationBeanArrayList.add(notificationBean);
//                    Pref.setNotificationArray(this, Config.PREF_NOTIFICATION, notificationBeanArrayList);
//
//                    intent = new Intent(this, HomeActivity.class);
//                    intent.putExtra("type", jsonObject.getInt("type"));
//
//                    msg = "<b>" + jsonObject.getString("message") + "</b>" + " comment your post";
//                } else if (jsonObject.getInt("type") == 2) {
//
//                    notificationBeanArrayList = Pref.getNotificationArray(this, Config.PREF_NOTIFICATION, new ArrayList<NotificationBean>());
//                    notificationBean = new NotificationBean();
//                    notificationBean.userid = Integer.parseInt(jsonObject.getString("senderid"));
//                    notificationBean.msg = jsonObject.getString("message");
//                    notificationBean.notiid = Integer.parseInt(jsonObject.getString("senderid") + "" + 222);
//                    notificationBean.avatar = jsonObject.getString("image");
//                    notificationBean.type = jsonObject.getInt("type");
//                    notificationBeanArrayList.add(notificationBean);
//                    Pref.setNotificationArray(this, Config.PREF_NOTIFICATION, notificationBeanArrayList);
//
//
////                    PostBean postBean = new PostBean();
////                    postBean.userid = Integer.parseInt(jsonObject.getString("senderid"));
////                    notificationid = Integer.parseInt(jsonObject.getString("senderid") + "" + 222);
////                    postBean.name = jsonObject.getString("message");
////                    postBean.avatar = jsonObject.getString("image");
////                    postBean.noOfpost = 0;
////                    postBean.noOffollowers = 0;
////                    postBean.noOffollowing = 0;
////                    intent.putExtra("beanData", postBean);
//                    intent = new Intent(this, HomeActivity.class);
//                    intent.putExtra("type", jsonObject.getInt("type"));
//                    msg = "<b>" + jsonObject.getString("message") + "</b>" + " starting follow you";
//                }
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.large_icon)
//                        .setContentTitle("TimePass")
//                        .setContentText(Html.fromHtml(msg))
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                notificationManager.notify(notificationid, notificationBuilder.build());
//            } else {
//                Log.print("===Userid===Not");
//            }
//        } catch (Exception e) {
//            Log.print("===error===" + e.toString());
//        }
//    }
}