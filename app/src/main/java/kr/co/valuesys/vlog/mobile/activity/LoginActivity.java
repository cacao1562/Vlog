package kr.co.valuesys.vlog.mobile.activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;

import java.util.Arrays;

import kr.co.valuesys.vlog.mobile.application.MobileApplication;
import kr.co.valuesys.vlog.mobile.common.KakaoSessionCallback;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.common.LogUtil;
import kr.co.valuesys.vlog.mobile.common.PermissionUtils;
import kr.co.valuesys.vlog.mobile.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private ActivityLoginBinding binding;

// kakao
    private ISessionCallback callback;

// facebook
    private CallbackManager callbackManager;

//    private PermissionUtils m_permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

//        m_permissionUtils = new PermissionUtils(this, this);

        callback = new KakaoSessionCallback( (result, exception) -> {
            LogUtil.d(TAG, " KakaoSessionCallback result " + result );
            if (result) {

                MobileApplication.getContext().requestMe(res -> {
                    LogUtil.d(TAG, " requestMe result " + res );
                    if (res) {

//                        checkPermission();
                        presentMain();

                    }

                });

            }else {

                if (exception != null) {
                    exception.printStackTrace();
                }
            }

        });
        Session.getCurrentSession().addCallback(callback);

        binding.facebookButton.setPermissions(Arrays.asList("email", "public_profile"));
        callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        LogUtil.d(TAG, " facebook 444 " + isLoggedIn );

        if (isLoggedIn) {
            MobileApplication.getContext().useLoginInformation(accessToken, result -> {

                if (result) {

//                    checkPermission();
                    presentMain();
                }

            });
        }else {
            Session.getCurrentSession().checkAndImplicitOpen();
        }




//        LogUtil.d(TAG, " kaako " + Session.getCurrentSession().checkAndImplicitOpen());
//        LogUtil.d(TAG, " kaako oepn " + Session.getCurrentSession().isOpenable());
//        LogUtil.d(TAG, " facebook " + AccessToken.isCurrentAccessTokenActive());
//        LogUtil.d(TAG, " facebook 22 " + AccessToken.isDataAccessActive());
//        LogUtil.d(TAG, " facebook 33 " + AccessToken.getCurrentAccessToken());







        binding.facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken token = loginResult.getAccessToken();
                MobileApplication.getContext().useLoginInformation(token, result -> {

                    if (result) {

//                        checkPermission();
                        presentMain();

                    }

                });

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }

        });


        binding.customFbButton.setOnClickListener(v -> {
            binding.facebookButton.performClick();
        });

        binding.customKakaoButton.setOnClickListener(v -> {
            binding.kakaoButton.performClick();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.d(TAG, "onActivityResult requestCode = " + requestCode);
        LogUtil.d(TAG, "onActivityResult resultCode = " + resultCode);
        LogUtil.d(TAG, "onActivityResult data = " + data);
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

//    private void checkPermission() {
//
//        if (m_permissionUtils.checkPermission() == false) {
//            m_permissionUtils.requestPermission();
//
//        }else {
//            presentMain();
//        }
//    }

    private void presentMain() {

        MobileApplication.getContext().writeLog(MobileApplication.getContext().getLoginkName());

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (m_permissionUtils.permissionResult(requestCode, permissions, grantResults) == false) {
//            m_permissionUtils.requestPermission();
//        }else {
//            presentMain();
//        }
//    }


    @Override
    protected void onDestroy() {
        Session.getCurrentSession().removeCallback(callback);
        binding.facebookButton.unregisterCallback(callbackManager);
        super.onDestroy();

    }


}
