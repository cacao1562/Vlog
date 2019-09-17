package kr.co.valuesys.vlog.mobile.Activity;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import kr.co.valuesys.vlog.mobile.Fragment.AppInfoFragment;
import kr.co.valuesys.vlog.mobile.Fragment.CameraFragment;
import kr.co.valuesys.vlog.mobile.Fragment.VideoListFragment;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements AppInfoFragment.OnAppInfoListener {

    private ActivityMainBinding binding;

    private AppInfoFragment appInfoFragment = AppInfoFragment.newInstance();
    private CameraFragment cameraFragment = CameraFragment.newInstance();
//    private CalendarFragment videoListFragment = CalendarFragment.newInstance();
    private VideoListFragment videoListFragment = VideoListFragment.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (null == savedInstanceState) {

//            binding.bottomView.setSelectedItemId(R.id.two);
//            getSupportFragmentManager().beginTransaction().replace(R.id.container, cameraFragment).commit();
        }

        binding.bottomView.getMenu().getItem(0).setCheckable(false);

        binding.bottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (menuItem.getItemId()) {
                    case R.id.one :
                        binding.bottomView.getMenu().getItem(0).setCheckable(true);
                        transaction.replace(R.id.container, appInfoFragment).commit();
                        break;
                    case R.id.two :
                        transaction.replace(R.id.container, cameraFragment).commit();
                        break;
                    case R.id.three :
                        transaction.replace(R.id.container, videoListFragment).commit();
                        break;
                }
                return true;
            }
        });



    }


    @Override
    public void onClickBack() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, cameraFragment).commit();
        binding.bottomView.setSelectedItemId(R.id.two);
    }


}
