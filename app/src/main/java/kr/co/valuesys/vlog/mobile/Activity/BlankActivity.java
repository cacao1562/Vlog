package kr.co.valuesys.vlog.mobile.Activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import kr.co.valuesys.vlog.mobile.Fragment.AppInfoFragment;
import kr.co.valuesys.vlog.mobile.Fragment.CameraFragment;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.ActivityBlankBinding;

public class BlankActivity extends AppCompatActivity implements AppInfoFragment.OnAppInfoListener {

    private ActivityBlankBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_blank);
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


}
