package com.parkingapplication.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.parkingapplication.R;
import com.parkingapplication.utils.MoveActivityUtil;

/**
 * ParkingApplication
 * Class: AdministratorActivity
 * Created by MinjinPark on 2019-10-23.
 * <p>
 * Description:
 */
public class AdministratorActivity extends BaseActivity implements View.OnClickListener{

    //TODO AdminActivity 구체화
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);
        initView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //TODO MainActivity에서 Car Detect(YOLO)시 CarResultActivity 전환
            case R.id.img_logo:
                MoveActivityUtil.getInstance().moveCarResultActivity(mActivity);
                break;
        }
    }

    private void initView() {
        //setListener
        findViewById(R.id.img_logo).setOnClickListener(this);
    }
}
