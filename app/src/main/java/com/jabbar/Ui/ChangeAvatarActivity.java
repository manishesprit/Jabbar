package com.jabbar.Ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jabbar.API.ChangeAvatarAPI;
import com.jabbar.MasterCrop.Crop;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.FileUtils;
import com.jabbar.Utils.JabbarDialog;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.ResponseListener;
import com.jabbar.Utils.Utils;

import java.io.File;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static com.jabbar.Ui.StatusActivity.CHANGE_STATUS_CODE;


public class ChangeAvatarActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ImageView imgAvatar;
    private ImageView imgSelectImage;
    private EmojiconTextView txtStatus;
    private TextView txtnumber;
    private Intent intent;
    private Dialog dialog;

    public File file_avatar;
    public Uri destination;
    public File file_avatar_camara;
    public Uri destination_camara;

    public static final int OPEN_GALLERY_IMAGE_CODE = 170;
    public static final int OPEN_CAMARA_IMAGE_CODE = 180;
    private ChangeAvatarAPI changeAvatarAPI;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);
        Utils.addActivities(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        imgSelectImage = (ImageView) findViewById(R.id.imgSelectImage);
        txtStatus = (EmojiconTextView) findViewById(R.id.txtStatus);
        txtnumber = (TextView) findViewById(R.id.txtnumber);


        setToolbar(toolbar, true);

        txtStatus.setText(Pref.getValue(this, Config.PREF_STATUS, ""));
        txtnumber.setText(Pref.getValue(this, Config.PREF_MOBILE_NUMBER, ""));
        txtStatus.setOnClickListener(this);
        imgSelectImage.setOnClickListener(this);

        Glide.with(ChangeAvatarActivity.this).load(Config.AVATAR_HOST + Pref.getValue(ChangeAvatarActivity.this, Config.PREF_AVATAR, "")).asBitmap().placeholder(R.drawable.default_user).error(R.drawable.default_user).into(new BitmapImageViewTarget(imgAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
                super.setResource(resource);
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                roundedBitmapDrawable.setCircular(true);
                imgAvatar.setImageDrawable(roundedBitmapDrawable);

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtStatus) {
            startActivityForResult(new Intent(ChangeAvatarActivity.this, StatusActivity.class), CHANGE_STATUS_CODE);
        } else if (v.getId() == R.id.imgSelectImage) {
            ShowSelectImageDialog();
        }
    }


    private void ShowSelectImageDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_select_image_dialog);
        dialog.show();
        TextView txtCamera = (TextView) dialog.findViewById(R.id.txtCamera);
        TextView txtGallery = (TextView) dialog.findViewById(R.id.txtGallery);
        TextView txtRemove = (TextView) dialog.findViewById(R.id.txtRemove);
        txtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();

                capturePhoto();

            }
        });

        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();

                OpenImageGallery();

            }
        });

        txtRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();

            }
        });

    }


    public void imagePath() {
        if (file_avatar != null && file_avatar.exists())
            file_avatar.delete();

        file_avatar = new File(getApplicationInfo().dataDir, "avatar_" + Pref.getValue(this, Config.PREF_USERID, 0) + ".jpeg");
        destination = Uri.fromFile(file_avatar);

        if (file_avatar_camara != null && file_avatar_camara.exists())
            file_avatar_camara.delete();

        file_avatar_camara = new File(getExternalCacheDir(), "temp_avatar_camara_" + System.currentTimeMillis() + ".jpeg");
        destination_camara = Uri.fromFile(file_avatar_camara);
    }

    public void capturePhoto() {
        imagePath();
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, destination_camara);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, OPEN_CAMARA_IMAGE_CODE);
        }
    }

    public void OpenImageGallery() {
        imagePath();
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), OPEN_GALLERY_IMAGE_CODE);
    }


    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("============onActivityResult=============requestCode===" + requestCode + "===resultCode==" + resultCode);

        if (resultCode == RESULT_OK) {


            Log.print("=======destination=========" + destination);
            if (requestCode == CHANGE_STATUS_CODE) {
                txtStatus.setText(Pref.getValue(this, Config.PREF_STATUS, ""));
            } else if (requestCode == OPEN_GALLERY_IMAGE_CODE) {

                System.out.println("===real path===" + FileUtils.getPath(this, data.getData()));
                if (Utils.isGotoCrop(this, data.getData(), 300, 300)) {
                    Crop.of(data.getData(), destination).withAspect(1, 2, 2, 300, 300).start(this);
                } else {
                    new JabbarDialog(this, getString(R.string.mini_image_validation)).show();
                }

            } else if (requestCode == OPEN_CAMARA_IMAGE_CODE) {
                if (destination_camara != null) {
                    System.out.println("===real path===" + FileUtils.getPath(this, destination_camara));
                    if (Utils.isGotoCrop(this, destination_camara, 300, 300)) {
                        Crop.of(destination_camara, destination).withAspect(1, 2, 2, 300, 300).start(this);
                    } else {
                        new JabbarDialog(this, getString(R.string.mini_image_validation)).show();
                    }
                } else {

                }

            } else if (requestCode == Crop.REQUEST_CROP) {
                if (destination != null) {
                    Log.print("=======destination=========" + destination);
                    int degree = Utils.getCameraPhotoOrientation(this, Uri.fromFile(file_avatar), file_avatar.getPath());
                    Log.print("==degree===" + degree);
                    Utils.ConvertImage(this, Utils.rotateBitmap(BitmapFactory.decodeFile(file_avatar.getPath()), degree), file_avatar.getName());
                    imgAvatar.setImageDrawable(null);

                    if (Utils.isOnline(this)) {
                        progressDialog.show();
                        changeAvatarAPI = new ChangeAvatarAPI(this, file_avatar.getName(), new ResponseListener() {
                            @Override
                            public void onResponce(String tag, int result, Object obj) {
                                progressDialog.dismiss();
                                if (tag.equalsIgnoreCase(Config.TAG_CHANGE_AVATAR) && result == 0) {

                                    Glide.with(ChangeAvatarActivity.this).load(Config.AVATAR_HOST + Pref.getValue(ChangeAvatarActivity.this, Config.PREF_AVATAR, "")).asBitmap().placeholder(R.drawable.default_user).error(R.drawable.default_user).into(new BitmapImageViewTarget(imgAvatar) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            super.setResource(resource);
                                            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                                            roundedBitmapDrawable.setCircular(true);
                                            imgAvatar.setImageDrawable(roundedBitmapDrawable);

                                        }
                                    });

                                }
                            }
                        });
                        changeAvatarAPI.execute();
                    } else {
                        new JabbarDialog(this, getString(R.string.no_internet)).show();
                    }

                }

            }

        }
    }


}
