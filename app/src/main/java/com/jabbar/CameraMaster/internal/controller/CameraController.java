package com.jabbar.CameraMaster.internal.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jabbar.CameraMaster.configuration.Configuration;
import com.jabbar.CameraMaster.internal.manager.CameraManager;
import com.jabbar.CameraMaster.listeners.CameraFragmentResultListener;

import java.io.File;


public interface CameraController<CameraId> {

    void onCreate(Bundle savedInstanceState);

    void onResume();

    void onPause();

    void onDestroy();

    void takePhoto(CameraFragmentResultListener resultListener);

    void takePhoto(CameraFragmentResultListener callback, @Nullable String direcoryPath, @Nullable String fileName);

    void startVideoRecord();

    void startVideoRecord(@Nullable String direcoryPath, @Nullable String fileName);

    void stopVideoRecord(CameraFragmentResultListener callback);

    boolean isVideoRecording();

    void switchCamera(@Configuration.CameraFace int cameraFace);

    void switchQuality();

    void setFlashMode(@Configuration.FlashMode int flashMode);

    int getNumberOfCameras();

    @Configuration.MediaAction
    int getMediaAction();

    CameraId getCurrentCameraId();

    File getOutputFile();

    CameraManager getCameraManager();

    CharSequence[] getVideoQualityOptions();

    CharSequence[] getPhotoQualityOptions();
}
