package kr.co.valuesys.vlog.mobile.common;

import android.content.DialogInterface;

import com.kakao.util.exception.KakaoException;

public interface CommonInterface {

    /**
     * 1. 캘린더에서 날짜 클릭했을때 mainActivity로 콜백 할때 사용
     * 2. 비디오 제목 입력하는 dialog에서 mainActivity로 콜백 할때 사용
     */
    interface OnCallbackToMain {
        void oncallbackMain(int id);
    }

    /** VideoInfo 에서 VideoListFragment로 비디오 개수가 0일때 true를 보내서 데이터가 없는 뷰를 보여줌. */
    interface OnCallbackEmptyVideo {
        void onEmptyVideo(boolean show);
    }

    /** SimpleAlert에서 확인버튼 눌렀을때 콜백 */
    interface OnAlertOkCallback {
        void onclickOK(DialogInterface dialog);
    }

    /** 카카오 세션 콜백 */
    interface OnSessionResult {
        void onCallback(boolean result, KakaoException exception);
    }

    /** 카카오, 페이스북 프로필 정보 가져오는 결과 콜백 */
    interface OnRequestGetLoginInfo {
        void onCallback(boolean result, String msg);
    }

    /** 파일 저장 삭제 결과 콜백 */
    interface OnFileCallback {
        void onFileCallback(boolean result);
    }

    /** 비디오 삭제 버튼 콜백 ( 리스트 index 넘겨줌 ) */
    interface OnRemoveCallback {
        void onRemove(int index);
    }

    /** 앱 정보 화면에 로그아웃 cell 클릭 콜백 */
    interface OnSelectedCallback {
        void onSelected(int positon);
    }

    /** 퍼미션 허용, 거부 콜백 */
    interface OnPermissionCallback {
        void onCallback(int result);
    }

}
