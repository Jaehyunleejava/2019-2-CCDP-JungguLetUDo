package com.parkingapplication;


import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.parkingapplication.activity.BaseActivity;
import com.parkingapplication.networks.controller.NetworkManager;
import com.parkingapplication.networks.dataModel.TestModel;
import com.parkingapplication.networks.listener.ActionResultListener;
import com.parkingapplication.networks.network.NetworkRequestTest;
import com.parkingapplication.utils.Logger;
import com.parkingapplication.utils.MoveActivityUtil;
import com.parkingapplication.view.Camera2;

public class MainActivity extends BaseActivity implements View.OnClickListener, Camera2.Camera2Interface, TextureView.SurfaceTextureListener {
    //
    //    // [s] 시크릿 페이지 관련 변수
    private int mSecretClickCnt = 0;
    private long mSecretTime = -1;
    // [e] 시크릿 페이지 관련 변수

    private TextureView mTextureView;
    private Camera2 mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        MoveActivityUtil.getInstance().moveIntroActivity(mActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTextureView != null) {
            if (mTextureView.isAvailable()) {
                openCamera();
            } else {
                mTextureView.setSurfaceTextureListener(this);
            }
        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        super.onPause();
    }

    //ctrl+i
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_logo:
                // 시간 5초 카운팅 유효성 체크.
                long diffTime = Math.abs(mSecretTime - System.currentTimeMillis());
                // 5초 이상인 경우 초기화 X
                if (diffTime > 3000) {
                    mSecretTime = -1;
                    mSecretClickCnt = 0;
                }

                // 타임 현재 시간으로 초기화
                if (mSecretTime == -1) {
                    mSecretTime = System.currentTimeMillis();
                    mSecretClickCnt = 0;
                } else {
                    mSecretClickCnt++;
                }

                if (mSecretClickCnt >= 6) {
                    MoveActivityUtil.getInstance().movePasswordActivity(mActivity);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MoveActivityUtil.REQUEST_INTRO:
                setInit();
                break;
        }
    }

    private void setInit() {
        setContentView(R.layout.activity_main);

        // 함수 내에서 초기화 할수 있는 영역
        findViewById(R.id.img_logo).setOnClickListener(this);

        // 클래스 내에서 초기화 해야 하는 영역
        mTextureView = findViewById(R.id.v_texture);
        mTextureView.setSurfaceTextureListener(this);

        mCamera = new Camera2(mContext, this);

    }

    private void openCamera() {
        CameraManager cameraManager = mCamera.CameraManager_1(mActivity);
        String cameraId = mCamera.CameraCharacteristics_2(cameraManager);
        mCamera.CameraDevice_3(cameraManager, cameraId);
    }

    private void closeCamera() {
        if (mCamera != null) {
            mCamera.closeCamera();
        }
    }

    // [s] TextureView.SurfaceTextureListener
    //이부분 절대 절대 절대 절대 건드리지말기!!
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    // [e] TextureView.SurfaceTextureListener

    /**
     * Camera2 Interface
     *
     * @param cameraDevice CameraDevice.
     * @param cameraSize   CameraSize
     */
    @Override
    public void onCameraDeviceOpened(CameraDevice cameraDevice, Size cameraSize) {
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        texture.setDefaultBufferSize(cameraSize.getWidth(), cameraSize.getHeight());
        Surface surface = new Surface(texture);
        mCamera.CaptureSession_4(cameraDevice, surface);
        mCamera.CaptureRequest_5(cameraDevice, surface);
    }
}
