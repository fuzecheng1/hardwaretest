package com.hollyland.hardwaretest.ui.preview;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.hollyland.hardwaretest.R;
import com.hollyland.hardwaretest.ui.BaseActivity;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;

public class PreviewActivity extends BaseActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.view_finder)
    PreviewView previewView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderListenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private void bindPreview(ProcessCameraProvider processCameraProvider){
        //创建 Preview。
        Preview preview = new Preview.Builder().build();
        //**前置摄像头
         CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        //将 Preview 连接到 PreviewView。
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        //将所选相机和任意用例绑定到生命周期
        Camera camera = processCameraProvider.bindToLifecycle((LifecycleOwner) this,cameraSelector ,preview);
    }

    @Override
    protected int setContentView() {
        return R.layout.prewview_activity;
    }
}
