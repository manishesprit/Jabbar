package com.jabbar.CameraMaster;

import android.Manifest;
import android.support.annotation.RequiresPermission;

import com.jabbar.CameraMaster.configuration.Configuration;
import com.jabbar.CameraMaster.internal.ui.BaseAnncaFragment;


public class CameraFragment extends BaseAnncaFragment {

    @RequiresPermission(Manifest.permission.CAMERA)
    public static CameraFragment newInstance(Configuration configuration) {
        return (CameraFragment) BaseAnncaFragment.newInstance(new CameraFragment(), configuration);
    }
}
