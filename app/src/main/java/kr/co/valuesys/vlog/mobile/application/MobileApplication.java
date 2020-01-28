package kr.co.valuesys.vlog.mobile.application;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.kakao.auth.AccessTokenCallback;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthService;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserAccount;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.activity.SplashActivity;
import kr.co.valuesys.vlog.mobile.common.CommonInterface;
import kr.co.valuesys.vlog.mobile.common.Constants;
import kr.co.valuesys.vlog.mobile.common.LogUtil;

//import io.realm.Realm;
//import io.realm.RealmConfiguration;

public class MobileApplication extends Application {

    private static MobileApplication mobileApplication;

    public static MobileApplication getContext() {
        return mobileApplication;
    }

    /**
     * 로그인 이름
     */
    private String mLoginName;
    /**
     * SNS 로그인 방식 ( 카카오 or 페이스북 )
     */
    private String mLoginPlatform;

    /**
     * 캘린더에서 선택한 날짜
     */
    private CalendarDay mSelectDay;

    public void setmLoginName(String mLoginName) {
        this.mLoginName = mLoginName;
    }

    public String getLoginkName() {
        return mLoginName;
    }

    public String getmLoginPlatform() {
        return mLoginPlatform;
    }

    public void setmLoginPlatform(String mLoginPlatform) {
        this.mLoginPlatform = mLoginPlatform;
    }

    public CalendarDay getmSelectDay() {
        return mSelectDay;
    }

    public void setmSelectDay(CalendarDay mSelectDay) {
        this.mSelectDay = mSelectDay;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mobileApplication = this;

        KakaoSDK.init(new KakaoSDKAdapter());

        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);

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

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss");

    public static String convertDateToString(Date date) {

        String str = sdf.format(date);
        return str;
    }

    public static String convertDateToString_time(Date date) {

        String str = sdf2.format(date);
        return str;
    }

    private static class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         *
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
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

    // kakao 토킅 가져오기
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


    /**
     * kakao 사용자 정보 가져오기
     */
    public void requestMe(CommonInterface.OnRequestGetLoginInfo callback) {

        List<String> keys = new ArrayList<>();
        keys.add("properties.nickname");
        keys.add("properties.profile_image");
        keys.add("kakao_account.email");
        keys.add("kakao_account.profile");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                LogUtil.d("kakao requestMe onFailure", message);
                callback.onCallback(false, errorResult.toString());
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
//                redirectLoginActivity()
                LogUtil.d("kakao requestMe ", "onSessionClosed");
//                callback.onCallback(false, errorResult.toString());
            }

            @Override
            public void onSuccess(MeV2Response response) {

                UserAccount userAccount = null;
                if (response != null) {
                    if (response.getKakaoAccount() != null) {
                        userAccount = response.getKakaoAccount();
                        LogUtil.d("kakao ", "userAccount  : " + userAccount.toString());
                        if (userAccount.profileNeedsAgreement() != null) {
                            if (userAccount.profileNeedsAgreement().getBoolean()) {
                                handleScopeError(userAccount);
                            } else {
                                if (userAccount.getProfile() != null) {
                                    if (userAccount.getProfile().getNickname() != null) {
                                        LogUtil.d("kakao ", "profile nick name: " + userAccount.getProfile().getNickname());
                                        mLoginName = userAccount.getProfile().getNickname();
                                    }
                                }
                            }
                        }
                    }
                }

                LogUtil.d("kakao ", "user id : " + response.getId());
                LogUtil.d("kakao ", "nick name: " + response.getNickname());

//                mLoginName = response.getNickname();
                mLoginPlatform = Constants.Kakao;

//                redirectMainActivity();
                callback.onCallback(true, "");
            }

        });

    }

    private void handleScopeError(UserAccount account) {
        List<String> neededScopes = new ArrayList<>();
        if (account.emailNeedsAgreement().getBoolean()) {
            neededScopes.add("account_email");
        }
        if (account.genderNeedsAgreement().getBoolean()) {
            neededScopes.add("gender");
        }
        Session.getCurrentSession().updateScopes(((SplashActivity) this.getApplicationContext()), neededScopes, new
                AccessTokenCallback() {


                    @Override
                    public void onAccessTokenReceived(com.kakao.auth.authorization.accesstoken.AccessToken accessToken) {
                        // 유저에게 성공적으로 동의를 받음. 토큰을 재발급 받게 됨.
                    }

                    @Override
                    public void onAccessTokenFailure(ErrorResult errorResult) {
                        // 동의 얻기 실패
                    }
                });
    }

    // facebook 이름 가져오기
    public void setFbName() {

        mLoginName = Profile.getCurrentProfile().getName();
    }

    /**
     * facebook 로그인 정보 가져오기
     */
    public void useLoginInformation(AccessToken accessToken, CommonInterface.OnRequestGetLoginInfo callback) {
        /**
         Creating the GraphRequest to fetch user details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                if (object == null || response.getError() != null) {
                    if (callback != null) {
                        callback.onCallback(false, response.getError().getErrorMessage());
                        return;
                    }
                    return;
                }
                try {
                    String name = object.getString("name");
                    String email = object.getString("email");
                    String image = object.getJSONObject("picture").getJSONObject("data").getString("url");
//                    displayName.setText(name);
//                    emailID.setText(email);
                    LogUtil.d("facebook", " name = " + name);
                    mLoginName = name;
                    mLoginPlatform = Constants.FaceBook;

                    callback.onCallback(true, "");

                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onCallback(false, e.getMessage());
                }
            }
        });

        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }


    public static String getLoginSession() {

        boolean kakaoSession = Session.getCurrentSession().checkAndImplicitOpen();
        boolean fbSession = AccessToken.isCurrentAccessTokenActive();

        if (kakaoSession) {
            return Constants.Kakao;
        } else if (fbSession) {
            return Constants.FaceBook;
        }
        return "";
    }


    public static AlertDialog.Builder showProgress(String title, String msg, Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View innerView = inflater.inflate(R.layout.dialog_progress, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
//        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogStyle));

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(msg)) {
            builder.setMessage(msg);
        }
//        final ProgressBar progressBar = new ProgressBar(this);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        progressBar.setLayoutParams(lp);

        builder.setView(innerView);
        builder.setCancelable(false);

        return builder;
    }


    /**
     * 파일에 로그인 로그 쓰기
     *
     * @param str 로그인 이름
     */
    public void writeLog(String str) {

//        String str_Path_Full = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/log.txt";
//        String str_Path_Full = Environment.getExternalStorageDirectory().getAbsolutePath();

        String path = "";
        String folderName = "/vlogLog";
        try {
            path = mobileApplication.getExternalFilesDir(null).getAbsolutePath() + folderName;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                path = mobileApplication.getExternalFilesDir(null).getAbsolutePath() + folderName;
//            } else {
//                path = Environment.getExternalStorageDirectory().getAbsolutePath() + folderName;
//            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        str += " \t " + convertDateToString_time(Calendar.getInstance().getTime());
//        LogUtil.d("lll", " path 1 = " + str_Path_Full);
        LogUtil.d("lll", " path 2 = " + path);
        File file = new File(path);
        if (file.exists() == false) {
            LogUtil.d("lll", " not exists ");
            file.mkdir();

        }

        File txtfile = new File(path + "/log.txt");
        if (txtfile.exists() == false) {
            try {
                txtfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            /** 저장할때 한글 깨져서 MS949 사용*/
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + "/log.txt" , true), "MS949"));
            bw.append(str);
            bw.newLine();
//            bw.write(str);
//            bw.write("\n");
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
