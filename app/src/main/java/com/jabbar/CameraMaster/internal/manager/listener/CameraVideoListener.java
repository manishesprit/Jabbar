package com.jabbar.CameraMaster.internal.manager.listener;


import com.jabbar.CameraMaster.internal.utils.Size;
import com.jabbar.CameraMaster.listeners.CameraFragmentResultListener;

import java.io.File;

public interface CameraVideoListener {
    void onVideoRecordStarted(Size videoSize);

    void onVideoRecordStopped(File videoFile, CameraFragmentResultListener callback);

    void onVideoRecordError();
}
