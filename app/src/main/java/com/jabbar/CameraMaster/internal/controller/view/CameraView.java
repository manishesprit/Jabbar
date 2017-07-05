package com.jabbar.CameraMaster.internal.controller.view;

import android.support.annotation.Nullable;
import android.view.View;

import com.jabbar.CameraMaster.configuration.Configuration;
import com.jabbar.CameraMaster.internal.utils.Size;
import com.jabbar.CameraMaster.listeners.CameraFragmentResultListener;


public interface CameraView {

    void updateCameraPreview(Size size, View cameraPreview);

    void updateUiForMediaAction(@Configuration.MediaAction int mediaAction);

    void updateCameraSwitcher(int numberOfCameras);

    void onPhotoTaken(byte[] bytes, @Nullable CameraFragmentResultListener callback);

    void onVideoRecordStart(int width, int height);

    void onVideoRecordStop(@Nullable CameraFragmentResultListener callback);

    void releaseCameraPreview();

}
