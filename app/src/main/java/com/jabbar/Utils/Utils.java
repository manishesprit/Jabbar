package com.jabbar.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.jabbar.Bean.ContactsBean;

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

//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(Config.TIMEOUT_CONNECTION, TimeUnit.MILLISECONDS);
//        httpClient.addInterceptor(logging);
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Config.HOST).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.client(httpClient.build()).build();

        return retrofit;
    }

    public static String getDeviceID(Context context) {
        String udid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Pref.setValue(context, Config.PREF_UDID, udid);
        return udid;
    }
}
