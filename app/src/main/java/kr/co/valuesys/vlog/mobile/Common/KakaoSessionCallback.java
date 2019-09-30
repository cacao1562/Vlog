package kr.co.valuesys.vlog.mobile.Common;

import com.kakao.auth.ISessionCallback;
import com.kakao.util.exception.KakaoException;

public class KakaoSessionCallback implements ISessionCallback {

    private CommonInterface.OnSessionResult callback;

    public KakaoSessionCallback(CommonInterface.OnSessionResult onSessionResult) {
        this.callback = onSessionResult;
    }

    @Override
    public void onSessionOpened() {
        callback.onCallback(true, null);
    }

    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        callback.onCallback(false, exception);
    }

}
