package com.parkingapplication.networks.controller;

import java.util.ArrayList;

/**
 * mj_parking_app
 * Class: NetworkManager
 * Created by MinjinPark on 2019-11-12.
 * <p>
 * Description: Network Controller Single Ton 클래스.
 */
public class NetworkManager {

    private final ArrayList<Runnable> mRunList = new ArrayList<>();

    // [s] Single Ton 클래스 영역
    private NetworkManager() {
    }

    private static class LazyHolder {
        static final NetworkManager instance = new NetworkManager();
    }

    public static NetworkManager getInstance() {
        return LazyHolder.instance;
    }
    // [e] Single Ton 클래스 영역

    /**
     * API Action Add Func.
     *
     * @param run Runnable
     * @author MinjinPark
     */
    public NetworkManager add(Runnable run) {
        mRunList.add(run);
        return this;
    }

    /**
     * API Action Run Func.
     *
     * @author MinjinPark
     */
    public NetworkManager runNext() {
        if (!mRunList.isEmpty()) {
            mRunList.get(0).run();
            mRunList.remove(0);
        }
        return this;
    }

    public void clear() {
        mRunList.clear();
    }
}
