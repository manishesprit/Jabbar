package com.jabbar.Ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jabbar.API.AddStoryAPI;
import com.jabbar.CameraMaster.internal.enums.MediaAction;
import com.jabbar.ImageViewer.DecodeUtils;
import com.jabbar.ImageViewer.ImageViewTouch;
import com.jabbar.ImageViewer.ImageViewTouchBase;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

import org.apache.commons.lang3.StringEscapeUtils;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by hardikjani on 7/6/17.
 */

public class PreviewStoryImage extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private final static String MEDIA_ACTION_ARG = "media_action_arg";
    private final static String FILE_PATH_ARG = "file_path_arg";
    private final static String FILE_STORAGE = "FILE_STORAGE";

    private int mediaAction;
    private boolean isStorage;
    private RelativeLayout rlBack;
    private ImageView conversation_contact_photo;
    ImageViewTouch mImage;
    Bitmap bitmap = null;
    private EmojiconEditText edit_msg;
    private ImageView img_emoji;
    private RelativeLayout rel_send;
    private ImageView imgSend;
    private EmojIconActions emojIcon;
    private String fileName;


    public static Intent newIntentPhoto(Context context, String filePath, boolean isStorage) {
        return new Intent(context, PreviewStoryImage.class)
                .putExtra(MEDIA_ACTION_ARG, MediaAction.ACTION_PHOTO)
                .putExtra(FILE_PATH_ARG, filePath)
                .putExtra(FILE_STORAGE, isStorage);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview_story_image);
        Bundle args = getIntent().getExtras();


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        mediaAction = args.getInt(MEDIA_ACTION_ARG);

        mImage = (ImageViewTouch) findViewById(R.id.image);
        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        conversation_contact_photo = (ImageView) findViewById(R.id.conversation_contact_photo);

        edit_msg = (EmojiconEditText) findViewById(R.id.edit_msg);
        img_emoji = (ImageView) findViewById(R.id.img_emoji);
        rel_send = (RelativeLayout) findViewById(R.id.rel_send);
        imgSend = (ImageView) findViewById(R.id.img_send);

        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnline(PreviewStoryImage.this)) {
                    progressDialog.show();
                    new AddStoryAPI(PreviewStoryImage.this, fileName, StringEscapeUtils.escapeJava(edit_msg.getText().toString().trim()), new ResponseListener() {
                        @Override
                        public void onResponce(String tag, int result, Object obj) {
                            progressDialog.dismiss();
                            if (tag.equalsIgnoreCase(Config.TAG_ADD_STORY) && result == 0) {

                            }
                        }
                    });
                } else {

                }
            }
        });

        Utils.setGlideImage(this, Pref.getValue(this, Config.PREF_AVATAR, ""), conversation_contact_photo, true);

        img_emoji.setOnClickListener(null);
        emojIcon = new EmojIconActions(this, rel_send, edit_msg, img_emoji);
        emojIcon.setUseSystemEmoji(false);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);


    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mImage = (ImageViewTouch) findViewById(R.id.image);
        mImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_IF_BIGGER);
        fileName = getIntent().getStringExtra(FILE_PATH_ARG);
        isStorage = getIntent().getBooleanExtra(FILE_PATH_ARG, false);
        LoadImage(Uri.parse(getIntent().getStringExtra(FILE_PATH_ARG)));
        mImage.setSingleTapListener(
                new ImageViewTouch.OnImageViewTouchSingleTapListener() {

                    @Override
                    public void onSingleTapConfirmed() {
                        com.jabbar.Utils.Log.print("onSingleTapConfirmed");
                    }
                }
        );

        mImage.setDoubleTapListener(
                new ImageViewTouch.OnImageViewTouchDoubleTapListener() {

                    @Override
                    public void onDoubleTap() {
                        com.jabbar.Utils.Log.print("onDoubleTap");
                    }
                }
        );

        mImage.setOnDrawableChangedListener(
                new ImageViewTouchBase.OnDrawableChangeListener() {

                    @Override
                    public void onDrawableChanged(Drawable drawable) {
                        com.jabbar.Utils.Log.print("onBitmapChanged: " + drawable);
                    }
                }
        );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    Matrix imageMatrix;

    public void LoadImage(Uri imageUri) {

        Log.print(imageUri.toString());

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        int size = (int) (Math.min(metrics.widthPixels, metrics.heightPixels) / 0.55);

//        if ("http".equals(imageUri.getScheme()) || "https".equals(imageUri.getScheme())) {
//
//            Glide.with(this).load(imageUri).asBitmap().placeholder(R.drawable.ic_gallery).error(R.drawable.ic_gallery).into(new BitmapImageViewTarget(mImage) {
//                @Override
//                protected void setResource(Bitmap resource) {
//                    super.setResource(resource);
//                    bitmap = resource;
//
//                    if (null != bitmap) {
//                        com.jabbar.Utils.Log.print("screen size: " + metrics.widthPixels + "x" + metrics.heightPixels);
//                        com.jabbar.Utils.Log.print("bitmap size: " + bitmap.getWidth() + "x" + bitmap.getHeight());
//
//                        mImage.setOnDrawableChangedListener(
//                                new ImageViewTouchBase.OnDrawableChangeListener() {
//                                    @Override
//                                    public void onDrawableChanged(final Drawable drawable) {
//                                        com.jabbar.Utils.Log.print("image scale: " + mImage.getScale() + "/" + mImage.getMinScale());
//                                        com.jabbar.Utils.Log.print("scale type: " + mImage.getDisplayType() + "/" + mImage.getScaleType());
//
//                                    }
//                                }
//                        );
//                        mImage.setImageBitmap(bitmap, null, -1, -1);
//
//                    } else {
//                        Toast.makeText(this, "Failed to load the image", Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//        } else {
        bitmap = DecodeUtils.decode(this, imageUri, size, size);

        if (null != bitmap) {
            com.jabbar.Utils.Log.print("screen size: " + metrics.widthPixels + "x" + metrics.heightPixels);
            com.jabbar.Utils.Log.print("bitmap size: " + bitmap.getWidth() + "x" + bitmap.getHeight());

            mImage.setOnDrawableChangedListener(
                    new ImageViewTouchBase.OnDrawableChangeListener() {
                        @Override
                        public void onDrawableChanged(final Drawable drawable) {
                            com.jabbar.Utils.Log.print("image scale: " + mImage.getScale() + "/" + mImage.getMinScale());
                            com.jabbar.Utils.Log.print("scale type: " + mImage.getDisplayType() + "/" + mImage.getScaleType());

                        }
                    }
            );
            mImage.setImageBitmap(bitmap, null, -1, -1);

        } else {
            Toast.makeText(this, "Failed to load the image", Toast.LENGTH_LONG).show();
        }
//        }


    }
}
