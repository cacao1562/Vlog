package kr.co.valuesys.vlog.mobile.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import kr.co.valuesys.vlog.mobile.common.CommonInterface;
import kr.co.valuesys.vlog.mobile.common.LogUtil;
import kr.co.valuesys.vlog.mobile.dialogFragment.AppInfoFragment;
import kr.co.valuesys.vlog.mobile.dialogFragment.CalendarFragment;
import kr.co.valuesys.vlog.mobile.dialogFragment.Camera2Fragment;
import kr.co.valuesys.vlog.mobile.fragment.VideoListFragment;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements CommonInterface.OnCallbackToMain {

    private static final String TAG = "MainActivity";
    private static final int Permission_Request_Code = 200;
    private boolean isPermission = true;

    private ActivityMainBinding binding;

//    private VideoListFragment videoListFragment = VideoListFragment.newInstance();

    private static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };


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

        switch (id) {

            case 0:
                AppInfoFragment aif = AppInfoFragment.newInstance();
                aif.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
                aif.show(getSupportFragmentManager(), "tag");
                break;

            case 1:
//                CameraFragment cf = CameraFragment.newInstance();
                Camera2Fragment cf = Camera2Fragment.newInstance();
                cf.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
                cf.show(getSupportFragmentManager(), "tag");
                break;

            case 2:
                CalendarFragment clf = CalendarFragment.newInstance();
                clf.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
                clf.show(getSupportFragmentManager(), "tag");
                break;
        }

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
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, VideoListFragment.newInstance()).commit();
                }
                break;
        }

    }

    private void requestPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this
                    , PERMISSIONS
                    , Permission_Request_Code);
        }else {

            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, VideoListFragment.newInstance()).commit();
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

//        if (mpause) {
//            mpause = false;
////            CameraFragment cf = CameraFragment.newInstance();
////            cf.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
////            cf.show(getSupportFragmentManager(), "tag");
//
//            if (cf.isVisible()) {
//                cf.dismiss();
////                cf.show(getSupportFragmentManager(), "tag1");
//                CameraFragment cameraFragment = CameraFragment.newInstance();
//                cf.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
//                cf.show(getSupportFragmentManager(), "tag");
//            }
//        }
    }

    @Override
    public void oncallbackMain(int id) {

        VideoListFragment vf = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.main_container);

// 캘린더에서 날짜 눌렀을때 콜백
        if (id == 2) {

            if (vf != null) {
                vf.scrolltoVideo();
            }

// 동영상 제목 입력하는 뷰에서 저장 버튼누르고 확인 눌렀을때 콜백
        }else if (id == 1) {

            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

            if (fragmentList != null) {

                for(Fragment fragment : fragmentList) {

//                    if (fragment instanceof CameraFragment) {
                    if (fragment instanceof Camera2Fragment) {

//                        ((CameraFragment) fragment).dismiss();
                        ((Camera2Fragment) fragment).dismiss();

                        if (vf != null) {
                            vf.refreshVideo(true);
                        }

                    }
                }

            }

        }


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