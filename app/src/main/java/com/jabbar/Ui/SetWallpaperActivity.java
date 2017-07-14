package com.jabbar.Ui;

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

public class SetWallpaperActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgwallpaper1;
    private ImageView imgwallpaper2;
    private ImageView imgwallpaper3;
    private ImageView imgwallpaper4;
    private Toolbar toolbar;
    private LinearLayout root_view;

    private Drawable drawable;

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
            drawable = Drawable.createFromStream(getAssets().open("wallpaper1.jpg"), null);
            imgwallpaper1.setImageDrawable(drawable);

            drawable = Drawable.createFromStream(getAssets().open("wallpaper2.jpg"), null);
            imgwallpaper2.setImageDrawable(drawable);

            drawable = Drawable.createFromStream(getAssets().open("wallpaper3.jpg"), null);
            imgwallpaper3.setImageDrawable(drawable);

            drawable = Drawable.createFromStream(getAssets().open("wallpaper4.jpg"), null);
            imgwallpaper4.setImageDrawable(drawable);
        } catch (Exception e) {
        }

        imgwallpaper1.setOnClickListener(this);
        imgwallpaper2.setOnClickListener(this);
        imgwallpaper3.setOnClickListener(this);
        imgwallpaper4.setOnClickListener(this);


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
