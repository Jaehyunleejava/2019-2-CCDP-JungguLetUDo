package com.parkingapplication.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.parkingapplication.R;
import com.parkingapplication.utils.Logger;
import com.parkingapplication.utils.MoveActivityUtil;

import java.util.Stack;

/**
 * ParkingApplication
 * Class: PasswordActivity
 * Created by MinjinPark on 2019-10-23.
 * <p>
 * Description:
 */
public class PasswordActivity extends BaseActivity implements View.OnClickListener {

    private final int [] mPassword={1,2,3,4};
    private Stack<Integer> mMaskingStack = new Stack<>();

    private TextView mTxtMaskingOne;
    private TextView mTxtMaskingTwo;
    private TextView mTxtMaskingThree;
    private TextView mTxtMaskingFour;

    private LinearLayout mLlMasking;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        initView();
    }

    /**
     * init View..
     */
    private void initView() {
        mTxtMaskingOne = findViewById(R.id.txt_masking_one);
        mTxtMaskingTwo = findViewById(R.id.txt_masking_two);
        mTxtMaskingThree = findViewById(R.id.txt_masking_three);
        mTxtMaskingFour = findViewById(R.id.txt_masking_four);
        mLlMasking = findViewById(R.id.ll_password);

        //setListener
        findViewById(R.id.txt_password_one).setOnClickListener(this);
        findViewById(R.id.txt_password_two).setOnClickListener(this);
        findViewById(R.id.txt_password_three).setOnClickListener(this);
        findViewById(R.id.txt_password_four).setOnClickListener(this);
        findViewById(R.id.txt_password_five).setOnClickListener(this);
        findViewById(R.id.txt_password_six).setOnClickListener(this);
        findViewById(R.id.txt_password_seven).setOnClickListener(this);
        findViewById(R.id.txt_password_eight).setOnClickListener(this);
        findViewById(R.id.txt_password_nine).setOnClickListener(this);
        findViewById(R.id.txt_password_zero).setOnClickListener(this);
        findViewById(R.id.txt_password_remove).setOnClickListener(this);
    }

    // Ctrl + i
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_password_one:
            case R.id.txt_password_two:
            case R.id.txt_password_three:
            case R.id.txt_password_four:
            case R.id.txt_password_five:
            case R.id.txt_password_six:
            case R.id.txt_password_seven:
            case R.id.txt_password_eight:
            case R.id.txt_password_nine:
            case R.id.txt_password_zero:
                if(isMaskingAvail()){
                    Logger.d("푸시푸시");
                    mMaskingStack.push(v.getId());
                    maskingShow();
                }
                break;

            case R.id.txt_password_remove:
                Logger.d(" password removed ");
                if(mMaskingStack.size()>0) {
                    mMaskingStack.pop();
                    maskingShow();
                }
                break;
        }
    }

    private boolean isMaskingAvail(){
        Logger.d("TEST:: 유무 체크");
        return mMaskingStack.size()<4;
    }

    /**
     * 비밀 번호 체크 Func.
     * @return  true -> 비밀번호 4자리수 승인 완료, false -> 비밀번호 틀림.
     */
    private boolean checkPassword(){
        for (int i=mMaskingStack.size()-1; i>=0;i--){
            int k = getCastId(mMaskingStack.pop());
            // 하나라도 숫자가 다른경우 false 리턴
            if (k != mPassword[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * View Id 값을 알맞은 값으로 변환.
     * @param id
     * @return
     */
    private int getCastId(int id){
        switch (id){
            case R.id.txt_password_one:
                return 1;

            case R.id.txt_password_two:
                return 2;

            case R.id.txt_password_three:
                return 3;

            case R.id.txt_password_four:
                return 4;

            case R.id.txt_password_five:
                return 5;

            case R.id.txt_password_six:
                return 6;

            case R.id.txt_password_seven:
                return 7;

            case R.id.txt_password_eight:
                return 8;

            case R.id.txt_password_nine:
                return 9;

            default:
                return 0;

        }
    }

    /**
     * 마스킹 View 표시.
     */
    private void maskingShow(){
        switch(mMaskingStack.size()){
            default:
                break;

            case 1:
                mTxtMaskingOne.setVisibility(View.VISIBLE);
                mTxtMaskingTwo.setVisibility(View.INVISIBLE);
                mTxtMaskingThree.setVisibility(View.INVISIBLE);
                mTxtMaskingFour.setVisibility(View.INVISIBLE);
                break;

            case 2:
                mTxtMaskingOne.setVisibility(View.VISIBLE);
                mTxtMaskingTwo.setVisibility(View.VISIBLE);
                mTxtMaskingThree.setVisibility(View.INVISIBLE);
                mTxtMaskingFour.setVisibility(View.INVISIBLE);
                break;

            case 3:
                mTxtMaskingOne.setVisibility(View.VISIBLE);
                mTxtMaskingTwo.setVisibility(View.VISIBLE);
                mTxtMaskingThree.setVisibility(View.VISIBLE);
                mTxtMaskingFour.setVisibility(View.INVISIBLE);
                break;

            case 4:
                mTxtMaskingOne.setVisibility(View.VISIBLE);
                mTxtMaskingTwo.setVisibility(View.VISIBLE);
                mTxtMaskingThree.setVisibility(View.VISIBLE);
                mTxtMaskingFour.setVisibility(View.VISIBLE);
                if (checkPassword()){
                    passwordRight();
                } else {
                    passwordWrong();
                }
                break;
        }
    }

    private void passwordRight() {
        Toast.makeText(mContext,"correct",Toast.LENGTH_SHORT).show();
        MoveActivityUtil.getInstance().moveAdminActivity(mActivity);
    }

    private void passwordWrong(){
        Toast.makeText(mContext,"wrong",Toast.LENGTH_SHORT).show();
        Animation shakeAni = AnimationUtils.loadAnimation(mContext,R.anim.anim_shake);
        mLlMasking.animate().cancel();
        mLlMasking.startAnimation(shakeAni);
        shakeAni.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Logger.d("TEST:: 애니메이션 시작");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Logger.d("TEST:: 애니메이션 끝");
                mMaskingStack.clear();

                mTxtMaskingOne.setVisibility(View.INVISIBLE);
                mTxtMaskingTwo.setVisibility(View.INVISIBLE);
                mTxtMaskingThree.setVisibility(View.INVISIBLE);
                mTxtMaskingFour.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


}
