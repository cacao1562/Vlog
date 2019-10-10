package kr.co.valuesys.vlog.mobile.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;

import java.util.Arrays;

import kr.co.valuesys.vlog.mobile.Application.MobileApplication;
import kr.co.valuesys.vlog.mobile.Common.KakaoSessionCallback;
import kr.co.valuesys.vlog.mobile.Common.LogUtil;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

// kakao
    private ISessionCallback callback;

// facebook
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.facebookButton.setPermissions(Arrays.asList("email", "public_profile"));

//        LogUtil.d("123", " kaako " + Session.getCurrentSession().checkAndImplicitOpen() );
//        LogUtil.d("123", " facebook " + AccessToken.isCurrentAccessTokenActive() );
//        LogUtil.d("123", " facebook 22 " + AccessToken.isDataAccessActive() );
//        LogUtil.d("123", " facebook 33 " + AccessToken.getCurrentAccessToken() );

        callback = new KakaoSessionCallback( (result, exception) -> {

            if (result) {

                MobileApplication.getContext().requestMe(res -> {

                    if (res) {

                        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

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

                        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
        binding.facebookButton.unregisterCallback(callbackManager);
    }


}
