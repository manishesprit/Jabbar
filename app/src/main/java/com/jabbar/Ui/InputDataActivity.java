package com.jabbar.Ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.jabbar.R;
import com.jabbar.Uc.JabbarDialog;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.GetLocation;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.UpdateContact1;
import com.jabbar.Utils.Utils;

import java.util.concurrent.TimeUnit;


public class InputDataActivity extends AppCompatActivity implements View.OnClickListener, GetLocation.MyLocationListener {

    private ProgressDialog progressDialog;
    public static final int PERMISSION_CODE = 150;
    private GetLocation getLocation;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;

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


        findViewById(R.id.btnVerify).setOnClickListener(this);


        if (!Utils.HasPermission(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (String s : permissions) {
            Log.print("====permissions===" + s);
        }

        if (requestCode == PERMISSION_CODE && grantResults.length == 1) {

        } else {
            new JabbarDialog(this, "Permission is importent").show();
        }
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
                    if (Utils.isOnline(InputDataActivity.this)) {
                        if (!progressDialog.isShowing())
                            progressDialog.show();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + getEdtnumber().getText().toString().trim(), 60, TimeUnit.SECONDS, InputDataActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(FirebaseException e) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Toast.makeText(InputDataActivity.this, "Error send code.Try again", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                startActivity(new Intent(InputDataActivity.this, VerifyCodeActivity.class).putExtra("veriId", s).putExtra("number", getEdtnumber().getText().toString().trim()));
                                finish();
                            }
                        });
                    } else {
                        new JabbarDialog(InputDataActivity.this, getString(R.string.no_internet)).show();
                    }
                }
                break;
        }
    }

    @Override
    public void getLoc(boolean isUpdate) {

    }
}
