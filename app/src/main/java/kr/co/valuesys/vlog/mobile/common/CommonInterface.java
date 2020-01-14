package kr.co.valuesys.vlog.mobile.common;

import android.content.DialogInterface;

import com.kakao.util.exception.KakaoException;

public interface CommonInterface {

// BlankActivity에서 백버튼 눌렀을때 fragment로 callback
    interface OnCallbackToMain {
        void oncallbackMain(int id);
    }

// CameraFragment에서 홈버튼 누를때 촬영전 상태일때만 camerafragment remove했다 다시 생성. 이유는 홈으로 갔다 다시 촬영시 비율이 깨져보이는 문제때문
    interface OnCameraPauseListener {
        void onCameraPause(boolean pause);
    }

// VideoInfo 에서 VideoListFragment로 비디오 개수가 0일때 true를 보내서 데이터가 없는 뷰를 보여줌.
    interface OnCallbackEmptyVideo {
        void onEmptyVideo(boolean show);
    }

// OnCallbackEmptyVideo 인터페이스와 같은 기능이지만 Adapter에서 List 뷰로 콜백 해줄때 사용
    interface OnCallbackEmptyVideoToList {
        void onCallbackToList(boolean show);
    }


// SimpleAlert에서 확인버튼 눌렀을때 콜백
    interface OnAlertOkCallback {
        void onclickOK(DialogInterface dialog);
    }

// 카카오 세션 콜백
    interface OnSessionResult {
        void onCallback(boolean result, KakaoException exception);
    }

// 파일 저장 삭제 결과 콜백
    interface OnFileCallback {
        void onFileCallback(boolean result);
    }

// Adapter에서 삭제버튼 눌렀을때 프래그먼트로 콜백
    interface OnLoadingCallback {
        void onLoading(boolean show);
    }

    interface OnRemoveCallback {
        void onRemove(int index);
    }

}
