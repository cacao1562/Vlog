package kr.co.valuesys.vlog.mobile.Common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;

public class SimpleAlert {

    public interface AlertOkCallback {
        void onclickOK(DialogInterface dialog);
    }

    public AlertDialog createAlert(Context context, String msg, boolean showCancle, AlertOkCallback callback) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(msg);

        if (showCancle) {
            alert.setNegativeButton("취소", null);
        }
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onclickOK(dialog);
            }
        });

        alert.setCancelable(false);
        AlertDialog dialog = alert.create();

        return dialog;


    }
}
