package kr.co.valuesys.vlog.mobile.Activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import kr.co.valuesys.vlog.mobile.Fragment.AppInfoFragment;
import kr.co.valuesys.vlog.mobile.Fragment.CameraFragment;
import kr.co.valuesys.vlog.mobile.Fragment.VideoListFragment;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

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
            Log.d("aaa", "main savedInstanceState is null");
//            binding.bottomView.setSelectedItemId(R.id.two);
//            getSupportFragmentManager().beginTransaction().replace(R.id.container, videoListFragment).commit();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, videoListFragment).commit();
//        binding.bottomView.getMenu().getItem(0).setCheckable(false);

//        binding.bottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//                switch (menuItem.getItemId()) {
//                    case R.id.one :
//                        binding.bottomView.getMenu().getItem(0).setCheckable(true);
//                        transaction.replace(R.id.container, appInfoFragment).commit();
//                        break;
//                    case R.id.two :
//                        transaction.replace(R.id.container, cameraFragment).commit();
//                        break;
//                    case R.id.three :
//                        transaction.replace(R.id.container, videoListFragment).commit();
//                        break;
//                }
//                return true;
//            }
//        });

        binding.mainInfoImgbutton.setOnClickListener(v -> { presentBlankActivity(0); });
        binding.mainRecordImgbutton.setOnClickListener(v -> { presentBlankActivity(1); });
        binding.mainCalendarImgbutton.setOnClickListener(v -> { presentBlankActivity(2); });

    }

    private void presentBlankActivity(int id) {

        Intent intent = new Intent(MainActivity.this, BlankActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);

    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d("aaa", "============== main onResume ");
//        getSupportFragmentManager().beginTransaction().detach(videoListFragment).attach(videoListFragment).commit();
    }
}
