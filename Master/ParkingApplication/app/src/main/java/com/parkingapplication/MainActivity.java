package com.parkingapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.parkingapplication.activity.BaseActivity;
import com.parkingapplication.utils.MoveActivityUtil;

import static com.parkingapplication.utils.FileUpload.gFileNum;
import static com.parkingapplication.utils.FileUpload.gPrefKeyFileNum;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MoveActivityUtil.getInstance().moveIntroActivity(mActivity);
    }

    @Override
    public void finish() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(gPrefKeyFileNum, gFileNum);
        editor.apply();
        super.finish();
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
