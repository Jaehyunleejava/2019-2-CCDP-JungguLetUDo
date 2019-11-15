package com.parkingapplication.networks.dataModel;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * mj_parking_app
 * Class: TestModel
 * Created by jsieu on 2019-11-12.
 * <p>
 * Description:
 */
public class TestModel implements Serializable {

    class TestTitle implements Serializable {
        String title;

        public String getTitle() {
            return title;
        }

        @NonNull
        @Override
        public String toString() {
            return "{" +
                    "title=" + title +
                    "}";
        }
    }

    private List<TestTitle> result;

    public List<TestTitle> getResult() {
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "{" +
                "result=" + result.toString() +
                "}";
    }
}
