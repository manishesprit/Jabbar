package com.jabbar;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jabbar.Bll.UserBll;
import com.jabbar.Utils.Config;

/**
 * Created by hardikjani on 8/4/17.
 */

public class MagicService extends Service {

    private WindowManager windowManager;
    private View view;
    ImageView imgMagic;
    private MediaPlayer mediaPlayer;
    private Animation anim_heart;
    int curAnim = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);


        if (intent.getStringExtra("code").equalsIgnoreCase(Config.magic_alert_jabbar_code)) {

            String username = new UserBll(getApplicationContext()).getUsername(intent.getIntExtra("userid", 0));

            if (username != null) {
                view = LayoutInflater.from(this).inflate(R.layout.layout_magic_alert, null);
                windowManager.addView(view, params);

                TextView txtName = (TextView) view.findViewById(R.id.txtName);
                RelativeLayout imgAnim = (RelativeLayout) view.findViewById(R.id.imgAnim);
                txtName.setText(username + " wants attention");

                ObjectAnimator
                        .ofFloat(imgAnim, "translationY", 0, 100, -100, 100, -100, 50, -50, 50, -50, 25, -25, 25, -25, 15, -15, 10, -10, 0)
                        .setDuration(2500)
                        .start();

                mediaPlayer = MediaPlayer.create(this, R.raw.alert_tone);
                mediaPlayer.start();
            }


        } else if (intent.getStringExtra("code").equalsIgnoreCase(Config.magic_rain_jabbar_code)) {
            view = LayoutInflater.from(this).inflate(R.layout.layout_magic, null);
            windowManager.addView(view, params);
            imgMagic = (ImageView) view.findViewById(R.id.imgSnake);

            ViewTreeObserver vto = imgMagic.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    imgMagic.getViewTreeObserver().removeOnPreDrawListener(this);
                    int toY = -(imgMagic.getMeasuredHeight());

                    System.out.println("==toY==" + toY);
                    Display display = windowManager.getDefaultDisplay();
                    TranslateAnimation animation = new TranslateAnimation(0, 0, toY, 0);
                    animation.setDuration(2500);

                    imgMagic.clearAnimation();
                    imgMagic.setAnimation(animation);
                    imgMagic.startAnimation(animation);

                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alert_tone);
                    mediaPlayer.start();

                    return true;
                }
            });
        } else if (intent.getStringExtra("code").equalsIgnoreCase(Config.magic_heart_jabbar_code)) {
            view = LayoutInflater.from(this).inflate(R.layout.layout_magic_heart, null);
            windowManager.addView(view, params);
            imgMagic = (ImageView) view.findViewById(R.id.imgMagic);

            // load the zoom animation
            anim_heart = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.heart);
            imgMagic.startAnimation(anim_heart);

            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alert_tone);
            mediaPlayer.start();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, 3000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();

            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (windowManager != null) {
            windowManager.removeViewImmediate(view);
        }
    }
}
