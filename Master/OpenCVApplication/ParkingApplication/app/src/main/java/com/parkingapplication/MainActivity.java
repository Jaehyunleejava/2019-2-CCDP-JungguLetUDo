package com.parkingapplication;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.parkingapplication.activity.BaseActivity;
import com.parkingapplication.networks.controller.NetworkManager;
import com.parkingapplication.networks.dataModel.TestModel;
import com.parkingapplication.networks.listener.ActionResultListener;
import com.parkingapplication.networks.network.NetworkRequestTest;
import com.parkingapplication.utils.Logger;
import com.parkingapplication.utils.MoveActivityUtil;
import com.parkingapplication.view.Camera2;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, Camera2.Camera2Interface, TextureView.SurfaceTextureListener {

    TessBaseAPI tessBaseAPI;
    private ImageView imageView;
    private ImageView imageResult;
    private Button btnTakePicture;
    private TextView txt_comment;
    private static final String TAG = "MAINACTIVITY";
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    Bitmap imgBase;
    Bitmap roi;


    // [s] 시크릿 페이지 관련 변수
    private int mSecretClickCnt = 0;
    private long mSecretTime = -1;
    // [e] 시크릿 페이지 관련 변수

    private TextureView mTextureView;
    private Camera2 mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        MoveActivityUtil.getInstance().moveIntroActivity(mActivity);

        assert mTextureView != null;
        assert btnTakePicture != null;


    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if (mTextureView != null) {
            if (mTextureView.isAvailable()) {
                openCamera();
            } else {
                mTextureView.setSurfaceTextureListener(this);
            }
        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    //ctrl+i
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_logo:
                // 시간 5초 카운팅 유효성 체크.
                long diffTime = Math.abs(mSecretTime - System.currentTimeMillis());
                // 5초 이상인 경우 초기화 X
                if (diffTime > 3000) {
                    mSecretTime = -1;
                    mSecretClickCnt = 0;
                }

                // 타임 현재 시간으로 초기화
                if (mSecretTime == -1) {
                    mSecretTime = System.currentTimeMillis();
                    mSecretClickCnt = 0;
                } else {
                    mSecretClickCnt++;
                }

                if (mSecretClickCnt >= 6) {
                    MoveActivityUtil.getInstance().movePasswordActivity(mActivity);
                }
                break;

            //양진영 opencv
            case R.id.btnTakePicture:
                takePicture();
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

    private void setInit() {
        setContentView(R.layout.activity_main);

        // 함수 내에서 초기화 할수 있는 영역
        findViewById(R.id.img_logo).setOnClickListener(this);

        // 클래스 내에서 초기화 해야 하는 영
        mTextureView = findViewById(R.id.v_texture);
        mTextureView.setSurfaceTextureListener(this);
        txt_comment = findViewById(R.id.txt_comment);
        imageView = findViewById(R.id.imageView);
        imageResult = findViewById(R.id.imageResult);
        btnTakePicture = findViewById(R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(this);


        mCamera = new Camera2(mContext, this);

        // Test API Call.
        NetworkManager.getInstance().add(new NetworkRequestTest(mContext, new ActionResultListener<TestModel>() {
            @Override
            public void onSuccess(TestModel data) {
                Logger.d("onSuccess\t" + data.toString());
            }

            @Override
            public void onFail(String error) {
                Logger.d("onFail\t" + error);
            }
        })).runNext();

        tessBaseAPI = new TessBaseAPI();
        String dir = getFilesDir() + "/tesseract";
        if (checkLanguageFile(dir + "/tessdata"))
            tessBaseAPI.init(dir, "kor");


    }

    private void openCamera() {
        CameraManager cameraManager = mCamera.CameraManager_1(mActivity);
        String cameraId = mCamera.CameraCharacteristics_2(cameraManager);
        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
        } catch (CameraAccessException ex) {
            Logger.d("Error\t" + ex.getMessage());
        }

        mCamera.CameraDevice_3(cameraManager, cameraId);
    }

    private void closeCamera() {
        if (mCamera != null) {
            mCamera.closeCamera();
        }
    }


    // [s] TextureView.SurfaceTextureListener
    //이부분 절대 절대 절대 절대 건드리지말기!!
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    // [e] TextureView.SurfaceTextureListener

    /**
     * Camera2 Interface
     *
     * @param cameraDevice CameraDevice.
     * @param cameraSize   CameraSize
     */
    @Override
    public void onCameraDeviceOpened(CameraDevice cameraDevice, Size cameraSize) {
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        texture.setDefaultBufferSize(cameraSize.getWidth(), cameraSize.getHeight());
        Surface surface = new Surface(texture);
        mCamera.CaptureSession_4(cameraDevice, surface);
        mCamera.CaptureRequest_5(cameraDevice, surface);
    }


    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2) {
                    bitmap = b2;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    protected void takePicture() {

        if (null == mCamera.mCameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }

        try {
            CameraCharacteristics characteristics = mCamera.CameraManager_1(this).getCameraCharacteristics(mCamera.mCameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                jpegSizes = map.getOutputSizes(ImageFormat.JPEG);
            }

            int width = 640;
            int height = 480;

            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                Log.e(TAG, width + "....");
                height = jpegSizes[0].getHeight();
                Log.e(TAG, height + "....");
            }
            ImageReader imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);

            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(imageReader.getSurface());
            outputSurfaces.add(new Surface(mTextureView.getSurfaceTexture()));

            final CaptureRequest.Builder captureBuilder = mCamera.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(imageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            //final File file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        Log.d(TAG, "takePicture");

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bitmap = GetRotatedBitmap(bitmap, 90);

                        Bitmap imgRoi;
                        OpenCVLoader.initDebug(); // 초기화
                        Mat matBase = new Mat();

                        Utils.bitmapToMat(bitmap, matBase);
                        Mat matGray = new Mat();
                        Mat matCny = new Mat();

                        Imgproc.cvtColor(matBase, matGray, Imgproc.COLOR_BGR2GRAY, 0); // GrayScale  //양진영 : 맨 뒤에 ,1 붙여봄
                        Log.e(TAG,"gray1");

                        Imgproc.GaussianBlur(matGray, matGray, new org.opencv.core.Size(5, 5), 0); //양진영: 가우시안 블러
                        //노이즈제거
                        Imgproc.erode(matGray, matGray, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new org.opencv.core.Size(6, 6)));
                        Imgproc.dilate(matGray, matGray, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new org.opencv.core.Size(12, 12)));

                        Imgproc.Canny(matGray, matCny, 10, 100, 3, true); // Canny Edge 검출 //진영: Gray
                        Imgproc.threshold(matGray, matCny, 150, 255, Imgproc.THRESH_BINARY); //Binary 검은색, 흰색으로 나눔, canny보다 먼저 수행되면 안됨, 경계선이 안생김


                        List<MatOfPoint> contours = new ArrayList<>(); ///컨투어 리스트 선언
                        Mat hierarchy = new Mat();
                        //관심영역 추출
                        Imgproc.findContours(matCny, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);//RETR_EXTERNAL //RETR_TREE //컨투어에 넣음, 윤곽선을 찾아준다.
                        //양진영 : Gray, external, chain - simple

                        Imgproc.drawContours(matBase, contours, -1, new Scalar(255, 0, 0), 5); ///양진영: 값 조절해봄 , 윤곽선을 그려준다.

                        imgBase = Bitmap.createBitmap(matBase.cols(), matBase.rows(), Bitmap.Config.ARGB_8888); // 비트맵 생성 양진영: 원래 matBase

                        Utils.matToBitmap(matBase, imgBase); // Mat을 비트맵으로 변환, 원래 matBase


                        //이미지 보낼 땐 runOnUiThread, 나머지는 AsyncTask
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(imgBase);
                            }
                        });

                        imgRoi = Bitmap.createBitmap(matGray.cols(), matGray.rows(), Bitmap.Config.ARGB_8888); // 비트맵 생성
                        Utils.matToBitmap(matGray, imgRoi);

                        // 컨투어에 넣음
                        for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
                            MatOfPoint matOfPoint = contours.get(idx);
                            Rect rect = Imgproc.boundingRect(matOfPoint);
                            Log.d(TAG, "D");
                            ///양진영: 값 조절해봄

                            Log.d(TAG, "size " + "x : " + rect.x + ", y : " + rect.y + ", w :" + rect.width + ", h : " + rect.height);

                            if (700 >= rect.width || rect.width >= 1600 || 150 >= rect.height || rect.height >= 450 || rect.width <= rect.height || rect.width <= rect.height * 3.5 || rect.width >= rect.height * 5.5)
                                continue; // 사각형 크기에 따라 출력 여부 결정


                            Log.d(TAG, "Go");
                            roi = Bitmap.createBitmap(imgRoi, (int) rect.tl().x, (int) rect.tl().y, rect.width, rect.height);
                            Log.d(TAG, "size " + "x : " + rect.x + ", y : " + rect.y + ", w :" + rect.width + ", h : " + rect.height);

                            ///양진영 : for문 추가해봄, 이진화된 이미지의 픽셀값을 모두 반전시킨다고 함
//                            for (int x = 0; x < roi.getWidth(); x++) {
//                                for (int y = 0; y < roi.getHeight(); y++) {
//                                    if (roi.getPixel(x, y) == -1) {
//                                        roi.setPixel(x, y, 0);
//                                    } else {
//                                        roi.setPixel(x, y, -1);
//                                    }
//                                }
//                            }
                            if (roi == null) {
                                Log.d(TAG, "roi Error");
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageResult.setImageBitmap(roi);
                                    new AsyncTess().execute(roi);
                                    btnTakePicture.setEnabled(false);
                                    Toast.makeText(MainActivity.this, "이미지 촬영", Toast.LENGTH_LONG).show();
                                    btnTakePicture.setText("텍스트 인식중...");
                                }
                            });


                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "ECXEPTION");
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }

            };
            imageReader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    createCameraPreview();
                }
            };

            mCamera.mCameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Size imageDimension;

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            mCamera.mPreviewRequestBuilder = mCamera.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCamera.mPreviewRequestBuilder.addTarget(surface);
            mCamera.mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == mCamera.mCameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    mCamera.mCaptureSession = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void updatePreview() {
        if (null == mCamera.mCameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        mCamera.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            mCamera.mCaptureSession.setRepeatingRequest(mCamera.mPreviewRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    boolean checkLanguageFile(String dir) {
        File file = new File(dir);
        if (!file.exists() && file.mkdirs())
            createFiles(dir);
        else if (file.exists()) {
            String filePath = dir + "/kor.traineddata";
            File langDataFile = new File(filePath);
            if (!langDataFile.exists())
                createFiles(dir);
        }
        return true;
    }

    private void createFiles(String dir) {
        AssetManager assetMgr = this.getAssets();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = assetMgr.open("kor.traineddata");

            String destFile = dir + "/kor.traineddata";

            outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        protected void startBackgroundThread() {
            mBackgroundThread = new HandlerThread("Camera Background");
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }

        protected void stopBackgroundThread() {
            mBackgroundThread.quitSafely();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    private class AsyncTess extends AsyncTask<Bitmap, Integer, String> {
        @Override
        protected String doInBackground(Bitmap... mRelativeParams) {
            tessBaseAPI.setImage(mRelativeParams[0]);
            Log.e(TAG, "result tessBaseAPI" + tessBaseAPI.getUTF8Text());
            return tessBaseAPI.getUTF8Text();
        }

        protected void onPostExecute(String result) {
            //특수문자 제거
            String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
            result = result.replaceAll(match, " ");
            result = result.replaceAll(" ", "");
            if (result.length() >= 7 && result.length() <= 8) {
                txt_comment.setText(result);
                Toast.makeText(MainActivity.this, "" + result, Toast.LENGTH_SHORT).show();
            } else {
                txt_comment.setText("");
                Toast.makeText(MainActivity.this, "번호판 문자인식에 실패했습니다", Toast.LENGTH_LONG).show();
            }

            btnTakePicture.setEnabled(true);
            btnTakePicture.setText("텍스트 인식");
        }
    }

}
