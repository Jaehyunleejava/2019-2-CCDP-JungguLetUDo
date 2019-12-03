package com.parkingapplication.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.parkingapplication.R;
import com.parkingapplication.utils.Logger;

import org.tensorflow.demo.Classifier;
import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.YoloListener;

import java.util.List;

public class YoloActivity extends DetectorActivity implements YoloListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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
        // 여기는 콜백을 받지 않습니다.요
    }

    /**
     * 화면에 물체가 인식되면 콜백을 받는 곳.
     * @param data
     */
    @Override
    public void onDetected(Classifier.Recognition data) {
        Logger.d("Data" + data.toString());
        String id = data.getId();
        String title = data.getTitle();
        float cinfidence = data.getConfidence();
    }
}
