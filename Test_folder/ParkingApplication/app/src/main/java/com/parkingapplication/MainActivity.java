package com.parkingapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
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
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.googlecode.tesseract.android.TessBaseAPI;
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
import org.tensorflow.demo.CameraActivity;
import org.tensorflow.demo.CameraConnectionFragment;
import org.tensorflow.demo.Classifier;
import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.YoloListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends DetectorActivity implements YoloListener {

    private Activity mActivity;
    private Context mContext;
    private CameraConnectionFragment CCF;

    //openCV:진영
    TessBaseAPI tessBaseAPI;
    Bitmap imgBase;
    Bitmap roi;
    private ImageView imageView;
    private ImageView imageResult;
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

    CameraActivity CameraActivity;
    private TextureView mTextureView;
    private Camera2 mCamera;



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
        if (recognition.getTitle().equals("car") && recognition.getConfidence() > 0.7) {
            Logger.d("!!!!!!!!!!!!!!!!!!!!!!1  " + recognition.getTitle() + recognition.getConfidence());
            takePicture();
        }

        Logger.d("Data" + recognition.toString());
    }


    protected void takePicture() {

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



                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageResult.setImageBitmap(roi);
                                    new AsyncTess().execute(roi);
                                    Toast.makeText(MainActivity.this, "이미지 촬영", Toast.LENGTH_LONG).show();
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
                    ////수정
                    CCF.createCameraPreviewSession();
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

    //createFiles, AsyncTess, checkLanguage, getrotatedbitmap
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

    private class AsyncTess extends AsyncTask<Bitmap, Integer, String> {
        @Override
        protected String doInBackground(Bitmap... mRelativeParams) {
            tessBaseAPI.setImage(mRelativeParams[0]);
            Log.e(TAG, "result tessBaseAPI" + tessBaseAPI.getUTF8Text());
            return tessBaseAPI.getUTF8Text();
        }


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

}
