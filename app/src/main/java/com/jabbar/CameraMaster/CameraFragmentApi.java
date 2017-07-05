package com.jabbar.CameraMaster;

import android.support.annotation.Nullable;

import com.jabbar.CameraMaster.listeners.CameraFragmentControlsListener;
import com.jabbar.CameraMaster.listeners.CameraFragmentResultListener;
import com.jabbar.CameraMaster.listeners.CameraFragmentStateListener;
import com.jabbar.CameraMaster.listeners.CameraFragmentVideoRecordTextListener;


public interface CameraFragmentApi {

    void takePhotoOrCaptureVideo(CameraFragmentResultListener resultListener, @Nullable String directoryPath, @Nullable String fileName);

    void openSettingDialog();

    void switchCameraTypeFrontBack();

    void switchActionPhotoVideo();

    void toggleFlashMode();

    void setStateListener(CameraFragmentStateListener cameraFragmentStateListener);

    void setTextListener(CameraFragmentVideoRecordTextListener cameraFragmentVideoRecordTextListener);

    void setControlsListener(CameraFragmentControlsListener cameraFragmentControlsListener);

    void setResultListener(CameraFragmentResultListener cameraFragmentResultListener);

}
