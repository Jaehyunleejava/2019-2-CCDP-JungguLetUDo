package com.parkingapplication.networks.dataModel;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * mj_parking_app
 * Class: TestModel
 * Created by jsieu on 2019-11-12.
 * <p>
 * Description:
 */
public class TestModel implements Serializable {

    String result;

    /**
     * getter Func.
     * 차량번호 조회 값
     * @return result == null 인경우 빈값 리턴.
     */
    public String getResult() {
        return (result == null) ? "" : result;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ result=" + result + "}";
    }

}
