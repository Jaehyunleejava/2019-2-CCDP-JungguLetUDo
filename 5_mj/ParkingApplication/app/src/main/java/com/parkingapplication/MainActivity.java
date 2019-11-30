package com.parkingapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.parkingapplication.utils.Logger;
import com.parkingapplication.utils.MoveActivityUtil;

import org.tensorflow.demo.Classifier;
import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.YoloListener;

import java.util.List;

public class MainActivity extends DetectorActivity implements YoloListener {

    private Activity mActivity;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
        setInit();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        MoveActivityUtil.getInstance().moveIntroActivity(mActivity);
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
//        setContentView(R.layout.activity_main);
        Logger.d("TEST:: 여기까지옴??");
        setYoloListener(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_main, null);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addContentView(view, layoutParams);
    }



    @Override
    public void onDetectedList(List<Classifier.Recognition> list) {

    }

    @Override
    public void onDetected(Classifier.Recognition recognition) {
        Logger.d("Data" + recognition.toString());
    }
}
