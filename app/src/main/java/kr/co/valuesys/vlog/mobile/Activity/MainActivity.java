package kr.co.valuesys.vlog.mobile.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import kr.co.valuesys.vlog.mobile.Common.Constants;
import kr.co.valuesys.vlog.mobile.Common.LogUtil;
import kr.co.valuesys.vlog.mobile.Fragment.AppInfoFragment;
import kr.co.valuesys.vlog.mobile.Fragment.CalendarFragment;
import kr.co.valuesys.vlog.mobile.Fragment.CameraFragment;
import kr.co.valuesys.vlog.mobile.Fragment.VideoListFragment;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int Permission_Request_Code = 200;
    private boolean isPermission = true;

    private ActivityMainBinding binding;

    private VideoListFragment videoListFragment = VideoListFragment.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        requestPermission();

        binding.mainInfoImgbutton.setOnClickListener(v -> { presentBlankActivity(0); });
        binding.mainRecordImgbutton.setOnClickListener(v -> { presentBlankActivity(1); });
        binding.mainCalendarImgbutton.setOnClickListener(v -> { presentBlankActivity(2); });

//        MobileApplication.getContext().requestAccessTokenInfo();
//        MobileApplication.getContext().requestMe();
        LogUtil.d("mmm", "onCreate Mian");

    }

    private void presentBlankActivity(int id) {

        if (!isPermission) {
            Toast.makeText(this,"접근 권한을 허용해 주세요", Toast.LENGTH_LONG).show();
            requestPermission();
            return;
        }

        Intent intent = new Intent(MainActivity.this, BlankActivity.class);
        intent.putExtra(Constants.Fragment_Id, id);
        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case Permission_Request_Code:

                for (int i=0; i<grantResults.length; i++) {

                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                        isPermission = false;
                        Toast.makeText(this,"permission not granted = " + permissions[i], Toast.LENGTH_LONG).show();
                        LogUtil.d(TAG, "permission not granted = " + permissions[i] );
                    }
                }

                if (isPermission) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, videoListFragment).commit();
                }
                break;
        }

    }

    private void requestPermission() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.CAMERA
                            , Manifest.permission.RECORD_AUDIO}
                    , Permission_Request_Code);
        }else {

            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, videoListFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

//        binding.mainContainer2.setVisibility(View.GONE);
//        if (getFragmentManager().getBackStackEntryCount() > 0) {
//            getFragmentManager().popBackStack();
//
//        } else {
//            super.onBackPressed();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume");
    }
}

// BottomNavigationView 기본으로 첫번째 선택되어있는거 해제해주는거
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