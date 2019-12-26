package com.parkingapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * mj_application_v1
 * Class: BaseActivity
 * Created by jsieu on 2019-10-05.
 * <p>
 * Description:
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Context mContext;
    protected Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
    }

}
