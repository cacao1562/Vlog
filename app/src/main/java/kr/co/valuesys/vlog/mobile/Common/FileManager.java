package kr.co.valuesys.vlog.mobile.Common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

import kr.co.valuesys.vlog.mobile.Model.VideoInfo;

public class FileManager {

    private static final String TEMP_PATH = "DCIM/" + Constants.Temp_Folder_Name + "/";
    private static final String REAL_PATH = "DCIM/" + Constants.Real_Folder_Name + "/";

    private static final File dir = Environment.getExternalStorageDirectory().getAbsoluteFile();

    /**
     * 파일 저장
     */
    public static void saveFile(Activity mActivity, String tempPath, String newFileName, CommonInterface.OnFileCallback callback) {

        boolean result = false;

        if (TextUtils.isEmpty(tempPath)) {
            callback.onFileCallback(false);
            return;
        }

        File tempFile = new File(tempPath);
        String path = dir.getPath() + "/" + REAL_PATH;
        File realfolder = new File(path);

        if (!realfolder.exists()) {
            realfolder.mkdir();
        }

        File newFile = new File(path + newFileName + ".mp4");

        if (tempFile.exists()) {

// 사용자가 입력한 이름으로 파일이름 변경
            if (tempFile.renameTo(newFile)) {

                result = true;

                if (null != mActivity) {

                    Toast.makeText(mActivity, "Video saved: " + newFile.getPath(), Toast.LENGTH_SHORT).show();
                    mActivity.getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(tempFile)));

                    if (newFile.exists()) {
                        mActivity.getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newFile)));
                    }

                }

            }else {

                Toast.makeText(mActivity, "파일 이름 변경 실패", Toast.LENGTH_SHORT).show();

            }

        }

        callback.onFileCallback(result);

    }

    /**
     * 비디오 파일 삭제
     */
    public static void deleteVideo(Activity mActivity, String filePath, CommonInterface.OnFileCallback callback) {

        if (TextUtils.isEmpty(filePath)) {
            callback.onFileCallback(false);
            return;
        }

        try {

            File file = new File(filePath);

            if (file.exists()) {

                if (file.delete()) {

                    mActivity.getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    callback.onFileCallback(true);

                } else {

                    callback.onFileCallback(false);
                }

            } else {

                callback.onFileCallback(false);
            }

        } catch (NullPointerException e) {

            callback.onFileCallback(false);
            e.printStackTrace();
        }

    }

    /**
     * 파일 이름 및 저장경로를 만듭니다.
     */
    public static String getVideoFilePath() {

        String path = dir.getPath() + "/" + TEMP_PATH;
        File dst = new File(path);
        if (!dst.exists()) dst.mkdirs();

        return path + System.currentTimeMillis() + ".mp4";
    }

    /**
     * temp 폴더 아래에 있는 파일 삭제
     */
    public static void deleteTempFiles(Activity mActivity) {

//        String path = dir.getPath() + "/DCIM/" + Constants.Temp_Folder_Name + "/";
        String path = dir.getPath() + "/" + TEMP_PATH;
        File temp = new File(path);

        if (temp.exists()) {

            File[] childs = temp.listFiles();

            if (childs != null) {

                for (File file : childs) {

                    if (file.delete()) {

                        mActivity.getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

                    }else {

                        Toast.makeText(mActivity, "temp 파일들 삭제 실패", Toast.LENGTH_SHORT).show();

                    }

                }


            }
        }

    }


}
