package kr.co.valuesys.vlog.mobile.Activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;

import org.json.JSONException;
import org.json.JSONObject;

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

        LogUtil.d("123", " kaako " + Session.getCurrentSession().checkAndImplicitOpen() );
        LogUtil.d("123", " facebook " + AccessToken.isCurrentAccessTokenActive() );
        LogUtil.d("123", " facebook 22 " + AccessToken.isDataAccessActive() );
        LogUtil.d("123", " facebook 33 " + AccessToken.getCurrentAccessToken() );

        callback = new KakaoSessionCallback( (result, exception) -> {

            if (result) {

                MobileApplication.getContext().requestMe();
                final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

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
                final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                AccessToken token = loginResult.getAccessToken();
                MobileApplication.getContext().useLoginInformation(token);
                LogUtil.d("login", " facebook " + AccessToken.isCurrentAccessTokenActive() );
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
        LogUtil.d("mmm", "onCreate Login");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        LoginManager.getInstance().logOut();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }


}
