package com.parkingapplication.networks.network;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.parkingapplication.networks.controller.NetworkManager;
import com.parkingapplication.networks.listener.ActionResultListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * mj_parking_app
 * Class: BaseNetwork
 * Created by MinjinPark on 2019-11-12.
 * <p>
 * Description: Retrofit2 Request Base Action Abstract Class
 */
abstract class BaseNetwork<T> implements Runnable {

    public enum Type {
        NEXT(1),
        NOT_RESPONSE(2),
        ERROR(3);
        public int value;

        Type(int value) {
            this.value = value;
        }
    }

    protected void actionDone(@NonNull Type type) {
        // Next Type 인경우 Next Action Call.
        switch (type) {
            case NEXT:
                NetworkManager.getInstance().runNext();
                break;
            default:
                actionDone(type, "");
                break;
        }
    }

    abstract void actionDone(@NonNull Type type, @Nullable String msg);

    @Nullable
    Context mContext = null;
    @Nullable
    ActionResultListener<T> mActionListener = null;

    /**
     * Base CallBack Class.
     */
    protected class BaseCallBack implements Callback<T> {
        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            actionDone(Type.NEXT);
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            actionDone(Type.ERROR, t.getMessage());
        }
    }
}
