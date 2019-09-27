package kr.co.valuesys.vlog.mobile.Application;

import android.app.Application;
import android.content.Context;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthService;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.co.valuesys.vlog.mobile.Common.LogUtil;

//import io.realm.Realm;
//import io.realm.RealmConfiguration;

public class MobileApplication extends Application {

    private static MobileApplication mobileApplication;

    public static MobileApplication getContext() {
        return mobileApplication;
    }

    private static class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
//                    return GlobalApplication.getGlobalApplicationContext();
                    return mobileApplication;
                }
            };
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mobileApplication = this;

        KakaoSDK.init(new KakaoSDKAdapter());
//        Realm.init(this);
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        Realm.setDefaultConfiguration(realmConfiguration);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mobileApplication = null;
    }

    public String convertDateToString(Date date, String pattern) {

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String str = sdf.format(date);
        return str;
    }

    public void requestAccessTokenInfo() {

        AuthService.getInstance().requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
//                redirectLoginActivity(self);
                LogUtil.d("kakao requestAccessTokenInfo ", "onSessionClosed");
            }

            @Override
            public void onNotSignedUp() {
                // not happened
                LogUtil.d("kakao requestAccessTokenInfo ", "onNotSignedUp");
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                LogUtil.d("kakao requestAccessTokenInfo = onFailure", "failed to get access token info. msg=" + errorResult);
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                long userId = accessTokenInfoResponse.getUserId();
                LogUtil.d("kakao requestAccessTokenInfo = onSuccess", "this access token is for userId=" + userId);

                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
                LogUtil.d("kakao requestAccessTokenInfo = onSuccess ", "this access token expires after " + expiresInMilis + " milliseconds.");
            }
        });
    }


    public void requestMe() {
        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                LogUtil.d("kakao requestMe onFailure", message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
//                redirectLoginActivity()
                LogUtil.d("kakao requestMe ", "onSessionClosed" );
            }

            @Override
            public void onSuccess(MeV2Response response) {
                LogUtil.d("kakao ", "user id : " + response.getId());
//                LogUtil.d("kakao " , "email: " + response.getKakaoAccount());
                LogUtil.d("kakao " , "nick name: " + response.getNickname());
                LogUtil.d("kakao" , "profile image: " + response.getProfileImagePath());
//                redirectMainActivity();
            }

        });

    }


}
