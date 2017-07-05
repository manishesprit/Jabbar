package com.jabbar.CameraMaster.internal.manager.listener;


import com.jabbar.CameraMaster.listeners.CameraFragmentResultListener;

import java.io.File;

public interface CameraPhotoListener {
    void onPhotoTaken(byte[] bytes, File photoFile, CameraFragmentResultListener callback);

    void onPhotoTakeError();
}
