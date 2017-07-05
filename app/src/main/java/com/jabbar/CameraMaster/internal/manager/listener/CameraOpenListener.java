package com.jabbar.CameraMaster.internal.manager.listener;


import com.jabbar.CameraMaster.internal.utils.Size;

public interface CameraOpenListener<CameraId, SurfaceListener> {
    void onCameraOpened(CameraId openedCameraId, Size previewSize, SurfaceListener surfaceListener);

    void onCameraOpenError();
}
