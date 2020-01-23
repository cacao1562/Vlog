package kr.co.valuesys.vlog.mobile.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    private Context context;
    private Activity activity;

    // 요청할 권한을 배열로 저장해주었습니다.

    private final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private List<String> permissionList;

    // 이 부분은 권한 요청을 할 때 발생하는 창에 대한 결과값을 받기 위해 지정해주는 int 형입니다.
    // 본인에 맞게 숫자를 지정하시면 될 것 같습니다.
    private final int MULTIPLE_PERMISSIONS = 200;

    // 생성자에서 Activity와 Context를 파라미터로 받았습니다.
    public PermissionUtils(Activity _activity, Context _context) {
        this.activity = _activity;
        this.context = _context;
    }

    // 허용 받아야할 권한이 남았는지 체크
    public boolean checkPermission() {
        int result;
        permissionList = new ArrayList<>();

        // 위에서 배열로 선언한 권한 중 허용되지 않은 권한이 있는지 체크
        for (String pm : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }

        if (!permissionList.isEmpty()) {
            return false;
        }
        return true;
    }

    // 권한 허용 요청
    public void requestPermission() {
        LogUtil.d("ppp", "requestPermission  ");
        ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
    }

    // 권한 요청에 대한 결과 처리
    public void permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, CommonInterface.OnPermissionCallback callback) {

        boolean isGranted = true;
        ArrayList<String> userNotAllowdPermissions = new ArrayList<>();

        // 우선 requestCode가 아까 위에 final로 선언하였던 숫자와 맞는지, 결과값의 길이가 0보다는 큰지 먼저 체크했습니다.
        if (requestCode == MULTIPLE_PERMISSIONS && (grantResults.length > 0)) {

            for (int i = 0; i < grantResults.length; i++) {
                //grantResults 가 0이면 사용자가 허용한 것이고 / -1이면 거부한 것입니다.
                // -1이 있는지 체크하여 하나라도 -1이 나온다면 false를 리턴해주었습니다.

                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                    isGranted = false;

                    //[다시보지 않기] 거부
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                        LogUtil.d("ppp", " permission utils =  다시 거부  ");
                        userNotAllowdPermissions.add(permissions[i]);

                    }

                }
//                if (grantResults[i] == -1) {
//                    LogUtil.d("ppp", " permission utils =  grant -1 ");
//                    isGranted = false;
//                }

            }
        }

        if (isGranted) {
            if (callback != null) {
                callback.onCallback(0);
            }
        } else {
            if (userNotAllowdPermissions.size() > 0) {
                if (callback != null) {
                    callback.onCallback(2);
                }
            } else {
                if (callback != null) {
                    callback.onCallback(1);
                }
//                Toast.makeText(this, "권한 사용을 동의 해주시기 바랍니다.", Toast.LENGTH_LONG).show();
            }
//            setResult(RESULT_CANCELED);
        }
    }

}
