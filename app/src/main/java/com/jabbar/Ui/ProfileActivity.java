package com.jabbar.Ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.R;
import com.jabbar.Utils.Config;

/**
 * Created by hardikjani on 6/29/17.
 */

public class ProfileActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView image;
    private ContactsBean contactsBean;
    private Context context;
    private TextView txtnumber;
    private TextView txtStatus;
    private Bitmap bitmap;
//    private LinearLayout llDownload;
//    private TextView txtDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_profile);
        context = this;

        if (getIntent().getExtras() != null) {
            contactsBean = (ContactsBean) getIntent().getSerializableExtra("data");
//        contactsBean = new ContactsBean();
//        contactsBean.avatar = "avatar_5.jpeg";
//        contactsBean.userid = 5;
//        contactsBean.status = "Hi! i'm using jabbar";
//        contactsBean.name = "Manish Rathod";
//        contactsBean.mobile_number = "9904841033";

        } else {
            finish();
        }

        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), "com.antonioleiva.materializeyourapp.extraImage");
        supportPostponeEnterTransition();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(contactsBean.name);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.white));
        image = (ImageView) findViewById(R.id.image);

        txtnumber = (TextView) findViewById(R.id.txtnumber);
        txtStatus = (TextView) findViewById(R.id.txtStatus);


        Glide.with(ProfileActivity.this).load(Config.AVATAR_HOST + contactsBean.avatar)
                .asBitmap()
                .error(R.drawable.default_user).placeholder(R.drawable.default_user).error(R.drawable.default_user).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                image.setImageBitmap(resource);
                bitmap = resource;
                Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {

                        applyPalette(palette);
                    }
                });
            }
        });

//        image.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                llDownload.setVisibility(View.VISIBLE);
//                return false;
//            }
//        });
//
//        llDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                llDownload.setVisibility(View.GONE);
//            }
//        });
//
//
//        txtDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                llDownload.setVisibility(View.GONE);
//                Utils.DownloadImage(context, bitmap, "Image_" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()).toString() + ".jpg", Config.DIR_USERDATA);
//            }
//        });


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
}