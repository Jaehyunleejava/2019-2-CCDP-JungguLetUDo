package com.parkingapplication.activity;

import android.os.Bundle;
import android.widget.BaseExpandableListAdapter;

import androidx.annotation.Nullable;

import com.parkingapplication.R;

/**
 * ParkingApplication
 * Class: AdministratorActivity
 * Created by MinjinPark on 2019-10-23.
 * <p>
 * Description:
 */
public class AdministratorActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);
    }
}
