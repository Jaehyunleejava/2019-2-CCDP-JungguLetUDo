package com.parkingapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;

<<<<<<< HEAD
import com.googlecode.tesseract.android.TessBaseAPI;
import com.parkingapplication.networks.controller.NetworkManager;
import com.parkingapplication.networks.network.NetworkRequestTest;
import com.parkingapplication.utils.Logger;
import com.parkingapplication.utils.MoveActivityUtil;

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
import java.util.List;

public class MainActivity extends DetectorActivity implements YoloListener {

    private Activity mActivity;
    private Context mContext;

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


=======
import com.parkingapplication.activity.BaseActivity;
import com.parkingapplication.utils.MoveActivityUtil;

import static com.parkingapplication.utils.FileUpload.gFileNum;
import static com.parkingapplication.utils.FileUpload.gPrefKeyFileNum;
>>>>>>> 51e76156bb753c23950030e8910d83956ffdf2e2

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
<<<<<<< HEAD

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
        }

        Logger.d("Data" + recognition.toString());
    }

    //createFiles, AsyncTess, checkLanguage
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





=======
>>>>>>> 51e76156bb753c23950030e8910d83956ffdf2e2
}
