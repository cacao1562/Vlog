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
import kr.co.valuesys.vlog.mobile.common.PermissionUtils;
import kr.co.valuesys.vlog.mobile.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

// kakao
    private ISessionCallback callback;

// facebook
    private CallbackManager callbackManager;

    private PermissionUtils m_permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.facebookButton.setPermissions(Arrays.asList("email", "public_profile"));
        m_permissionUtils = new PermissionUtils(this, this);

//        LogUtil.d("123", " kaako " + Session.getCurrentSession().checkAndImplicitOpen() );
//        LogUtil.d("123", " facebook " + AccessToken.isCurrentAccessTokenActive() );
//        LogUtil.d("123", " facebook 22 " + AccessToken.isDataAccessActive() );
//        LogUtil.d("123", " facebook 33 " + AccessToken.getCurrentAccessToken() );

        callback = new KakaoSessionCallback( (result, exception) -> {

            if (result) {

                MobileApplication.getContext().requestMe(res -> {

                    if (res) {

                        checkPermission();

                    }

                });

            }else {

                if (exception != null) {
                    exception.printStackTrace();
                }
            }

        });

        callbackManager = CallbackManager.Factory.create();

        binding.facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken token = loginResult.getAccessToken();
                MobileApplication.getContext().useLoginInformation(token, result -> {

                    if (result) {

                        checkPermission();

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

        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkPermission() {

        if (m_permissionUtils.checkPermission() == false) {
            m_permissionUtils.requestPermission();

        }else {
            presentMain();
        }
    }

    private void presentMain() {

        MobileApplication.getContext().writeLog(MobileApplication.getContext().getLoginkName());

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (m_permissionUtils.permissionResult(requestCode, permissions, grantResults) == false) {
            m_permissionUtils.requestPermission();
        }else {
            presentMain();
        }
    }


    @Override
    protected void onDestroy() {
        Session.getCurrentSession().removeCallback(callback);
        binding.facebookButton.unregisterCallback(callbackManager);
        super.onDestroy();

    }


}
