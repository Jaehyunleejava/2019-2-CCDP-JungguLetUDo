package com.parkingapplication.utils;

import android.app.Activity;
import android.content.Intent;

import com.parkingapplication.R;
import com.parkingapplication.activity.AdministratorActivity;
import com.parkingapplication.activity.CarResultActivity;
import com.parkingapplication.activity.IntroActivity;
import com.parkingapplication.activity.PasswordActivity;
import com.parkingapplication.activity.YoloActivity;

/**
 * mj_application_v1
 * Class: MoveActivityUtil
 * Created by jsieu on 2019-10-05.
 * <p>
 * Description:
 */
public class MoveActivityUtil {

    public static final int REQUEST_INTRO = 1000;

    private static final MoveActivityUtil instance = new MoveActivityUtil();

    public static MoveActivityUtil getInstance() {
        return instance;
    }

    private MoveActivityUtil() { }

    /**
     * 인트로 페이지 진입 함수.
     *
     * @param activity
     * @author min jin Park
     */
    public synchronized void moveIntroActivity(final Activity activity) {
        Intent intent = new Intent(activity, IntroActivity.class);
        activity.startActivityForResult(intent, REQUEST_INTRO);
        activity.overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    public synchronized void movePasswordActivity(final Activity activity) {
        Intent intent = new Intent(activity, PasswordActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    public synchronized void moveAdminActivity(final Activity activity){
        Intent intent = new Intent(activity, AdministratorActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    public synchronized void moveCarResultActivity(final Activity activity){
        Intent intent = new Intent(activity, CarResultActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    public synchronized void moveYoloAct(final Activity activity){
        Intent intent = new Intent(activity, YoloActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }
}
