package com.jabbar.Ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jabbar.API.AuthenticationAPI;
import com.jabbar.API.VerifyCodeAPI;
import com.jabbar.R;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

import static com.jabbar.Utils.Config.API_SUCCESS;
import static com.jabbar.Utils.Config.TAG_AUTHENTICATION;

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
                            public void OnResponse(Boolean result, final String res) {

                                if (result) {
                                    new AuthenticationAPI(VerifyCodeActivity.this, new ResponseListener() {
                                        @Override
                                        public void onResponce(String tag, int result, Object obj) {
                                            progressDialog.dismiss();
                                            if (tag.equalsIgnoreCase(TAG_AUTHENTICATION) && result == API_SUCCESS) {
                                                startActivity(new Intent(VerifyCodeActivity.this, HomeActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(VerifyCodeActivity.this, obj.toString(), Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }, getIntent().getStringExtra("number"));

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(VerifyCodeActivity.this, "Error send code.Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).execute();
                    } else {
                        Toast.makeText(VerifyCodeActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerifyCodeActivity.this, "Enter code", Toast.LENGTH_SHORT).show();
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
}
