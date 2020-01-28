package kr.co.valuesys.vlog.mobile.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.List;

import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.common.CommonInterface;
import kr.co.valuesys.vlog.mobile.common.LogUtil;
import kr.co.valuesys.vlog.mobile.common.PermissionUtils;
import kr.co.valuesys.vlog.mobile.common.SimpleAlert;
import kr.co.valuesys.vlog.mobile.databinding.ActivityMainBinding;
import kr.co.valuesys.vlog.mobile.dialogFragment.AppInfoFragment;
import kr.co.valuesys.vlog.mobile.dialogFragment.CalendarFragment;
import kr.co.valuesys.vlog.mobile.dialogFragment.CameraFragment;
import kr.co.valuesys.vlog.mobile.fragment.VideoListFragment;

public class MainActivity extends AppCompatActivity implements CommonInterface.OnCallbackToMain {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    private PermissionUtils m_permissionUtils;

    private AlertDialog alertDialog;

    private final int Setting_Request_Code = 1010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        m_permissionUtils = new PermissionUtils(this, this);

        requestPermission();

        binding.mainInfoImgbutton.setOnClickListener(v -> { presentDialog(0); });
        binding.mainRecordImgbutton.setOnClickListener(v -> { presentDialog(1); });
        binding.mainCalendarImgbutton.setOnClickListener(v -> { presentDialog(2); });

//        MobileApplication.getContext().requestAccessTokenInfo();
//        MobileApplication.getContext().requestMe();
        LogUtil.d("mmm", "onCreate Mian");

    }

    private void presentDialog(int id) {

        if (m_permissionUtils.checkPermission() == false) {
//            Toast.makeText(this,"접근 권한을 허용해 주세요", Toast.LENGTH_LONG).show();
            m_permissionUtils.requestPermission();

            return;

        }
        /** 메인 하단 3개 버튼
         * 0 : 앱 정보
         * 1 : 카메라 촬영
         * 2 : 캘린더
         */
        switch (id) {

            case 0:
                AppInfoFragment aif = AppInfoFragment.newInstance();
                aif.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
                aif.show(getSupportFragmentManager(), "tag");
                break;

            case 1:
                CameraFragment cf = CameraFragment.newInstance();
//                Camera2Fragment cf = Camera2Fragment.newInstance();
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

//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        m_permissionUtils.permissionResult(requestCode, permissions, grantResults, result -> {

            switch (result) {
                /** 퍼미션 권한 모두 허용 했을때 */
                case 0:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, VideoListFragment.newInstance()).commit();
                    break;
                /** 퍼미션 권한 거부 했을때 다시 요청*/
                case 1:
                    m_permissionUtils.requestPermission();
                    break;
                /** 퍼미션 권한 다시 않보기 체크하고 거부 했을때 설정 화면으로 */
                case 2:
                    showPermissionAlert();
                    break;
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Setting_Request_Code) {

            requestPermission();

//            if (resultCode == RESULT_OK) {
//                LogUtil.d("ppp", "onActivityResult  OK ");
////                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, VideoListFragment.newInstance()).commit();
//            }else {
//                LogUtil.d("ppp", "onActivityResult = " + resultCode);
////                showPermissionAlert();
//            }
        }
    }

    /** 퍼미션 체크해서 한 개라도 거부되어있으면 다시 요청
     *  다 허용 되어있으면 리스트 화면 보여줌
     */
    private void requestPermission() {

        if (m_permissionUtils.checkPermission() == false) {
            m_permissionUtils.requestPermission();

        }else {
//            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, VideoListFragment.newInstance()).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, VideoListFragment.newInstance()).commitAllowingStateLoss();
        }

    }

    /** 앱 설정 화면으로 이동 */
    private void showPermissionAlert() {

        alertDialog = SimpleAlert.createAlert(this, "권한을 허용해 주세요", false, dialog -> {

            dialog.dismiss();

            try {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:" + getPackageName()));
//                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivityForResult(intent, Setting_Request_Code);
//                finish();
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();

                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                startActivityForResult(intent, Setting_Request_Code);
//                finish();
            }

        });
        alertDialog.show();
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
    protected void onStop() {
        super.onStop();
        if (alertDialog != null) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
                alertDialog = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void oncallbackMain(int id) {

        VideoListFragment vf = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.main_container);

/** 캘린더에서 날짜 눌렀을때 콜백 */
        if (id == 2) {

            if (vf != null) {
                vf.scrolltoVideo();
            }

/** 동영상 제목 입력하는 뷰에서 저장 버튼누르고 확인 눌렀을때 콜백 */
        }else if (id == 1) {

            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

            if (fragmentList != null) {

                for(Fragment fragment : fragmentList) {

                    if (fragment instanceof CameraFragment) {
//                    if (fragment instanceof Camera2Fragment) {

                        ((CameraFragment) fragment).dismiss();
//                        ((Camera2Fragment) fragment).dismiss();

                        if (vf != null) {
                            vf.refreshVideo();
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