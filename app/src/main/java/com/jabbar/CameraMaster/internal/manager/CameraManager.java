package com.jabbar.CameraMaster.internal.manager;

import android.content.Context;

import com.jabbar.CameraMaster.configuration.Configuration;
import com.jabbar.CameraMaster.configuration.ConfigurationProvider;
import com.jabbar.CameraMaster.internal.manager.listener.CameraCloseListener;
import com.jabbar.CameraMaster.internal.manager.listener.CameraOpenListener;
import com.jabbar.CameraMaster.internal.manager.listener.CameraPhotoListener;
import com.jabbar.CameraMaster.internal.manager.listener.CameraVideoListener;
import com.jabbar.CameraMaster.internal.utils.Size;
import com.jabbar.CameraMaster.listeners.CameraFragmentResultListener;

import java.io.File;

public interface CameraManager<CameraId, SurfaceListener> {

    void initializeCameraManager(ConfigurationProvider configurationProvider, Context context);

    void openCamera(CameraId cameraId, CameraOpenListener<CameraId, SurfaceListener> cameraOpenListener);

    void closeCamera(CameraCloseListener<CameraId> cameraCloseListener);

    void setFlashMode(@Configuration.FlashMode int flashMode);

    void takePhoto(File photoFile, CameraPhotoListener cameraPhotoListener, CameraFragmentResultListener callback);

    void startVideoRecord(File videoFile, CameraVideoListener cameraVideoListener);

    Size getPhotoSizeForQuality(@Configuration.MediaQuality int mediaQuality);

    void stopVideoRecord(CameraFragmentResultListener callback);

    void releaseCameraManager();

    CameraId getCurrentCameraId();

    CameraId getFaceFrontCameraId();

    CameraId getFaceBackCameraId();

    int getNumberOfCameras();

    int getFaceFrontCameraOrientation();

    int getFaceBackCameraOrientation();

    boolean isVideoRecording();

    CharSequence[] getVideoQualityOptions();

    CharSequence[] getPhotoQualityOptions();

    void setCameraId(CameraId currentCameraId);
}
