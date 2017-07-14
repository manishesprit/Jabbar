package com.jabbar.Listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jabbar.API.SendMessageNewAPI;
import com.jabbar.Bll.MessageBll;
import com.jabbar.Listener.ResponseListener;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Utils;

/**
 * Created by hardikjani on 7/11/17.
 */

public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Utils.isOnline(context)) {
            String data = new MessageBll(context).geUnsendMessageList();
            if (data != null && !SendMessageNewAPI.isCallAPI) {
                Log.print("====data=====" + data);
                new SendMessageNewAPI(context, new ResponseListener() {
                    @Override
                    public void onResponce(String tag, int result, Object obj) {

                    }
                }, data);
            }
        }

    }
}
