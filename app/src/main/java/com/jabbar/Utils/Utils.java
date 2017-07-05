package com.jabbar.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.jabbar.Bean.ContactsBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    public static ArrayList<ContactsBean> getUpdateNameList(ArrayList<ContactsBean> contactsBeanArrayList, ArrayList<ContactsBean> contactsBeanArrayList_old) {

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
            System.out.println("inside camear" + e.toString());
        }
        return isGoto;
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


    public static void ConvertImage(Context context, Bitmap bitmap, String name) {
        try {
            File imageFile = new File(context.getApplicationInfo().dataDir, name);
            OutputStream os;
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
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

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
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
}
