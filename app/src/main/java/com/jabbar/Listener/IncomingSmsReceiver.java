package com.jabbar.Listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.jabbar.Ui.VerifyCodeActivity;
import com.jabbar.Utils.Log;


/**
 * Created by hardikjani on 6/12/17.
 */

public class IncomingSmsReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.print("SmsReceiver " + " senderNum: " + senderNum + "; message: " + message);
                    Log.print("=====VerifyCodeActivity.active=====" + VerifyCodeActivity.active);
                    if (VerifyCodeActivity.active) {

                        Log.print("===Code===" + message.substring(0, message.indexOf("is")).toString().trim());

                        Intent newintent = new Intent(context, VerifyCodeActivity.class);
                        newintent.putExtra("code", String.valueOf(message.substring(0, message.indexOf("is")).toString().trim()));
                        newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        newintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(newintent);

                    }

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.print("SmsReceiver " + " Exception smsReceiver" + e);

        }
    }
}
