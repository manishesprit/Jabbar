package com.jabbar.Ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import java.util.ArrayList;

import static com.jabbar.Utils.Config.API_SUCCESS;
import static com.jabbar.Utils.Config.TAG_AUTHENTICATION;

public class VerifyCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    public static boolean active = false;
    private Toolbar toolbar;
    private String number;
    private String veriId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);
        Utils.addActivities(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        mAuth = FirebaseAuth.getInstance();

        number = getIntent().getStringExtra("number");
        veriId = getIntent().getStringExtra("veriId");
        Log.print("==============number==========" + number + "==========session_id======" + veriId);


        findViewById(R.id.btnVerify).setOnClickListener(this);

        Pref.setValue(this, Config.PREF_PUSH_ID, FirebaseInstanceId.getInstance().getToken());
        Log.print("=======PREF_PUSH_ID===========" + FirebaseInstanceId.getInstance().getToken());
        if (Pref.getValue(this, Config.PREF_PUSH_ID, "").equalsIgnoreCase("")) {
            Toast.makeText(this, "Push id not available", Toast.LENGTH_SHORT).show();
        }

        Log.print("======onCreate=====");


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            ArrayList<ExitsContactBean> exitsContactBeanArrayList = Pref.getArrayValue(VerifyCodeActivity.this, Config.PREF_CONTACT, new ArrayList<ExitsContactBean>());
                            new AuthenticationAPI(VerifyCodeActivity.this, new ResponseListener() {
                                @Override
                                public void onResponce(String tag, int result, Object obj) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    if (tag.equalsIgnoreCase(TAG_AUTHENTICATION) && result == API_SUCCESS) {
                                        startActivity(new Intent(VerifyCodeActivity.this, HomeActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(VerifyCodeActivity.this, obj.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, number, exitsContactBeanArrayList);

                        } else {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                            Toast.makeText(VerifyCodeActivity.this, "Error send code.Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                        if (!progressDialog.isShowing())
                            progressDialog.show();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(veriId, getEdtcode().getText().toString().trim());
                        signInWithPhoneAuthCredential(credential);

                    } else {
                        new JabbarDialog(VerifyCodeActivity.this, getString(R.string.no_internet)).show();
                    }
                } else {
                    new JabbarDialog(VerifyCodeActivity.this, "Enter code").show();
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
