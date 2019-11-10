package com.parkingapplication;


import android.content.ContentValues;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.parkingapplication.activity.BaseActivity;
import com.parkingapplication.connection.RequestHttpConnection;
import com.parkingapplication.utils.Logger;
import com.parkingapplication.utils.MoveActivityUtil;
import com.parkingapplication.view.CameraPreview;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static CameraPreview surfaceView;
    private static Camera mCamera;
    public static MainActivity getInstance;
    private TextView mTxtOutput;

    private SurfaceHolder holder;
    String mUrl = "http://ec2-15-164-211-230.ap-northeast-2.compute.amazonaws.com/index.php";

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
                if(diffTime > 3000){
                    mSecretTime = -1;
                    mSecretClickCnt = 0;
                }

                // 타임 현재 시간으로 초기화
                if(mSecretTime == -1){
                    mSecretTime = System.currentTimeMillis();
                    mSecretClickCnt = 0;
                } else {
                    mSecretClickCnt++;
                }

                if(mSecretClickCnt >= 6){
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

    public static Camera getCamera(){
        return mCamera;
    }

    private void setInit(){
        getInstance = this;

        // 카메라 객체를 R.layout.activity_main의 레이아웃에 선언한 SurfaceView에서 먼저 정의해야 함으로 setContentView 보다 먼저 정의한다.
        mCamera = Camera.open();

        setContentView(R.layout.activity_main);

        // 함수 내에서 초기화 할수 있는 영역
        findViewById(R.id.img_logo).setOnClickListener(this);


        // 클래스 내에서 초기화 할수 있는 영역
        // SurfaceView를 상속받은 레이아웃을 정의한다.
        mTxtOutput = (TextView) findViewById(R.id.txt_outPut);
        surfaceView = (CameraPreview) findViewById(R.id.camera);


        // SurfaceView 정의 - holder와 Callback을 정의한다.
        holder = surfaceView.getHolder();
        holder.addCallback(surfaceView);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // AsyncTask를 통해 HttpURLConnection 수행.
        new NetworkTask(mUrl,null).execute();
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("msg",""+s);
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            mTxtOutput.setText(s);
        }
    }
}
