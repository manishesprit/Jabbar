package com.jabbar.Ui;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import java.io.IOException;
import java.io.InputStream;

public class SetWallpaperActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgwallpaper1;
    private ImageView imgwallpaper2;
    private ImageView imgwallpaper3;
    private ImageView imgwallpaper4;
    private Toolbar toolbar;
    private LinearLayout root_view;

    private Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_wallpaper);

        Utils.addActivities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setToolbar(toolbar, true);

        root_view = (LinearLayout) findViewById(R.id.root_view);

        imgwallpaper1 = (ImageView) findViewById(R.id.imgwallpaper1);
        imgwallpaper2 = (ImageView) findViewById(R.id.imgwallpaper2);
        imgwallpaper3 = (ImageView) findViewById(R.id.imgwallpaper3);
        imgwallpaper4 = (ImageView) findViewById(R.id.imgwallpaper4);

        try {

            bm = getBitmapFromAsset("wallpaper1.jpg");
            imgwallpaper1.setImageBitmap(bm);

            bm = getBitmapFromAsset("wallpaper2.jpg");
            imgwallpaper2.setImageBitmap(bm);

            bm = getBitmapFromAsset("wallpaper3.jpg");
            imgwallpaper3.setImageBitmap(bm);

            bm = getBitmapFromAsset("wallpaper4.jpg");
            imgwallpaper4.setImageBitmap(bm);

        } catch (Exception e) {
        }

        imgwallpaper1.setOnClickListener(this);
        imgwallpaper2.setOnClickListener(this);
        imgwallpaper3.setOnClickListener(this);
        imgwallpaper4.setOnClickListener(this);


    }

    private Bitmap getBitmapFromAsset(String strName) throws IOException {
        AssetManager assetManager = getAssets();

        InputStream istr = assetManager.open(strName);
        Bitmap bitmap = BitmapFactory.decodeStream(istr);

        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgwallpaper1:
                Pref.setValue(this, Config.PREF_WALLPAPER, "wallpaper1.jpg");
                finish();
                break;

            case R.id.imgwallpaper2:
                Pref.setValue(this, Config.PREF_WALLPAPER, "wallpaper2.jpg");
                finish();
                break;

            case R.id.imgwallpaper3:
                Pref.setValue(this, Config.PREF_WALLPAPER, "wallpaper3.jpg");
                finish();
                break;

            case R.id.imgwallpaper4:
                Pref.setValue(this, Config.PREF_WALLPAPER, "wallpaper4.jpg");
                finish();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bm != null) {
            bm.recycle();
        }

        Utils.trimCache(this);
        Runtime.getRuntime().gc();
        unbindDrawables(root_view);
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }
}
