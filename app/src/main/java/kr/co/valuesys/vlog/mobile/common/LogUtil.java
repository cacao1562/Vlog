package kr.co.valuesys.vlog.mobile.common;

import android.util.Log;

public class LogUtil {

    public static void d(String tag, String msg) {
        if (Constants.Print_Log) {
            Log.d(tag, msg);
        }
    }
}
