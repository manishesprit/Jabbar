package com.jabbar.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.model.LatLng;
import com.jabbar.Bean.ContactsBean;
import com.jabbar.Bean.ExitsContactBean;
import com.jabbar.DownloadImage;
import com.jabbar.R;
import com.jabbar.Uc.JabbarDialog;
import com.jabbar.Ui.AuthenticationAlertActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.jabbar.Utils.Config.OtpAPIKey;

/**
 * Created by hardikjani on 6/17/17.
 */

public class Utils {


    public static String getSENDAPI(String mobile) {
        return "https://2factor.in/API/V1/" + OtpAPIKey + "/SMS/+91" + mobile + "/AUTOGEN";
    }

    public static String getVerifyAPI(String code, String sessionId) {
        return "https://2factor.in/API/V1/" + OtpAPIKey + "/SMS/VERIFY/" + sessionId + "/" + code;
    }

    public interface MyListener {
        public void OnResponse(Boolean result, String res);
    }

    public static boolean isOnline(Context context) {

        try {
            ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = conMgr.getActiveNetworkInfo();
            if (info != null && info.isConnected())
                return true;
            else
                return false;

        } catch (Exception e) {
            e.printStackTrace();
            // Log.error("Utils :: isonline() ", e);
            return false;
        }
    }

    public static ArrayList<ContactsBean> getUpdateNameList(ArrayList<ContactsBean> contactsBeanArrayList, ArrayList<ExitsContactBean> contactsBeanArrayList_old) {

        for (int i = 0; i < contactsBeanArrayList.size(); i++) {
            for (int j = 0; j < contactsBeanArrayList_old.size(); j++) {
                if (contactsBeanArrayList.get(i).mobile_number.equalsIgnoreCase(contactsBeanArrayList_old.get(j).mobile_number)) {
                    contactsBeanArrayList.get(i).name = contactsBeanArrayList_old.get(j).name;
                    break;
                }
            }
        }
        return contactsBeanArrayList;
    }

    public static boolean HasPermission(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }

    public static Retrofit getRetrofit() {


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(Config.TIMEOUT_CONNECTION, TimeUnit.MINUTES);
        httpClient.readTimeout(Config.TIMEOUT_CONNECTION, TimeUnit.MINUTES);
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Config.HOST).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient.build()).build();

        return retrofit;
    }

    public static String getDeviceID(Context context) {
        String udid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Pref.setValue(context, Config.PREF_UDID, udid);
        return udid;
    }

    public static LatLng getLocationFromString(String s) {
        return new LatLng(Double.parseDouble(s.split(",")[0]), Double.parseDouble(s.split(",")[1]));
    }

    public static boolean isGotoCrop(Context context, Uri imgDestination, int minWidth, int minHeight) {
        boolean isGoto = false;
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imgDestination);
            if (bm.getWidth() > minWidth && bm.getHeight() > minHeight) {
                isGoto = true;

            } else {
                new JabbarDialog(context, "Minimum image dimension must be " + minWidth + " X " + minHeight).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.print("inside camear" + e.toString());
        }
        return isGoto;
    }


    // Orientation hysteresis amount used in rounding, in degrees
    private static final int ORIENTATION_HYSTERESIS = 5;

    public static int roundOrientation(int orientation, int orientationHistory) {
        boolean changeOrientation = false;
        if (orientationHistory == OrientationEventListener.ORIENTATION_UNKNOWN) {
            changeOrientation = true;
        } else {
            int dist = Math.abs(orientation - orientationHistory);
            dist = Math.min(dist, 360 - dist);
            changeOrientation = (dist >= 45 + ORIENTATION_HYSTERESIS);
        }
        if (changeOrientation) {
            return ((orientation + 45) / 90 * 90) % 360;
        }
        return orientationHistory;
    }

    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    public static int getDisplayOrientation(int degrees, int cameraId) {
        // See android.hardware.Camera.setDisplayOrientation for
        // documentation.
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public static int getDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

    public static Camera.Size getOptimalPreviewSize(Activity currentActivity, List<Camera.Size> sizes, double targetRatio) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.001;
        if (sizes == null) return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        Point point = getDefaultDisplaySize(currentActivity, new Point());
        int targetHeight = Math.min(point.x, point.y);
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize == null) {
            Log.print("No preview size match the aspect ratio");
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        Log.print("====optimalSize===" + optimalSize.width + "===" + optimalSize.height);
        return optimalSize;
    }

    @SuppressWarnings("deprecation")
    private static Point getDefaultDisplaySize(Activity activity, Point size) {
        Display d = activity.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            d.getSize(size);
        } else {
            size.set(d.getWidth(), d.getHeight());
        }
        return size;
    }

    public static Bitmap rotateBitmap(Bitmap b, int degrees) {

        Matrix m = new Matrix();
        if (degrees != 0) {
            // clockwise
            m.postRotate(degrees, (float) b.getWidth() / 2,
                    (float) b.getHeight() / 2);
        }
        try {
            Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                    b.getHeight(), m, true);
            if (b != b2) {
                b.recycle();
                b = b2;
            }
        } catch (OutOfMemoryError ex) {
            // We have no memory to rotate. Return the original bitmap.
        }
        return b;
    }


    public static void setGlideImage(final Context context, final String fileName, final ImageView imageView) {

        File file = new File(Utils.getAvatarDir(context), "/" + fileName);
        if (file != null && file.exists()) {
            Log.print("====setGlideImage exists====" + fileName);

            Glide.with(context).load(file.getAbsolutePath()).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(circularBitmapDrawable);
                }
            });

        } else {
            Glide.with(context).load(Config.AVATAR_HOST + fileName).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(circularBitmapDrawable);
                }
            });
            imageView.setImageResource(R.drawable.default_user);
            if (!DownloadImage.isDownloading) {
                new DownloadImage(context).execute();
            }
        }

    }

    public static void addActivities(Activity _activity) {
        if (Config.screen_al == null)
            Config.screen_al = new ArrayList<Activity>();
        if (_activity != null)
            Config.screen_al.add(_activity);
    }

    public static void closeAllScreens() {
        if (Config.screen_al != null && Config.screen_al.size() > 0) {

            for (int i = 0; i < Config.screen_al.size(); i++) {
                Activity _activity = Config.screen_al.get(i);

                if (_activity != null) {
                    _activity.finish();
                }
            }
        }
    }

    public static String ConvertArrayToString(String[] array) {
        String data = "";
        for (int i = 0; i < array.length; i++) {
            data += "," + array[i];
        }
        return data.substring(1, data.length());
    }

    public static String convertStringDateToStringDate(SimpleDateFormat oldDateFormate, SimpleDateFormat newDateFormate, String date) {
        try {
            return newDateFormate.format(oldDateFormate.parse(date));
        } catch (Exception e) {

        }
        return "";
    }

    public static String getLastSeen(SimpleDateFormat oldDateFormate, String date) {
        try {
            Date last_seen = oldDateFormate.parse(date);
            Date current_seen = new Date();

            long diff = current_seen.getTime() - last_seen.getTime();
            Log.print("======diff=====" + diff);
            Log.print("last_seen.getDay() == current_seen.getDay()======" + last_seen.getDate() + "-------" + current_seen.getDate());
            if (diff <= (1000 * 120)) {
                return "online";
            } else if (last_seen.getDate() == current_seen.getDate()) {
                return "last seen Today " + new SimpleDateFormat("HH:mm").format(last_seen);
            } else {
                return "last seen " + new SimpleDateFormat("dd MMM,yyyy").format(last_seen);
            }

        } catch (Exception e) {

        }
        return "";
    }

    public static String gettime(SimpleDateFormat oldDateFormate, String date) {
        try {
            Date last_seen = oldDateFormate.parse(date);
            Date current_seen = new Date();

            if (last_seen.getDate() == current_seen.getDate()) {
                return new SimpleDateFormat("HH:mm").format(last_seen);
            } else {
                return new SimpleDateFormat("dd-MM-yyyy").format(last_seen);
            }

        } catch (Exception e) {

        }
        return "";
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            } else {
                dir.delete();
            }
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static Typeface getFont(Context context, int tag) {

        if (tag == 100) {
            return Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        } else if (tag == 200) {
            return Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
        } else if (tag == 300) {
            return Typeface.createFromAsset(context.getAssets(), "Roboto_Bold.ttf");
        }
        return Typeface.DEFAULT;
    }

    public static void ConvertImage(final Context context, Bitmap bitmap, String fullpath) {
        try {


            File imageFile = new File(fullpath);
            OutputStream os;
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
        }
    }


    public static void AvatarResize(Bitmap b, String newpath, int maxWidth) {

        try {
            float origWidth = b.getWidth();
            float origHeight = b.getHeight();

            Log.print("===origWidth===" + origWidth + "======origHeight======" + origHeight);

            float destWidth = maxWidth;//or the width you need
            float destHeight = origHeight / (origWidth / destWidth);

            Log.print("===destWidth===" + destWidth + "======destHeight======" + destHeight);

            Bitmap b2 = Bitmap.createScaledBitmap(b, (int) destWidth, (int) destHeight, false);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            b2.compress(Bitmap.CompressFormat.JPEG, 60, outStream);
            File f = new File(newpath);
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(outStream.toByteArray());
            fo.close();
            Log.print("============After Size======" + f.length() / 1024);


        } catch (Exception e) {

        }
    }

    public static void ClearAllDataAndRestartApp(Context context) {
        Utils.closeAllScreens();
        Intent intent = new Intent(context, AuthenticationAlertActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static String getAvatarDir(Context context) {
        File file = new File(context.getApplicationInfo().dataDir + "/avatar");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    public static String getStoryDir(Context context) {
        File file = new File(context.getApplicationInfo().dataDir + "/story");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    public static String getMobileNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (tm != null) {
            if (tm.getLine1Number() != null && !tm.getLine1Number().equalsIgnoreCase("")) {
                Log.print("=====getLine1Number====" + tm.getLine1Number());
                return tm.getLine1Number();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
