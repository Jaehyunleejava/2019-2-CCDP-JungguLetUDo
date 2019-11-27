package com.parkingapplication.activity;

import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.parkingapplication.R;
import com.parkingapplication.networks.controller.NetworkManager;
import com.parkingapplication.networks.dataModel.TestModel;
import com.parkingapplication.networks.listener.ActionResultListener;
import com.parkingapplication.networks.network.NetworkRequestTest;
import com.parkingapplication.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CarResultActivity extends BaseActivity {

    ImageView mImgAni;
    TextView mTxtComment;
    TextView mTxtCarNum;
    TextView mTxtDate;

    int mSoundId;
    int streamId;

    //date 출력 변수.
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String formatDate = sdfNow.format(date);

    //siren 출력.
    SoundPool sound = new SoundPool(1, AudioManager.STREAM_ALARM, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carresult);

        initView();
    }

    @Override
    public void finish() {
        super.finish();
        // 페이지가 넘어 갈때 소리 중지.
        if(sound != null){
            sound.stop(streamId);
        }
    }

    private void initView() {

        mImgAni = findViewById(R.id.img_ani);
        mTxtComment = findViewById(R.id.txt_comment);
        mTxtDate = findViewById(R.id.txt_date);
        mTxtCarNum = findViewById(R.id.txt_car_number);

        mSoundId = sound.load(this, R.raw.siren, 1);

        // 차량번호 조회 API Call.
        NetworkManager.getInstance().add(new NetworkRequestTest(mContext,mActionResultListener)).runNext();

    }

    private void bindView(TestModel data) {
        //0 = 비장애인, 1 = 장애인
        if (data.getResult().equals("1")) {
            // setAni..
            mImgAni.setImageResource(R.drawable.ani_check);
            AnimationDrawable ani = (AnimationDrawable) mImgAni.getDrawable();
            ani.start();
            //TODO OpenCv 차량번호 결과값
            mTxtCarNum.setText("1=장애인차량");
            mTxtDate.setText(formatDate);
            mTxtComment.setText("확인되었습니다.");
        } else {
            // setAni..
            mImgAni.setImageResource(R.drawable.ani_siren);
            AnimationDrawable ani = (AnimationDrawable) mImgAni.getDrawable();
            ani.start();

            streamId = sound.play(mSoundId, 1.0F, 1.0F, 1, -1, 1.0F);
            //TODO CarActivity 1분~30초 후 자동종료
            //종료 시 사운드 중지: sound.stop(streamId);
            mTxtCarNum.setText("0=비장애인차량");
            mTxtDate.setText(formatDate);
            mTxtComment.setText(" 주차 불가 차량입니다.\n3분 후 자동 신고됩니다.");
        }
    }

    /**
     * API Result CallBack Func.
     */
    private final ActionResultListener<TestModel> mActionResultListener = new ActionResultListener<TestModel>() {
        @Override
        public void onSuccess(TestModel data) {
            bindView(data);
        }

        @Override
        public void onFail(String error) {

        }
    };
}
