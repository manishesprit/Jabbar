package com.jabbar.Ui;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.jabbar.R;
import com.jabbar.Utils.Log;

/**
 * Created by hardikjani on 7/19/17.
 */

public class ProfileFullViewActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_full);

        Log.print("======getIntent().getStringExtra(\"path\")====" + getIntent().getStringExtra("path"));
        ((ImageView) findViewById(R.id.imgFullProfile)).setImageBitmap(BitmapFactory.decodeFile(getIntent().getStringExtra("path")));

        ((ImageView) findViewById(R.id.imgBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
