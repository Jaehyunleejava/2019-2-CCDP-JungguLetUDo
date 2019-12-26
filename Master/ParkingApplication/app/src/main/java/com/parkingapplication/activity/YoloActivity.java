package com.parkingapplication.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;
import com.parkingapplication.R;
import com.parkingapplication.networks.dataModel.TestModel;
import com.parkingapplication.networks.listener.ActionResultListener;
import com.parkingapplication.utils.FileUpload;
import com.parkingapplication.utils.Logger;
import com.parkingapplication.utils.MoveActivityUtil;

import org.json.JSONObject;
import org.tensorflow.demo.Classifier;
import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.YoloListener;

import java.util.List;

import static com.parkingapplication.utils.FileUpload.gPrefKeyFileNum;

public class YoloActivity extends DetectorActivity implements YoloListener {

    private String mFilePath;
    private Activity mActivity;
    private String mCaptureFilePath;
    public static boolean isCaptured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setYoloListener(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_main, null);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addContentView(view, layoutParams);

        SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        FileUpload.gFileNum = sharedPreferences.getInt(gPrefKeyFileNum,400);
    }


    /**
     * 화면에 물체가 인식되면 콜백을 받는 곳.
     *
     * @param data
     */
    @Override
    public void onDetected(Classifier.Recognition data) {
        //  Logger.d("Data" + data.toString());
        if (isCaptured){  //boolean이 true면 아래 함수는 pass하도록
            return;
        }
        String id = data.getId(); //잡히는 갯수
        String title = data.getTitle(); //이름, ex) car
        float confidence = data.getConfidence(); //정확도 ex) 0.991
        Logger.d("id: "+id+" title: "+title+" confidence: "+confidence);

        switch(title){
            case "bus":
            case "car":
            case "truck":
                if( confidence > 0.5) {
                    isCaptured = true;//지정 경로는 CameraActivity mFilePath 참고, //return 값은 String
                    serverFileUpload(takePicture());
                }
                break;
        }

    }

    public void serverFileUpload(String mFilePath) {
        FileUpload fileUpload = new FileUpload();
        fileUpload.goSend(mFilePath,mActionResultListener);
    }

    /**
     * 파일 업로드 후 서버에 리턴 받는 데이터 콜백 받는 리스너.
     */
    private final ActionResultListener<JSONObject> mActionResultListener = new ActionResultListener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject data) {
            MoveActivityUtil.getInstance().moveCarResultActivity(mActivity,data.toString());
    }

        @Override
        public void onFail(String error) {
            Logger.d("onFail \t" + error);
            isCaptured = false;
        }
    };
}