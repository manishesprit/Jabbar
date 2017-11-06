package com.jabbar.MyCamera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jabbar.R;
import com.jabbar.Ui.BaseActivity;
import com.jabbar.Ui.PreviewStoryImage;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Pref;
import com.jabbar.Utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by hardikjani on 11/4/17.
 */

public class MyCameraActivity extends BaseActivity implements SurfaceHolder.Callback {

    private Camera mCamera;
    public int camId = 0;
    private int mOrientation;
    private int mOrientationCompensation;
    private OrientationEventListener mOrientationEventListener;
    private int mDisplayRotation;
    private int mDisplayOrientation;
    private SurfaceView mysurface;
    public Camera.PictureCallback pictureCallback;
    private ImageView imgCapture;
    private ImageView camera_switcher;
    public static final int REQUEST_CAMERA_PERMISSIONS = 931;
    public static final int REQUEST_PREVIEW_CODE = 1001;
    private ImageView imgGallery;
    private RelativeLayout record_panel;
    private GridView rv_image_panel;
    private ArrayList<ImageBean> imageArrayList;
    private RelativeLayout rlBack;
    private ImageView conversation_contact_photo;
    private ProgressDialog progressDialog;
    private File pictureFile;
    private final CameraErrorCallback mErrorCallback = new CameraErrorCallback();

    public class CameraErrorCallback implements Camera.ErrorCallback {

        @Override
        public void onError(int error, Camera camera) {
            Log.print("Encountered an unexpected camera error: " + error);
        }
    }


    private class SimpleOrientationEventListener extends OrientationEventListener {

        public SimpleOrientationEventListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == ORIENTATION_UNKNOWN) return;
            mOrientation = Utils.roundOrientation(orientation, mOrientation);
            int orientationCompensation = mOrientation
                    + Utils.getDisplayRotation(MyCameraActivity.this);
            if (mOrientationCompensation != orientationCompensation) {
                mOrientationCompensation = orientationCompensation;
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        getCameraInstant(camId);
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (Exception e) {
            Log.sendError(e);
            Log.print("Could not preview the image." + e);
        }

    }

    public void getCameraInstant(int id) {
        try {
            mCamera = Camera.open(id);
        } catch (Exception e) {

            Log.sendError(e);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Another apps in use camera, so try after some time");
            builder.setCancelable(false);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });

            builder.show();

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.sendError(e);
            // Ignore...
        }
        Log.print("====surfaceChanged====" + width + "===" + height);
        configureCamera(width, height);
        setDisplayOrientation();
        setErrorCallback();

        mCamera.startPreview();
    }

    private void setErrorCallback() {
        mCamera.setErrorCallback(mErrorCallback);
    }

    private void setDisplayOrientation() {
        mDisplayRotation = Utils.getDisplayRotation(this);
        Log.print("====mDisplayRotation====" + mDisplayRotation);
        mDisplayOrientation = Utils.getDisplayOrientation(mDisplayRotation, 0);
        Log.print("====mDisplayOrientation====" + mDisplayOrientation);
        mCamera.setDisplayOrientation(mDisplayOrientation);
    }

    private void configureCamera(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) && getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        }
        setOptimalPreviewSize(parameters, width, height);
        setAutoFocus(parameters);

        mCamera.setParameters(parameters);
    }

    private void setOptimalPreviewSize(Camera.Parameters cameraParameters, int width, int height) {
        List<Camera.Size> previewSizes = cameraParameters.getSupportedPreviewSizes();
        float targetRatio = (float) width / height;
        Camera.Size previewSize = Utils.getOptimalPreviewSize(this, previewSizes, targetRatio);
        cameraParameters.setPreviewSize(previewSize.width, previewSize.height);
    }

    private void setAutoFocus(Camera.Parameters cameraParameters) {
        cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.setErrorCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SurfaceHolder holder = mysurface.getHolder();
        holder.addCallback(this);
    }

    @Override
    protected void onPause() {

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
                mOrientationEventListener.disable();
            }
        } else {
            mOrientationEventListener.disable();
        }
        super.onPause();

    }

    @Override
    protected void onResume() {

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
                mOrientationEventListener.enable();
            }
        } else {
            mOrientationEventListener.enable();
        }

        super.onResume();

    }


    public class ImageBean {
        public String path;
        public String DATE_TAKEN;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_mycamera);
        Utils.addActivities(this);

        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        conversation_contact_photo = (ImageView) findViewById(R.id.conversation_contact_photo);

        record_panel = (RelativeLayout) findViewById(R.id.record_panel);
        imgCapture = (ImageView) findViewById(R.id.imgCapture);
        camera_switcher = (ImageView) findViewById(R.id.camera_switcher);

        rv_image_panel = (GridView) findViewById(R.id.rv_image_panel);
        imgGallery = (ImageView) findViewById(R.id.imgGallery);

        mysurface = (SurfaceView) findViewById(R.id.mysurface);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Compressing...");
        progressDialog.setCancelable(false);

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
                mOrientationEventListener = new SimpleOrientationEventListener(this);
                mOrientationEventListener.enable();
            }
        } else {
            mOrientationEventListener = new SimpleOrientationEventListener(this);
            mOrientationEventListener.enable();
        }


        rv_image_panel.setNumColumns(4);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (!Pref.getValue(this, Config.PREF_AVATAR, "").equalsIgnoreCase(""))
            Utils.setGlideImage(this, Pref.getValue(this, Config.PREF_AVATAR, ""), conversation_contact_photo);


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

            }
        });

        pictureCallback = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                pictureFile = getOutputMediaFile();

                camera.startPreview();

                try {

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Log.print("===bitmap rotateBitmap====" + bitmap.getWidth() + "=====" + bitmap.getHeight());

                    if (bitmap == null) {

                    } else {

                        Log.print("==== bitmapnew=====" + bitmap.getWidth() + "===" + bitmap.getHeight());

                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();

                        Toast.makeText(MyCameraActivity.this, "Save photo", Toast.LENGTH_SHORT).show();

                        if (pictureFile != null && pictureFile.exists()) {

                            Log.print("=======filePath========" + pictureFile.getAbsolutePath());
                            int degree = Utils.getCameraPhotoOrientation(MyCameraActivity.this, Uri.fromFile(new File(pictureFile.getAbsolutePath())), pictureFile.getAbsolutePath());
                            Log.print("==degree===" + degree);
                            Utils.AvatarResize(Utils.rotateBitmap(BitmapFactory.decodeFile(pictureFile.getAbsolutePath()), degree), pictureFile.getAbsolutePath(), 1400);
                            Intent intent = new Intent(MyCameraActivity.this, PreviewStoryImage.class).putExtra("path", pictureFile.getAbsolutePath());
                            startActivityForResult(intent, REQUEST_PREVIEW_CODE);

                        }


                    }
                } catch (Exception e) {
                    Log.sendError(e);
                }

            }
        };


        imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCamera != null)
                    mCamera.takePicture(null, null, pictureCallback);
            }
        });

    }

    private File getOutputMediaFile() {

        File mediaFile = new File(getCacheDir().toString() + File.separator + "story.jpg");
        if (mediaFile.exists())
            mediaFile.delete();

        Log.print("=====mediaFile====" + mediaFile.getAbsolutePath());
        return mediaFile;
    }

    private void getAllShownImagesPath() {

        imageArrayList = new ArrayList<>();
        getImageList(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageArrayList);
        getImageList(MediaStore.Images.Media.INTERNAL_CONTENT_URI, imageArrayList);


        Log.print("=====Collections=====");
        Collections.sort(imageArrayList, new Comparator<MyCameraActivity.ImageBean>() {
            @Override
            public int compare(MyCameraActivity.ImageBean o1, MyCameraActivity.ImageBean o2) {
                return o2.DATE_TAKEN.compareTo(o1.DATE_TAKEN);
            }
        });

        Log.print("=========imageBeanArrayList=====" + imageArrayList.size());
        ImageAdapter imageAdapter = new ImageAdapter(this);
        rv_image_panel.setAdapter(imageAdapter);

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PREVIEW_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }


    public void getImageList(Uri uri, ArrayList<MyCameraActivity.ImageBean> imageBeanArrayList) {
        Cursor cursor;
        int column_index_data;
        int column_index_date_time;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN};
        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";
        cursor = getContentResolver().query(uri, projection, null, null, orderBy);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_date_time = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN);
        while (cursor.moveToNext()) {
            MyCameraActivity.ImageBean imageBean = new MyCameraActivity.ImageBean();
            imageBean.path = cursor.getString(column_index_data);
            imageBean.DATE_TAKEN = cursor.getString(column_index_date_time);
            imageBeanArrayList.add(imageBean);
        }
        cursor.close();
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
                    int degree = Utils.getCameraPhotoOrientation(MyCameraActivity.this, Uri.fromFile(new File(imageArrayList.get(position).path)), imageArrayList.get(position).path);
                    Log.print("==degree===" + degree);
                    Utils.AvatarResize(Utils.rotateBitmap(BitmapFactory.decodeFile(imageArrayList.get(position).path), degree), getCacheDir().toString() + "/story.jpg", 1400);
                    Intent intent = new Intent(MyCameraActivity.this, PreviewStoryImage.class).putExtra("path", getCacheDir().toString() + "/story.jpg");
                    startActivityForResult(intent, REQUEST_PREVIEW_CODE);
                }
            });

            Glide.with(context).load(imageArrayList.get(position).path)
                    .placeholder(R.drawable.ic_gallery).centerCrop()
                    .into(picturesView);

            return picturesView;
        }
    }


}
