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

import com.google.firebase.iid.FirebaseInstanceId;
import com.jabbar.API.SendMobileAPI;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.GetLocation;
import com.jabbar.Uc.JabbarDialog;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.UpdateContact1;
import com.jabbar.Utils.Utils;


public class InputDataActivity extends AppCompatActivity implements View.OnClickListener, GetLocation.MyLocationListener {

    private ProgressDialog progressDialog;
    public static final int PERMISSION_CODE = 150;
    private GetLocation getLocation;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);
        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        new UpdateContact1(this).execute();

//        getEdtnumber().setText("8735032992");


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
                        progressDialog.show();
                        new SendMobileAPI(InputDataActivity.this, getEdtnumber().getText().toString().trim(), new Utils.MyListener() {
                            @Override
                            public void OnResponse(Boolean result, String res) {
                                Log.print("=====res========" + res);
                                progressDialog.dismiss();
                                if (result) {
                                    startActivity(new Intent(InputDataActivity.this, VerifyCodeActivity.class).putExtra("session_id", res).putExtra("number", getEdtnumber().getText().toString().trim()));
                                    finish();
                                } else {
                                    Toast.makeText(InputDataActivity.this, "Error send code.Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).execute();
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
