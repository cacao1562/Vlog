package kr.co.valuesys.vlog.mobile.Activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import kr.co.valuesys.vlog.mobile.Common.CommonInterface;
import kr.co.valuesys.vlog.mobile.Common.Constants;
import kr.co.valuesys.vlog.mobile.Common.LogUtil;
import kr.co.valuesys.vlog.mobile.Fragment.AppInfoFragment;
import kr.co.valuesys.vlog.mobile.Fragment.CalendarFragment;
import kr.co.valuesys.vlog.mobile.Fragment.CameraFragment;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.ActivityBlankBinding;

public class BlankActivity extends AppCompatActivity implements CommonInterface.OnCameraPauseListener {

    private ActivityBlankBinding binding;

    private int mFragmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_blank);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_blank);

        if (getIntent() != null) {

            if (getIntent().getIntExtra(Constants.Fragment_Id, -1) != -1) {

                mFragmentId = getIntent().getIntExtra(Constants.Fragment_Id, -1);

                switch (mFragmentId) {

                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(R.id.blank_container, AppInfoFragment.newInstance()).commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.blank_container, CameraFragment.newInstance()).commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.blank_container, CalendarFragment.newInstance()).commit();
                        break;
                }
            }
        }

    }


    @Override
    public void onBackPressed() {

// calendar fragment일때 바로 finish
        if (mFragmentId == 0 || mFragmentId == 2  ) {
            super.onBackPressed();
            return;
        }
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        if (fragmentList != null) {
            //TODO: Perform your logic to pass back press here
            for(Fragment fragment : fragmentList) {

                if(fragment instanceof CommonInterface.OnBackPressedListener) {

                    ((CommonInterface.OnBackPressedListener)fragment).onBackPressedCallback();

                }else {

                    super.onBackPressed();
                }
            }

        }else {

            super.onBackPressed();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mFragmentId == 0 || mFragmentId == 2  ) {
            return;
        }

        LogUtil.d("eee", "blank onPause");
        int id = getIntent().getIntExtra(Constants.Fragment_Id, -1);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.blank_container);

        if (mFragmentId == 1 && isBeforeRecording) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mFragmentId == 0 || mFragmentId == 2  ) {
            return;
        }
        LogUtil.d("eee", "blank onResume");
        int id = getIntent().getIntExtra(Constants.Fragment_Id, -1);

// 현재 container에 있는 프래그먼트 가져옴
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.blank_container);

        if (mFragmentId == 1 && fragment == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.blank_container, CameraFragment.newInstance()).commit();
        }

    }

    private boolean isBeforeRecording = false;

// CameraFragment에서 촬영전 상태일때 홈으로 나갔을때 true 콜백
    @Override
    public void onCameraPause(boolean pause) {
        isBeforeRecording = pause;
    }

}
