package com.parkingapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.parkingapplication.activity.BaseActivity;
import com.parkingapplication.utils.MoveActivityUtil;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MoveActivityUtil.getInstance().moveIntroActivity(mActivity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MoveActivityUtil.REQUEST_INTRO:
                MoveActivityUtil.getInstance().moveYoloAct(mActivity);
                break;
        }
    }
}
