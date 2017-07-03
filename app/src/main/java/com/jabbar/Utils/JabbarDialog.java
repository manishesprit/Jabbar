package com.jabbar.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Manish on 03-07-2017.
 */


public class JabbarDialog extends AlertDialog {
    public Context context;

    public JabbarDialog(Context context, String message) {
        super(context);
        this.setCancelable(false);
        this.setMessage(message);

        this.setButton("Ok", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
    }
}
