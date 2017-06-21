package com.jabbar.Ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jabbar.API.VerifyCodeAPI;
import com.jabbar.R;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Utils;

import java.util.Arrays;

public class VerifyCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    public static boolean active = false;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        findViewById(R.id.btnVerify).setOnClickListener(this);


        Log.print("======onCreate=====");
    }

    private EditText getEdtcode() {
        return (EditText) findViewById(R.id.edtcode);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnVerify:
                if (!getEdtcode().getText().toString().trim().equalsIgnoreCase("") && getEdtcode().getText().toString().trim().length() > 3) {
                    if (Utils.isOnline(VerifyCodeActivity.this)) {
                        progressDialog.show();
                        new VerifyCodeAPI(VerifyCodeActivity.this, getEdtcode().getText().toString().trim(), getIntent().getStringExtra("session_id"), new Utils.MyListener() {
                            @Override
                            public void OnResponse(Boolean result, String res) {
                                progressDialog.dismiss();
                                if (result) {
                                    startActivity(new Intent(VerifyCodeActivity.this, HomeActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(VerifyCodeActivity.this, "Error send code.Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).execute();
                    }
                } else {
                }
                break;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.print("======Intent=======" + intent.getStringExtra("code"));
        getEdtcode().setText(intent.getStringExtra("code"));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((Button) findViewById(R.id.btnVerify)).performClick();
            }
        }, 1000);


    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    public static String intentToString(Intent intent) {
        if (intent == null) {
            return null;
        }

        return intent.toString() + " " + bundleToString(intent.getExtras());
    }

    public static String bundleToString(Bundle bundle) {
        StringBuilder out = new StringBuilder("Bundle[");

        if (bundle == null) {
            out.append("null");
        } else {
            boolean first = true;
            for (String key : bundle.keySet()) {
                if (!first) {
                    out.append(", ");
                }

                out.append(key).append('=');

                Object value = bundle.get(key);

                if (value instanceof int[]) {
                    out.append(Arrays.toString((int[]) value));
                } else if (value instanceof byte[]) {
                    out.append(Arrays.toString((byte[]) value));
                } else if (value instanceof boolean[]) {
                    out.append(Arrays.toString((boolean[]) value));
                } else if (value instanceof short[]) {
                    out.append(Arrays.toString((short[]) value));
                } else if (value instanceof long[]) {
                    out.append(Arrays.toString((long[]) value));
                } else if (value instanceof float[]) {
                    out.append(Arrays.toString((float[]) value));
                } else if (value instanceof double[]) {
                    out.append(Arrays.toString((double[]) value));
                } else if (value instanceof String[]) {
                    out.append(Arrays.toString((String[]) value));
                } else if (value instanceof CharSequence[]) {
                    out.append(Arrays.toString((CharSequence[]) value));
                } else if (value instanceof Parcelable[]) {
                    out.append(Arrays.toString((Parcelable[]) value));
                } else if (value instanceof Bundle) {
                    out.append(bundleToString((Bundle) value));
                } else {
                    out.append(value);
                }

                first = false;
            }
        }

        out.append("]");
        return out.toString();
    }
}
