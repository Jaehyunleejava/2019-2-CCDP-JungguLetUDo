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
     * ������ȣ ��ȸ ��
     * @return result == null �ΰ�� �� ����.
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
