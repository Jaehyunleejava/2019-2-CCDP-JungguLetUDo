package com.parkingapplication.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.parkingapplication.R;
import com.parkingapplication.utils.Logger;

import java.util.ArrayList;

public class IntroActivity extends BaseActivity {

    final int REQUEST_PERMISSION = 1000;
    private View mVPermission;
    private ImageView mImgLogo;
    private TextView mTvAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mVPermission = findViewById(R.id.v_permission);
        mImgLogo = findViewById(R.id.img_logo);
        mTvAppName = findViewById(R.id.txt_app_name);

        checkPermission();
    }

    private void checkPermission() {
        ArrayList<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionList.size() > 0) {
            ActivityCompat.requestPermissions(mActivity, permissionList.toArray(new String[0]), REQUEST_PERMISSION);
        } else {
            startIntro();
        }
    }

    private void startIntro(){
        mVPermission.setVisibility(View.GONE);
        mImgLogo.setVisibility(View.VISIBLE);
        mTvAppName.setVisibility(View.VISIBLE);
        // 권한 동의 완료
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.setResult(RESULT_OK);
                finish();
            }
        },2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                Logger.d("TEST\t권한 동의 개수 " + grantResults.length);
                if(ContextCompat.checkSelfPermission(mContext,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    Logger.d("TEST\t권한 둘다 동의");
                    startIntro();
                } else {
                    mActivity.finishAffinity();
                }
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_fade_in,R.anim.anim_fade_out);
    }

}
