package com.jabbar.Ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jabbar.CameraMaster.CameraFragment;
import com.jabbar.CameraMaster.CameraFragmentApi;
import com.jabbar.CameraMaster.configuration.Configuration;
import com.jabbar.CameraMaster.listeners.CameraFragmentControlsAdapter;
import com.jabbar.CameraMaster.listeners.CameraFragmentResultAdapter;
import com.jabbar.CameraMaster.listeners.CameraFragmentResultListener;
import com.jabbar.CameraMaster.listeners.CameraFragmentStateAdapter;
import com.jabbar.CameraMaster.listeners.CameraFragmentVideoRecordTextAdapter;
import com.jabbar.CameraMaster.widgets.CameraSettingsView;
import com.jabbar.CameraMaster.widgets.CameraSwitchView;
import com.jabbar.CameraMaster.widgets.FlashSwitchView;
import com.jabbar.CameraMaster.widgets.MediaActionSwitchView;
import com.jabbar.CameraMaster.widgets.RecordButton;
import com.jabbar.R;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AddStoryActivity extends BaseActivity {


    public static final int REQUEST_CAMERA_PERMISSIONS = 931;
    public static final String FRAGMENT_TAG = "camera";
    private CameraSwitchView camera_switcher;
    private RecordButton record_button;
    private FlashSwitchView flash_switch_view;
    private TextView record_duration_text;
    private TextView record_size_mb_text;
    private MediaActionSwitchView photo_video_camera_switcher;
    private CameraSettingsView settings_view;
    public static final int REQUEST_PREVIEW_CODE = 1001;
    private ImageView imgGallery;
    private RelativeLayout record_panel;
    private GridView rv_image_panel;
    private ArrayList<ImageBean> imageArrayList;
    private RelativeLayout rlBack;
    private ImageView conversation_contact_photo;
    //    private FFmpeg fFmpeg = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_story);
        Utils.addActivities(this);

//        fFmpeg = FFmpeg.getInstance(AddStoryActivity.this);

        camera_switcher = (CameraSwitchView) findViewById(R.id.camera_switcher);
        record_button = (RecordButton) findViewById(R.id.record_button);
        flash_switch_view = (FlashSwitchView) findViewById(R.id.flash_switch_view);
        record_duration_text = (TextView) findViewById(R.id.record_duration_text);
        record_size_mb_text = (TextView) findViewById(R.id.record_size_mb_text);
        photo_video_camera_switcher = (MediaActionSwitchView) findViewById(R.id.photo_video_camera_switcher);
        settings_view = (CameraSettingsView) findViewById(R.id.settings_view);
        imgGallery = (ImageView) findViewById(R.id.imgGallery);
        rv_image_panel = (GridView) findViewById(R.id.rv_image_panel);
        record_panel = (RelativeLayout) findViewById(R.id.record_panel);
        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        conversation_contact_photo = (ImageView) findViewById(R.id.conversation_contact_photo);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Compressing...");
        progressDialog.setCancelable(false);

        rv_image_panel.setNumColumns(4);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (!Pref.getValue(this, Config.PREF_AVATAR, "").equalsIgnoreCase(""))
            Utils.setGlideImage(this, Pref.getValue(this, Config.PREF_AVATAR, ""), conversation_contact_photo);


        if (Build.VERSION.SDK_INT > 15) {
            final String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
            } else {
                addCamera();
            }
        } else {
            addCamera();
        }

        getAllShownImagesPath();

        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record_panel.setVisibility(View.GONE);
                imgGallery.setVisibility(View.GONE);
                rv_image_panel.setVisibility(View.VISIBLE);
            }
        });

        camera_switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CameraFragmentApi cameraFragment = getCameraFragment();
                if (cameraFragment != null) {
                    cameraFragment.switchCameraTypeFrontBack();
                }
            }
        });

        flash_switch_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CameraFragmentApi cameraFragment = getCameraFragment();
                if (cameraFragment != null) {
                    cameraFragment.toggleFlashMode();
                }
            }
        });

        photo_video_camera_switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CameraFragmentApi cameraFragment = getCameraFragment();
                if (cameraFragment != null) {
                    cameraFragment.switchActionPhotoVideo();
                }
            }
        });

        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CameraFragmentApi cameraFragment = getCameraFragment();
                if (cameraFragment != null) {
                    cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                        @Override
                        public void onVideoRecorded(String filePath) {
                        }

                        @Override
                        public void onPhotoTaken(byte[] bytes, String filePath) {
                        }
                    }, getCacheDir().toString(), "story");
                }
            }
        });

        settings_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CameraFragmentApi cameraFragment = getCameraFragment();
                if (cameraFragment != null) {
                    cameraFragment.openSettingDialog();
                }
            }
        });

    }

    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    @Override
    public void onBackPressed() {
        if (imgGallery.getVisibility() == View.GONE) {
            imgGallery.setVisibility(View.VISIBLE);
            record_panel.setVisibility(View.VISIBLE);
            rv_image_panel.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {

        Utils.trimCache(this);

        super.onDestroy();


    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void addCamera() {

        final CameraFragment cameraFragment = CameraFragment.newInstance(new Configuration.Builder().setCamera(Configuration.CAMERA_FACE_REAR).build());
        getSupportFragmentManager().beginTransaction().replace(R.id.content, cameraFragment, FRAGMENT_TAG).commitAllowingStateLoss();

        if (cameraFragment != null) {
            cameraFragment.setResultListener(new CameraFragmentResultListener() {
                @Override
                public void onVideoRecorded(String filePath) {
//                    LoadFFMPEG(filePath);
//                    Intent intent = new Intent(AddStoryActivity.this, PreviewStoryVideo.class).putExtra("path", filePath);
//                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }

                @Override
                public void onPhotoTaken(byte[] bytes, String filePath) {
                    Log.print("=======filePath========" + filePath);
                    int degree = Utils.getCameraPhotoOrientation(AddStoryActivity.this, Uri.fromFile(new File(filePath)), filePath);
                    Log.print("==degree===" + degree);
                    Utils.AvatarResize(Utils.rotateBitmap(BitmapFactory.decodeFile(filePath), degree), filePath, 1400);
                    Intent intent = new Intent(AddStoryActivity.this, PreviewStoryImage.class).putExtra("path", filePath);
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            });

            cameraFragment.setStateListener(new CameraFragmentStateAdapter() {

                @Override
                public void onCurrentCameraBack() {
                    camera_switcher.displayBackCamera();
                }

                @Override
                public void onCurrentCameraFront() {
                    camera_switcher.displayFrontCamera();
                }

                @Override
                public void onFlashAuto() {
                    flash_switch_view.displayFlashAuto();
                }

                @Override
                public void onFlashOn() {
                    flash_switch_view.displayFlashOn();
                }

                @Override
                public void onFlashOff() {
                    flash_switch_view.displayFlashOff();
                }

                @Override
                public void onCameraSetupForPhoto() {
                    photo_video_camera_switcher.displayActionWillSwitchVideo();

                    record_button.displayPhotoState();
                    flash_switch_view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCameraSetupForVideo() {
                    photo_video_camera_switcher.displayActionWillSwitchPhoto();

                    record_button.displayVideoRecordStateReady();
                    flash_switch_view.setVisibility(View.GONE);
                }

                @Override
                public void shouldRotateControls(int degrees) {
                    ViewCompat.setRotation(camera_switcher, degrees);
                    ViewCompat.setRotation(photo_video_camera_switcher, degrees);
                    ViewCompat.setRotation(flash_switch_view, degrees);
                    ViewCompat.setRotation(record_duration_text, degrees);
                    ViewCompat.setRotation(record_size_mb_text, degrees);
                }

                @Override
                public void onRecordStateVideoReadyForRecord() {
                    record_button.displayVideoRecordStateReady();
                }

                @Override
                public void onRecordStateVideoInProgress() {
                    record_button.displayVideoRecordStateInProgress();
                }

                @Override
                public void onRecordStatePhoto() {
                    record_button.displayPhotoState();
                }

                @Override
                public void onStopVideoRecord() {
                    record_size_mb_text.setVisibility(View.GONE);
//                    cameraSwitchView.setVisibility(View.VISIBLE);
                    settings_view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onStartVideoRecord(File outputFile) {
                }
            });

            cameraFragment.setControlsListener(new CameraFragmentControlsAdapter() {
                @Override
                public void lockControls() {
                    camera_switcher.setEnabled(false);
                    record_button.setEnabled(false);
                    settings_view.setEnabled(false);
                    flash_switch_view.setEnabled(false);
                }

                @Override
                public void unLockControls() {
                    camera_switcher.setEnabled(true);
                    record_button.setEnabled(true);
                    settings_view.setEnabled(true);
                    flash_switch_view.setEnabled(true);
                }

                @Override
                public void allowCameraSwitching(boolean allow) {
                    camera_switcher.setVisibility(allow ? View.VISIBLE : View.GONE);
                }

                @Override
                public void allowRecord(boolean allow) {
                    record_button.setEnabled(allow);
                }

                @Override
                public void setMediaActionSwitchVisible(boolean visible) {
                    photo_video_camera_switcher.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                }
            });

            cameraFragment.setTextListener(new CameraFragmentVideoRecordTextAdapter() {
                @Override
                public void setRecordSizeText(long size, String text) {
                    record_size_mb_text.setText(text);
                }

                @Override
                public void setRecordSizeTextVisible(boolean visible) {
                    record_size_mb_text.setVisibility(visible ? View.VISIBLE : View.GONE);
                }

                @Override
                public void setRecordDurationText(String text) {
                    Log.print("=====text====" + text);
                    record_duration_text.setText(text);
                }

                @Override
                public void setRecordDurationTextVisible(boolean visible) {
                    Log.print("=====visible====" + visible);
                    record_duration_text.setVisibility(visible ? View.VISIBLE : View.GONE);
                }
            });
        }
    }


    private void getAllShownImagesPath() {

        imageArrayList = new ArrayList<>();
        getImageList(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageArrayList);
        getImageList(MediaStore.Images.Media.INTERNAL_CONTENT_URI, imageArrayList);


        Log.print("=====Collections=====");
        Collections.sort(imageArrayList, new Comparator<ImageBean>() {
            @Override
            public int compare(ImageBean o1, ImageBean o2) {
                return o2.DATE_TAKEN.compareTo(o1.DATE_TAKEN);
            }
        });
        System.out.println("=========imageBeanArrayList=====" + imageArrayList.size());
        ImageAdapter imageAdapter = new ImageAdapter(this);
        rv_image_panel.setAdapter(imageAdapter);

    }

    public class ImageBean {
        public String path;
        public String DATE_TAKEN;
    }

    public void getImageList(Uri uri, ArrayList<ImageBean> imageBeanArrayList) {
        Cursor cursor;
        int column_index_data;
        int column_index_date_time;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN};
        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";
        cursor = getContentResolver().query(uri, projection, null, null, orderBy);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_date_time = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN);
        while (cursor.moveToNext()) {
            ImageBean imageBean = new ImageBean();
            imageBean.path = cursor.getString(column_index_data);
            imageBean.DATE_TAKEN = cursor.getString(column_index_date_time);
            imageBeanArrayList.add(imageBean);
        }
        cursor.close();
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PREVIEW_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private class ImageAdapter extends BaseAdapter {

        private Activity context;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        int width;

        public ImageAdapter(Activity localContext) {
            context = localContext;

            display.getSize(size);
            width = size.x;
        }

        public int getCount() {
            return imageArrayList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView.setLayoutParams(new GridView.LayoutParams((width / 4) - 5, (width / 4) - 5));

            } else {
                picturesView = (ImageView) convertView;
            }

            picturesView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.print("=======filePath========" + imageArrayList.get(position).path);
                    int degree = Utils.getCameraPhotoOrientation(AddStoryActivity.this, Uri.fromFile(new File(imageArrayList.get(position).path)), imageArrayList.get(position).path);
                    Log.print("==degree===" + degree);
                    Utils.AvatarResize(Utils.rotateBitmap(BitmapFactory.decodeFile(imageArrayList.get(position).path), degree), getCacheDir().toString() + "/story.jpg", 1400);
                    Intent intent = new Intent(AddStoryActivity.this, PreviewStoryImage.class).putExtra("path", getCacheDir().toString() + "/story.jpg");
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            });

            Glide.with(context).load(imageArrayList.get(position).path)
                    .placeholder(R.drawable.ic_gallery).centerCrop()
                    .into(picturesView);

            return picturesView;
        }
    }

//    public void LoadFFMPEG(final String path) {
//        try {
//
//            fFmpeg.loadBinary(new LoadBinaryResponseHandler() {
//
//                @Override
//                public void onStart() {
//                    if (!progressDialog.isShowing())
//                        progressDialog.show();
//                }
//
//                @Override
//                public void onFailure() {
//                    if (progressDialog.isShowing())
//                        progressDialog.dismiss();
//                    Toast.makeText(AddStoryActivity.this, "Fail compressing.Try again", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onSuccess() {
//                    ConvertVideo(path);
//                }
//
//                @Override
//                public void onFinish() {
//
//                }
//            });
//
//
//        } catch (FFmpegNotSupportedException e) {
//            // Handle if FFmpeg is not supported by device
//        }
//    }

//    public void ConvertVideo(final String path) {
//        try {
//            Log.print("=======before Convert Size====" + new File(path).length() / 1024);
//            String command = "ffmpeg -i " + path + " -b 1000000 " + path;
//            fFmpeg.execute(command.split(" "), new ExecuteBinaryResponseHandler() {
//
//                @Override
//                public void onStart() {
//                    super.onStart();
//
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    super.onFailure(message);
//                    Log.print("========onFailure===" + message);
//                    if (progressDialog.isShowing())
//                        progressDialog.dismiss();
//                    Toast.makeText(AddStoryActivity.this, "Fail compressing.Try again", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onProgress(String message) {
//                    super.onProgress(message);
//                }
//
//                @Override
//                public void onFinish() {
//                    super.onFinish();
//
//                    if (progressDialog.isShowing())
//                        progressDialog.dismiss();
//                    Toast.makeText(AddStoryActivity.this, "Compressing success", Toast.LENGTH_SHORT).show();
//                    Log.print("=======After Convert Size====" + new File(path).length() / 1024);
//                    Intent intent = new Intent(AddStoryActivity.this, PreviewStoryVideo.class).putExtra("path", path);
//                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
//                }
//            });
//        } catch (Exception e) {
//
//        }

//    }


}
