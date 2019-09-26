package kr.co.valuesys.vlog.mobile.Activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import kr.co.valuesys.vlog.mobile.Common.CommonInterface;
import kr.co.valuesys.vlog.mobile.Common.LogUtil;
import kr.co.valuesys.vlog.mobile.Fragment.AppInfoFragment;
import kr.co.valuesys.vlog.mobile.Fragment.CalendarFragment;
import kr.co.valuesys.vlog.mobile.Fragment.CameraFragment;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.ActivityBlankBinding;

public class BlankActivity extends AppCompatActivity implements CommonInterface.OnCameraPauseListener {

    private ActivityBlankBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_blank);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_blank);

        if (getIntent() != null) {

            if (getIntent().getIntExtra("id", -1) != -1) {

                int id = getIntent().getIntExtra("id", -1);

                switch (id) {
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
        if (getIntent().getIntExtra("id", -1) == 2 ) {
            super.onBackPressed();
        }
//        super.onBackPressed();
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        if (fragmentList != null) {
            //TODO: Perform your logic to pass back press here
            for(Fragment fragment : fragmentList) {

                if(fragment instanceof CommonInterface.OnBackPressedListener) {

                    ((CommonInterface.OnBackPressedListener)fragment).onBackPressed();

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

        LogUtil.d("eee", "blank onPause");
        int id = getIntent().getIntExtra("id", -1);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.blank_container);
        if (id == 1 && isBeforeRecording) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        LogUtil.d("eee", "blank onResume");
        int id = getIntent().getIntExtra("id", -1);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.blank_container);
        if (id == 1 && fragment == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.blank_container, CameraFragment.newInstance()).commit();
        }

    }

    private boolean isBeforeRecording = false;

    @Override
    public void onCameraPause(boolean pause) {
        isBeforeRecording = pause;
    }

}
