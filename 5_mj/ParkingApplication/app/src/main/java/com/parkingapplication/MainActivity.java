package com.parkingapplication;


import android.app.ActionBar;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceHolder;
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
import com.parkingapplication.view.CameraPreview;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static CameraPreview surfaceView;
    private static Camera mCamera;
    public static MainActivity getInstance;

    private SurfaceHolder holder;

    // [s] 시크릿 페이지 관련 변수
    private int mSecretClickCnt = 0;
    private long mSecretTime = -1;
    // [e] 시크릿 페이지 관련 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MoveActivityUtil.getInstance().moveIntroActivity(mActivity);
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

    public static Camera getCamera() {
        return mCamera;
    }

    private void setInit() {
        getInstance = this;

        // 카메라 객체를 R.layout.activity_main의 레이아웃에 선언한 SurfaceView에서 먼저 정의해야 함으로 setContentView 보다 먼저 정의한다.
        mCamera = Camera.open();

        setContentView(R.layout.activity_main);

        // 함수 내에서 초기화 할수 있는 영역
        findViewById(R.id.img_logo).setOnClickListener(this);


        // 클래스 내에서 초기화 할수 있는 영역
        // SurfaceView를 상속받은 레이아웃을 정의한다.
        surfaceView = (CameraPreview) findViewById(R.id.camera);


        // SurfaceView 정의 - holder와 Callback을 정의한다.
        holder = surfaceView.getHolder();
        holder.addCallback(surfaceView);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Test API Call.
        NetworkManager.getInstance().add(new NetworkRequestTest(mContext, new ActionResultListener<TestModel>() {
            @Override
            public void onSuccess(TestModel data) {
                Logger.d("onSuccess\t" + data.toString());
            }

            @Override
            public void onFail(String error) {
                Logger.d("onFail\t" + error);
            }
        })).runNext();

    }
}
