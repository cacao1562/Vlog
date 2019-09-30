package kr.co.valuesys.vlog.mobile.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.co.valuesys.vlog.mobile.Application.MobileApplication;
import kr.co.valuesys.vlog.mobile.Common.Constants;
import kr.co.valuesys.vlog.mobile.Common.KakaoSessionCallback;
import kr.co.valuesys.vlog.mobile.Common.LogUtil;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.ActivitySplashBinding;

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
        deleteTempFiles();

        LogUtil.d("splash", " kaako " + Session.getCurrentSession().checkAndImplicitOpen() );
        LogUtil.d("splash", " facebook " + AccessToken.isCurrentAccessTokenActive() );
        LogUtil.d("splash", " facebook 22 " + AccessToken.isDataAccessActive() );
        LogUtil.d("splash", " facebook 33 " + AccessToken.getCurrentAccessToken() );

        CallbackManager callbackManager = CallbackManager.Factory.create();


//        if (Session.getCurrentSession().checkAndImplicitOpen() || AccessToken.isCurrentAccessTokenActive()) {
//
//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//
//        }else {
//
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finish();
//        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (AccessToken.isCurrentAccessTokenActive()) {
            LogUtil.d("facebook", " splash token = active " + Profile.getCurrentProfile().getFirstName() + Profile.getCurrentProfile().getLastName() );
        }else {
            LogUtil.d("facebook", " splash token = not active " );
        }

        if (accessToken != null) {
            LogUtil.d("facebook", " splash token = null");
        }else {
            LogUtil.d("facebook", " splash token = " + accessToken );
        }

        callback = new KakaoSessionCallback( (result, exception) -> {

            if (result) {

                MobileApplication.getContext().requestMe();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }else {

                if (exception != null) {
                    exception.printStackTrace();
                }
                presentLoginView();
            }

        });


        Session.getCurrentSession().addCallback(callback);


        binding.logo.postDelayed(() -> {
            if (!Session.getCurrentSession().checkAndImplicitOpen()) {
                presentLoginView();
            }
        }, 500);

        LogUtil.d("sss", " check = " + Session.getCurrentSession().checkAndImplicitOpen());
        LogUtil.d("sss", " isOpenable = " + Session.getCurrentSession().isOpenable());
        LogUtil.d("sss", " isOpened = " + Session.getCurrentSession().isOpened());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private void presentLoginView() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    // temp 폴더 아래에 있는 파일 삭제
    private void deleteTempFiles() {

        final File dir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        String path = dir.getPath() + "/DCIM/" + Constants.Temp_Folder_Name + "/";
        File temp = new File(path);

        if (temp.exists()) {

            File[] childs = temp.listFiles();

            if (childs != null) {

                for (File file : childs) {

                    if (file.delete()) {

                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    }

                }


            }
        }

    }

    // 카카오 sdk 홈페이지에 입력 해야할 해시키값
    private void getHashKey() {
        try {                                                        // 패키지이름을 입력해줍니다.
            PackageInfo info = getPackageManager().getPackageInfo("kr.co.valuesys.vlog.mobile", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, "key_hash=" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}
