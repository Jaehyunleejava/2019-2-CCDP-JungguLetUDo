package com.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CaptureExActivity extends Activity {
	private static final String TAG = "Touch";

	ImageView view0 = null;

	Bitmap bitmapLogo = null;
	BitmapDrawable bitmapDrawableLogo = null;
	RelativeLayout fullScreenView = null;
	RelativeLayout screenView = null;
	Bitmap bm = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// fullScreen
		fullScreenView = (RelativeLayout) findViewById(R.id.full_screen);

		// screen
		screenView = (RelativeLayout) findViewById(R.id.screen);

		// 기본 배경 이미지
		view0 = (ImageView) findViewById(R.id.imageView0);
		bitmapDrawableLogo = (BitmapDrawable) view0.getResources().getDrawable(
				R.drawable.yong320);
		bitmapLogo = bitmapDrawableLogo.getBitmap();
		view0.setImageDrawable(new BitmapDrawable(bitmapLogo));

		// 전체 스크린샷
		Button btn2 = (Button) findViewById(R.id.btn2);
		btn2.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				fullScreenView.buildDrawingCache();
				fullScreenView.setDrawingCacheEnabled(true);
				bm = fullScreenView.getDrawingCache();
				fullScreenshot(bm);
			}
		});

		// 일부 영역 스크린샷
		Button btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				screenView.buildDrawingCache();
				screenView.setDrawingCacheEnabled(true);
				bm = screenView.getDrawingCache();
				screenshot(bm);
			}
		});

	}

	/**
	 * 전체 영역 스크린샷
	 * 
	 * @param bm
	 */
	private void fullScreenshot(Bitmap bm) {
		try {
			File path = new File("/sdcard/CaptureTest");

			if (!path.isDirectory()) {
				path.mkdirs();
			}

			FileOutputStream out = new FileOutputStream(
					"/sdcard/CaptureTest/capture2Full.jpg");
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);

			Toast toast = Toast.makeText(CaptureExActivity.this,
					"전체영역 스크린샷이 완료되었습니다", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 150);
			toast.show();

		} catch (FileNotFoundException e) {
			Log.d("FileNotFoundException:", e.getMessage());
		}
	}

	/**
	 * 
	 * 일부 영역 스크린샷
	 * 
	 * @param bm
	 */
	private void screenshot(Bitmap bm) {
		try {
			File path = new File("/sdcard/CaptureTest");

			if (!path.isDirectory()) {
				path.mkdirs();
			}

			FileOutputStream out = new FileOutputStream(
					"/sdcard/CaptureTest/capture2Part.jpg");
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);

			Toast toast = Toast.makeText(CaptureExActivity.this,
					"일부영역 스크린샷이 완료되었습니다.", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 150);
			toast.show();

		} catch (FileNotFoundException e) {
			Log.d("FileNotFoundException:", e.getMessage());
		}
	}

}
