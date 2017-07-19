package com.jabbar.Ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jabbar.Bean.ContactsBean;
import com.jabbar.DownloadImage;
import com.jabbar.R;
import com.jabbar.Utils.Utils;

import java.io.File;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by hardikjani on 6/29/17.
 */

public class ProfileActivity extends BaseActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView image;
    private ContactsBean contactsBean;
    private Context context;
    private TextView txtnumber;
    private EmojiconTextView txtStatus;
    private Bitmap bitmap;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Utils.addActivities(this);
        context = this;

        if (getIntent().getExtras() != null) {
            contactsBean = (ContactsBean) getIntent().getSerializableExtra("data");
        } else {
            finish();
        }

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(contactsBean.name);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
        image = (ImageView) findViewById(R.id.image);

        txtnumber = (TextView) findViewById(R.id.txtnumber);
        txtStatus = (EmojiconTextView) findViewById(R.id.txtStatus);


        if (!contactsBean.avatar.equalsIgnoreCase("")) {
            file = new File(Utils.getAvatarDir(context) + "/" + contactsBean.avatar);
            if (file != null && file.exists()) {

                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                image.setImageBitmap(bitmap);

                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        applyPalette(palette);
                    }
                });

            } else {
                if (!DownloadImage.isDownloading) {
                    new DownloadImage(this).execute();
                }
            }
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contactsBean.avatar.equalsIgnoreCase("") && file != null && file.exists())
                    startActivity(new Intent(ProfileActivity.this, ProfileFullViewActivity.class).putExtra("path", Utils.getAvatarDir(context) + "/" + contactsBean.avatar));
            }
        });

        txtnumber.setText(contactsBean.mobile_number);
        txtStatus.setText(contactsBean.status);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        try {
            return super.dispatchTouchEvent(motionEvent);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private void applyPalette(Palette palette) {
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.colorPrimary));
        int vibrantDarkColor = palette.getDarkVibrantColor(getResources().getColor(R.color.colorPrimaryDark));

        collapsingToolbarLayout.setContentScrimColor(vibrantColor);
        collapsingToolbarLayout.setStatusBarScrimColor(vibrantDarkColor);

        supportStartPostponedEnterTransition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
