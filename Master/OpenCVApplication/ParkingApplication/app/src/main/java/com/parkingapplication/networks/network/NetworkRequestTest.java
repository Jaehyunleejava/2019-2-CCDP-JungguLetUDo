package com.parkingapplication.networks.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.parkingapplication.networks.controller.ClientUtil;
import com.parkingapplication.networks.dataModel.TestModel;
import com.parkingapplication.networks.listener.APIService;
import com.parkingapplication.networks.listener.ActionResultListener;
import com.parkingapplication.utils.Logger;

import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Response;

/**
 * mj_parking_app
 * Class: NetworkRequestTest
 * Created by MinjinPark on 2019-11-12.
 * <p>
 * Description:
 */
public class NetworkRequestTest extends BaseNetwork<TestModel> {

    public NetworkRequestTest(Context ctx, ActionResultListener<TestModel> listener) {
        mContext = ctx;
        mActionListener = listener;
    }

    @Override
    void actionDone(@NonNull Type type, @Nullable String msg) {
        switch (type) {
            case ERROR:
                Logger.d("API Error\t" + msg);
                break;
            case NOT_RESPONSE:
                Logger.d("API Not Response\t" + msg);
                break;
        }
    }

    /**
     * API CallBack Func.
     */
    private BaseCallBack mCallBack = new BaseCallBack() {
        @Override
        public void onResponse(Call<TestModel> call, Response<TestModel> response) {
            super.onResponse(call, response);
            // 데이터 유효성 검사.
            if (response == null) {
                Logger.d("Response Null");
                return;
            }

            if (ClientUtil.Rest.OK.code == response.code() && mActionListener != null) {
                mActionListener.onSuccess(response.body());
            }
        }

        @Override
        public void onFailure(Call<TestModel> call, Throwable t) {
            super.onFailure(call, t);
            if (mActionListener != null) {
                mActionListener.onFail(t.getMessage());
            }
        }
    };

    @Override
    public void run() {

        //requestTest안에 차량 번호판 입력!!
        ClientUtil.getInstance().getRetrofit()
                .create(APIService.class)
                .requestTest("15가1234").enqueue(mCallBack);
    }
}
