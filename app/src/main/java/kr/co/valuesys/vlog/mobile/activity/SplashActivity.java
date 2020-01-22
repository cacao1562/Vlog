package kr.co.valuesys.vlog.mobile.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Base64;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.co.valuesys.vlog.mobile.application.MobileApplication;
import kr.co.valuesys.vlog.mobile.common.FileManager;
import kr.co.valuesys.vlog.mobile.common.LogUtil;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.common.PermissionUtils;
import kr.co.valuesys.vlog.mobile.databinding.ActivitySplashBinding;

import static kr.co.valuesys.vlog.mobile.common.Constants.FaceBook;
import static kr.co.valuesys.vlog.mobile.common.Constants.Kakao;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private ActivitySplashBinding binding;

    private ISessionCallback callback;

    private String session;

    private PermissionUtils m_permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

//        getHashKey();
        FileManager.deleteTempFiles(this);

        binding.spLogo.postDelayed(() -> {


//            MobileApplication.getContext().writeLog(MobileApplication.getContext().getLoginkName());

            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();


        }, 1000);

//        m_permissionUtils = new PermissionUtils(this, this);
//
//        LogUtil.d(TAG, " kaako " + Session.getCurrentSession().checkAndImplicitOpen());
//        LogUtil.d(TAG, " kaako oepn " + Session.getCurrentSession().isOpenable());
//
//        LogUtil.d(TAG, " facebook " + AccessToken.isCurrentAccessTokenActive());
//        LogUtil.d(TAG, " facebook 22 " + AccessToken.isDataAccessActive());
//        LogUtil.d(TAG, " facebook 33 " + AccessToken.getCurrentAccessToken());
//
//
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
//        LogUtil.d(TAG, " facebook 444 " + isLoggedIn);
//
//        session = MobileApplication.getLoginSession();
//        LogUtil.d(TAG, " session = " + session);
//
//        if (TextUtils.equals(session, Kakao)) {
//
//            MobileApplication.getContext().requestMe(result -> {
//
//                presentNext();
//
//            });
//
//        } else if (TextUtils.equals(session, FaceBook)) {
//
//            if (AccessToken.getCurrentAccessToken() != null) {
//
//                MobileApplication.getContext().useLoginInformation(AccessToken.getCurrentAccessToken(), result -> {
//
//                    presentNext();
//
//                });
//            }
//
//        } else {
//
//            presentNext();
//
//        }


//        callback = new KakaoSessionCallback( (result, exception) -> {
//
//            if (result) {
//
//                MobileApplication.getContext().requestMe();
//                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//
//            }else {
//
//                if (exception != null) {
//                    exception.printStackTrace();
//                }
//                presentLoginView();
//            }
//
//        });
//
//
//        Session.getCurrentSession().addCallback(callback);
//
//
//        binding.logo.postDelayed(() -> {
//            if (!Session.getCurrentSession().checkAndImplicitOpen()) {
//                presentLoginView();
//            }
//        }, 500);

    }

    private void presentNext() {

        binding.spLogo.postDelayed(() -> {

            if (!TextUtils.isEmpty(session)) {

                if (m_permissionUtils.checkPermission() == false) {
                    m_permissionUtils.requestPermission();

                } else {

                    MobileApplication.getContext().writeLog(MobileApplication.getContext().getLoginkName());

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }


            } else {

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();

            }

        }, 1000);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (m_permissionUtils.permissionResult(requestCode, permissions, grantResults) == false) {
            m_permissionUtils.requestPermission();
        } else {
            presentNext();
        }
    }

    /**
     * 카카오 sdk 홈페이지에 입력 해야할 해시키값 , 페이스북도 마찬가지
     */
    private void getHashKey() {
        try {                                                        // 패키지이름을 입력해줍니다.
            PackageInfo info = getPackageManager().getPackageInfo("kr.co.valuesys.vlog.mobile", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                LogUtil.d(TAG, "key_hash=" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Session.getCurrentSession().removeCallback(callback);
    }


}
