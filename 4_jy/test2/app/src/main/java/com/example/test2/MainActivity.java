package com.example.test2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.net.Uri;
import android.os.Environment;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    Button button;
    ConstraintLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.bt);
        container = findViewById(R.id.CL);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            // TODO Auto-generated method stub
            String folder = "Pictures"; // 폴더 이름
            try {
                // 현재 날짜로 파일을 저장하기
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                // 년월일시분초
                Date currentTime_1 = new Date();
                String dateString = formatter.format(currentTime_1);
                File sdCardPath = Environment.getExternalStorageDirectory();
                File dirs = new File(Environment.getExternalStorageDirectory(), folder);

                if (!dirs.exists()) { // 원하는 경로에 폴더가 있는지 확인
                    dirs.createNewFile(); // Test 폴더 생성
                    Log.d("CAMERA_TEST", "Directory Created");
                }
                container.buildDrawingCache();
                Bitmap captureView = container.getDrawingCache();

                FileOutputStream fos;
                String save;

                try {
                    save = sdCardPath.getPath() + "/" + folder + "/" + dateString + ".jpg";

                    // 저장 경로
                    fos = new FileOutputStream(save);
                    captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos); // 캡쳐

                    // 미디어 스캐너를 통해 모든 미디어 리스트를 갱신시킨다.
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                            Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), dateString + ".jpg 저장",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("Screen", "" + e.toString());
            }
            }
        });

    }

}


