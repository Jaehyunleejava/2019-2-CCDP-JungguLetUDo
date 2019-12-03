package com.parkingapplication.networks.listener;

/**
 * mj_parking_app
 * Class: ActionResultListener
 * Created by jsieu on 2019-11-12.
 * <p>
 * Description: 서버간 API 통신시 콜백 받는 리스너.
 */
public interface ActionResultListener<T> {
    public void onSuccess(T data);

    public void onFail(String error);
}
