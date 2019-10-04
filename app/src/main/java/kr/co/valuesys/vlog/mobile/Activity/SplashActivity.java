package kr.co.valuesys.vlog.mobile.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.facebook.AccessToken;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.co.valuesys.vlog.mobile.Application.MobileApplication;
import kr.co.valuesys.vlog.mobile.Common.FileManager;
import kr.co.valuesys.vlog.mobile.Common.LogUtil;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.ActivitySplashBinding;

import static kr.co.valuesys.vlog.mobile.Common.Constants.FaceBook;
import static kr.co.valuesys.vlog.mobile.Common.Constants.Kakao;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private ActivitySplashBinding binding;

    private ISessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

        getHashKey();
        new FileManager(this).deleteTempFiles();

        LogUtil.d(TAG, " kaako " + Session.getCurrentSession().checkAndImplicitOpen());
        LogUtil.d(TAG, " facebook " + AccessToken.isCurrentAccessTokenActive());
        LogUtil.d(TAG, " facebook 22 " + AccessToken.isDataAccessActive());
        LogUtil.d(TAG, " facebook 33 " + AccessToken.getCurrentAccessToken());

        boolean kakaoSession = Session.getCurrentSession().checkAndImplicitOpen();
        boolean fbSession = AccessToken.isCurrentAccessTokenActive();

        if (kakaoSession) {
            MobileApplication.getContext().requestMe();
            MobileApplication.getContext().setmLoginPlatform(Kakao);

        }else {

            if (AccessToken.getCurrentAccessToken() != null) {
                MobileApplication.getContext().useLoginInformation(AccessToken.getCurrentAccessToken());
            }
//            MobileApplication.getContext().setFbName();
            MobileApplication.getContext().setmLoginPlatform(FaceBook);
        }

        binding.logo.postDelayed(() -> {

            if ( kakaoSession || fbSession ) {

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {

                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();

            }

        }, 1000);


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
