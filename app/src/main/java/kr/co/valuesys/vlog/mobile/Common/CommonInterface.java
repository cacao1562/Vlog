package kr.co.valuesys.vlog.mobile.Common;

import android.content.DialogInterface;

import com.kakao.util.exception.KakaoException;

public interface CommonInterface {

// BlankActivity에서 백버튼 눌렀을때 fragment로 callback
    interface OnBackPressedListener {
        void onBackPressed();
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

// InputFileNameDialog에서 CameraFrament로 저장버튼 눌렀을때 입력한 텍스트(파일이름) 넘겨줌
    interface OnInputDialogListener {
        void onClickSave(String fileName);
    }

// SimpleAlert에서 확인버튼 눌렀을때 콜백
    interface OnAlertOkCallback {
        void onclickOK(DialogInterface dialog);
    }

// 카카오 세션 콜백
    interface OnSessionResult {
        void onCallback(boolean result, KakaoException exception);
    }

}
