package com.jabbar.GCM;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.jabbar.Bean.MessageBean;
import com.jabbar.Bll.MessageBll;
import com.jabbar.Bll.UserBll;
import com.jabbar.MagicService;
import com.jabbar.MyApplication;
import com.jabbar.Ui.ChatNewActivity;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by hardikjani on 6/23/17.
 */

public class MyFirebaseMessagingService extends GcmListenerService {

    private static final String TAG = "MyFirebaseMsgService";
    private Intent intent;
    private String msg = "";
    private UserBll userBll;

    //egkzOgwkdq8:APA91bEHJ062jtOBbEmr_seKBhAPyWCMUFdVQFjwl1g3q1hSnZ7z3ttz2dXFD-GwEZrp4v7Oi9uLC_huTWN0KCZc_uZz0sstz8Kb2jBy0g3C2kdyku_tHZB5tzgW48LBzY1po61c6OMI

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.print("=========From=========== " + from);
        Log.print("===========data===========" + data.get("data"));
        try {
            JSONObject jsonObject = new JSONObject(data.get("data").toString());
            if (jsonObject.has("type") && jsonObject.getInt("type") == 1 && Pref.getValue(getApplicationContext(), Config.PREF_USERID, 0) != 0) {
                Utils.ClearAllDataAndRestartApp(getApplicationContext());
            } else if (jsonObject.has("type") && jsonObject.getInt("type") == 2) {
                if (jsonObject.has("userid") && jsonObject.getInt("userid") == Pref.getValue(getApplicationContext(), Config.PREF_USERID, 0)) {
                    userBll = new UserBll(getApplicationContext());
                    JSONObject message = jsonObject.getJSONObject("message");
                    JSONArray messageslist = message.getJSONArray("messageslist");
                    if (messageslist != null && messageslist.length() > 0) {

                        for (int i = 0; i < messageslist.length(); i++) {
                            JSONObject msgObj = messageslist.getJSONObject(i);
                            MessageBean messageBean = new MessageBean();

                            userBll.getUser(message.getInt("userid"), message.getString("mobile_number"));

                            messageBean.id = msgObj.getInt("id");
                            messageBean.userid = message.getInt("userid");
                            messageBean.friendid = Pref.getValue(getApplicationContext(), Config.PREF_USERID, 0);
                            messageBean.msg = StringEscapeUtils.unescapeJava(msgObj.getString("message"));
                            messageBean.create_time = msgObj.getString("create_time");

                            if (messageBean.msg.equalsIgnoreCase(Config.magic_alert_jabbar_code)) {
                                if (userBll.getAlertStatus(message.getInt("userid")) == 0) {
                                    startService(new Intent(getApplicationContext(), MagicService.class).putExtra("code", messageBean.msg).putExtra("userid", messageBean.userid));
                                } else if (userBll.getAlertStatus(message.getInt("userid")) == 1 && MyApplication.isAppRuning && !Utils.isMyServiceRunning(MagicService.class, this)) {
                                    startService(new Intent(getApplicationContext(), MagicService.class).putExtra("code", messageBean.msg).putExtra("userid", messageBean.userid));
                                }
                            } else {
                                if (ChatNewActivity.chatActivity != null) {
                                    Intent newintent = new Intent(getApplicationContext(), ChatNewActivity.class);
                                    newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    newintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    newintent.putExtra("messageBean", messageBean);
                                    startActivity(newintent);
                                } else if (!messageBean.msg.equalsIgnoreCase(Config.magic_rain_jabbar_code) && !messageBean.msg.equalsIgnoreCase(Config.magic_heart_jabbar_code)) {
                                    messageBean.isread = 0;
                                    new MessageBll(getApplicationContext()).InsertMessage(messageBean, true);
                                }

                            }
                        }
                    }
                }
            } else if (jsonObject.has("type") && jsonObject.getInt("type") == 3) {
            }
        } catch (Exception e) {
            Log.print("========Exception:======= " + e.toString());
        }

    }
}