package com.parkingapplication.activity;

import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.parkingapplication.R;

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
    SoundPool sound = new SoundPool(1, AudioManager.STREAM_ALARM,0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carresult);

        initView();
        //TODO 서버에서 0 or 1 값 받아야 함
        bindView(new Random().nextInt(2));

    }

    private void initView() {

        mImgAni = findViewById(R.id.img_ani);
        mTxtComment = findViewById(R.id.txt_comment);
        mTxtDate = findViewById(R.id.txt_date);
        mTxtCarNum = findViewById(R.id.txt_car_number);

        mSoundId = sound.load(this,R.raw.siren,1);
    }

    private void bindView(int n) {
        //0 = 비장애인, 1 = 장애인
        if (n == 1) {
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
}
