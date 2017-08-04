package com.jabbar.Ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jabbar.API.AuthenticationAPI;
import com.jabbar.Bean.ExitsContactBean;
import com.jabbar.Listener.ResponseListener;
import com.jabbar.R;
import com.jabbar.Uc.JabbarDialog;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.GetLocation;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.UpdateContact1;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.jabbar.Utils.Config.API_SUCCESS;
import static com.jabbar.Utils.Config.TAG_AUTHENTICATION;


public class InputDataActivity extends AppCompatActivity implements View.OnClickListener, GetLocation.MyLocationListener {

    private ProgressDialog progressDialog;
    private GetLocation getLocation;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    public boolean isLast = true;
    public SmsManager smsManager;
    public BroadcastReceiver sendMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);
        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        new UpdateContact1(this).execute();

        Pref.setValue(this, Config.PREF_PUSH_ID, FirebaseInstanceId.getInstance().getToken());
        Log.print("=======PREF_PUSH_ID===========" + FirebaseInstanceId.getInstance().getToken());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Pref.setValue(InputDataActivity.this, Config.PREF_PUSH_ID, FirebaseInstanceId.getInstance().getToken());
                Log.print("=======PREF_PUSH_ID===========" + FirebaseInstanceId.getInstance().getToken());
                if (Pref.getValue(InputDataActivity.this, Config.PREF_PUSH_ID, "").equalsIgnoreCase("")) {
                    Toast.makeText(InputDataActivity.this, "Push id not available", Toast.LENGTH_SHORT).show();
                }
            }
        }, 2000);

        findViewById(R.id.btnVerify).setOnClickListener(this);


        getLocation = new GetLocation(this, this);
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!statusOfGPS) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Please enable gps");
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });


        } else {
            getLocation.UpdateLocation();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getLocation != null)
            getLocation.UpdateLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private EditText getEdtnumber() {
        return (EditText) findViewById(R.id.edtnumber);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnVerify:
                if (getEdtnumber().getText().toString().trim().equals("")) {
                    new JabbarDialog(InputDataActivity.this, "please enter mobile number").show();
                } else if (getEdtnumber().getText().toString().trim().length() != 10) {
                    new JabbarDialog(InputDataActivity.this, "please enter mobile number fix 10 digit").show();
                } else {
                    if (Pref.getValue(InputDataActivity.this, Config.PREF_PUSH_ID, "") != null && !Pref.getValue(InputDataActivity.this, Config.PREF_PUSH_ID, "").equalsIgnoreCase("")) {
                        if (Utils.isOnline(InputDataActivity.this)) {
                            if (!progressDialog.isShowing())
                                progressDialog.show();
                            if (Utils.getMobileNumber(InputDataActivity.this).length() == 13 && Utils.getMobileNumber(InputDataActivity.this).equalsIgnoreCase("+91" + getEdtnumber().getText().toString().trim())) {
                                DirectLogin();
                            } else if (Utils.getMobileNumber(InputDataActivity.this).length() == 11 && Utils.getMobileNumber(InputDataActivity.this).equalsIgnoreCase("+" + getEdtnumber().getText().toString().trim())) {
                                DirectLogin();
                            } else if (Utils.getMobileNumber(InputDataActivity.this).length() == 10 && Utils.getMobileNumber(InputDataActivity.this).equalsIgnoreCase(getEdtnumber().getText().toString().trim())) {
                                DirectLogin();
                            } else {
                                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + getEdtnumber().getText().toString().trim(), 60, TimeUnit.SECONDS, InputDataActivity.this, onVerificationStateChangedCallbacks);
                            }

                        } else {
                            new JabbarDialog(InputDataActivity.this, getString(R.string.no_internet)).show();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void getLoc(boolean isUpdate) {

    }

    public PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

//            sendDirectMsg();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ArrayList<ExitsContactBean> exitsContactBeanArrayList = Pref.getArrayValue(InputDataActivity.this, Config.PREF_CONTACT, new ArrayList<ExitsContactBean>());
                    new AuthenticationAPI(InputDataActivity.this, new ResponseListener() {
                        @Override
                        public void onResponce(String tag, int result, Object obj) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            if (tag.equalsIgnoreCase(TAG_AUTHENTICATION) && result == API_SUCCESS) {
                                startActivity(new Intent(InputDataActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                Toast.makeText(InputDataActivity.this, obj.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, getEdtnumber().getText().toString().trim(), exitsContactBeanArrayList);
                }
            }, 6000);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            Toast.makeText(InputDataActivity.this, "Error send code.Try again", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            startActivity(new Intent(InputDataActivity.this, VerifyCodeActivity.class).putExtra("veriId", s).putExtra("number", getEdtnumber().getText().toString().trim()));
            finish();
        }
    };

    public void DirectLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<ExitsContactBean> exitsContactBeanArrayList = Pref.getArrayValue(InputDataActivity.this, Config.PREF_CONTACT, new ArrayList<ExitsContactBean>());
                new AuthenticationAPI(InputDataActivity.this, new ResponseListener() {
                    @Override
                    public void onResponce(String tag, int result, Object obj) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        if (tag.equalsIgnoreCase(TAG_AUTHENTICATION) && result == API_SUCCESS) {
                            startActivity(new Intent(InputDataActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(InputDataActivity.this, obj.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, getEdtnumber().getText().toString().trim(), exitsContactBeanArrayList);
            }
        }, 6000);
    }

    public void sendDirectMsg() {
        smsManager = SmsManager.getDefault();
        final String getRandomCode = String.valueOf(System.currentTimeMillis()).toString().substring(0, 6);
        final String phoneNumber = getEdtnumber().getText().toString().trim();
        final String smsBody = getRandomCode + " is code.";
        String SMS_SENT = "SMS_SENT";
        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);

        sendMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                try {
                    Log.print("Deleting SMS from inbox");
                    Uri uriSms = Uri.parse("content://sms/");
                    Cursor c = context.getContentResolver().query(uriSms, new String[]{"_id", "thread_id", "address", "body", "type"}, null, null, null);

                    if (c != null && c.moveToFirst()) {
                        do {
                            Log.print("====c.getString(4)====" + c.getString(4) + "===c.getString(2)====" + c.getString(2) + "===c.getString(3)===" + c.getString(3));
                            if (!c.getString(4).contains("1") && c.getString(2).equalsIgnoreCase(phoneNumber) && c.getString(3).equalsIgnoreCase(smsBody)) {
                                context.getContentResolver().delete(Uri.parse("content://sms/" + c.getLong(0)), null, null);
                                break;
                            }
                        } while (c.moveToNext());
                    }
                } catch (Exception e) {
                    Log.print("Could not delete SMS from inbox: " + e.getMessage());
                }

                if (getResultCode() == Activity.RESULT_OK) {

                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    startActivity(new Intent(InputDataActivity.this, VerifyCodeActivity.class).putExtra("isDirect", true).putExtra("number", phoneNumber).putExtra("internal_code", getRandomCode));
                    finish();

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<ExitsContactBean> exitsContactBeanArrayList = Pref.getArrayValue(InputDataActivity.this, Config.PREF_CONTACT, new ArrayList<ExitsContactBean>());
                            new AuthenticationAPI(InputDataActivity.this, new ResponseListener() {
                                @Override
                                public void onResponce(String tag, int result, Object obj) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    if (tag.equalsIgnoreCase(TAG_AUTHENTICATION) && result == API_SUCCESS) {
                                        startActivity(new Intent(InputDataActivity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(InputDataActivity.this, obj.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, getEdtnumber().getText().toString().trim(), exitsContactBeanArrayList);
                        }
                    }, 6000);

                }

            }
        };
        registerReceiver(sendMessageReceiver, new IntentFilter(SMS_SENT));

        smsManager.sendTextMessage(phoneNumber, null, smsBody, sentPendingIntent, null);
    }

    public void deleteSMS(Context context, String message, String number) {
        try {
            Log.print("Deleting SMS from inbox");
            Uri uriSms = Uri.parse("content://sms/");
            Cursor c = context.getContentResolver().query(uriSms, new String[]{"_id", "thread_id", "address", "body", "type"}, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    if (!c.getString(4).contains("1") && c.getString(2).equalsIgnoreCase(number) && c.getString(3).equalsIgnoreCase(message)) {
                        context.getContentResolver().delete(Uri.parse("content://sms/" + c.getLong(0)), null, null);
                        break;
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.print("Could not delete SMS from inbox: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sendMessageReceiver != null) {
            unregisterReceiver(sendMessageReceiver);
        }
    }
}
