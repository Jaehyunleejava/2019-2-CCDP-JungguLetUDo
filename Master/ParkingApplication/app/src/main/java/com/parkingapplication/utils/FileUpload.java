package com.parkingapplication.utils;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.parkingapplication.networks.listener.ActionResultListener;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUpload {
    public static int gFileNum;
    public static final String gPrefKeyFileNum = "pref_file_num";
    //path만 입력하면 자동으로 업로드 될 수 있게끔 만든 util.
    public void goSend(String mFilePath, final ActionResultListener<JSONObject> listener) {
        @SuppressLint("DefaultLocale")
        String fileName = String.format("yolo_result%d.jpg",++gFileNum);
        
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fileToUpload", fileName, RequestBody.create(MultipartBody.FORM, new File(mFilePath)))
                .build();

        Request request = new Request.Builder()
                .url("http://ec2-15-164-211-230.ap-northeast-2.compute.amazonaws.com/upload.php")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.onFail(e.getMessage());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                Logger.d("onResponse: " + body);
                try {
                    final JSONObject jsonObject = new JSONObject(body.substring(body.indexOf("{"), body.lastIndexOf("}") + 1));
                    String result = jsonObject.getString("result");
                    Logger.d("Json\t" + result);
                    if (listener != null) {
                        listener.onSuccess(jsonObject);
                    }
                } catch (Exception ex) {
                    if (listener != null) {
                        listener.onFail(ex.getMessage());
                    }
                }

            }
        });
    }

    private String filePath(int i) {
        i++;
        return "yolo_result" + Integer.toString(i) + ".jpg";
    }
}
//final int i = 3;
//        String filename="yolo_result"+Integer.toString(i)+".jpg";