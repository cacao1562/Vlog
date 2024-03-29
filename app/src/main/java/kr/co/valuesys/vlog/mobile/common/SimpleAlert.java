package kr.co.valuesys.vlog.mobile.common;


import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class SimpleAlert {

    public static AlertDialog createAlert(Context context, String msg, boolean showCancle, CommonInterface.OnAlertOkCallback callback) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(msg);

        if (showCancle) {
            alert.setNegativeButton("취소", null);
        }

        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (callback != null) {
                    callback.onclickOK(dialog);
                }

            }
        });

        alert.setCancelable(false);
        AlertDialog dialog = alert.create();

        return dialog;

    }
}
