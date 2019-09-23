package kr.co.valuesys.vlog.mobile.Activity;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;

import kr.co.valuesys.vlog.mobile.Fragment.AppInfoFragment;
import kr.co.valuesys.vlog.mobile.Fragment.CameraFragment;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.ActivityBlankBinding;

public class BlankActivity extends AppCompatActivity implements AppInfoFragment.OnAppInfoListener {

    private ActivityBlankBinding binding;

    public interface OnBackPressedListener {
        void onBackPressed();
    }


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
//                        getSupportFragmentManager().beginTransaction().replace(R.id.blank_container, CalendarFragment.newInstance()).commit();
                        Toast.makeText(this, "준비중..", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }

    }

    @Override
    public void onClickBack() {
        finish();
    }


    @Override
    public void onBackPressed() {

        if (getIntent().getIntExtra("id", -1) == 2 ) {
            super.onBackPressed();
        }
//        super.onBackPressed();
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            //TODO: Perform your logic to pass back press here
            for(Fragment fragment : fragmentList){
                if(fragment instanceof OnBackPressedListener){
                    ((OnBackPressedListener)fragment).onBackPressed();
                }else {
                    super.onBackPressed();
                }
            }
        }else {
            super.onBackPressed();
        }
    }
}
