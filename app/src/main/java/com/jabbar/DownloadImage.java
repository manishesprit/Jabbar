package com.jabbar;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.jabbar.Utils.BasicImageDownloader;
import com.jabbar.Utils.Config;
import com.jabbar.Utils.Log;
import com.jabbar.Utils.Mydb;
import com.jabbar.Utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by hardikjani on 7/18/17.
 */

public class DownloadImage extends AsyncTask<String, String, String> {

    public static boolean isDownloading = false;
    Context context;
    File file;
    Mydb mydb;
    Cursor cursor = null;
    String filename = "";

    public DownloadImage(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isDownloading = true;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        isDownloading = false;
    }

    @Override
    protected String doInBackground(String... params) {

        mydb = new Mydb(context);
        cursor = mydb.query("select avatar from user_tb");

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            DownloadFile();
        }

        return null;
    }

    public void DownloadFile() {
        if (cursor != null && cursor.getCount() > 0 && !cursor.isAfterLast()) {
            filename = cursor.getString(0);
            file = new File(Utils.getAvatarDir(context) + "/" + filename);
            Log.print("===file====" + file.getAbsolutePath());
            if (!file.exists()) {
                Log.print("===file Download====" + filename);
                BasicImageDownloader basicImageDownloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
                    @Override
                    public void onError(BasicImageDownloader.ImageError error) {
                        cursor.moveToNext();
                        DownloadFile();
                    }

                    @Override
                    public void onProgressChange(int percent) {

                    }

                    @Override
                    public void onComplete(Bitmap result) {
                        Log.print("======ConvertImage===origWidth===" + result.getWidth() + "======origHeight======" + result.getHeight());
                        try {

                            OutputStream os;
                            os = new FileOutputStream(file);
                            result.compress(Bitmap.CompressFormat.JPEG, 60, os);
                            os.flush();
                            os.close();

                            cursor.moveToNext();
                            DownloadFile();

                        } catch (Exception e) {
                            Log.print("=====Exception====" + e.toString());

                            cursor.moveToNext();
                            DownloadFile();
                        }

                    }
                });
                basicImageDownloader.download(Config.AVATAR_HOST + cursor.getString(0), false);

            } else {
                cursor.moveToNext();
                DownloadFile();
            }
        } else {
            Log.print("====Cursor finish====");
        }
    }
}
