package kr.co.valuesys.vlog.mobile.common;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    private static final String TEMP_PATH = "/DCIM/" + Constants.Temp_Folder_Name + "/";
    private static final String REAL_PATH = "/DCIM/" + Constants.Real_Folder_Name + "/";

    private static final File dir = Environment.getExternalStorageDirectory().getAbsoluteFile();
//    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "CameraViewFreeDrawing";


    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };


    /** 파일 읽기 쓰기 권한 체크 */
    public static boolean checkStoragePermission(Context context) {

        if (context == null) {
            return false;
        }

        boolean grant = true;
        int result;
        for (String pm : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                grant = false;
            }
        }
        return grant;
    }
    /**
     * 파일 저장
     */
    public static void saveFile(Activity mActivity, String tempPath, String newFileName, CommonInterface.OnFileCallback callback) {

        boolean result = false;

        if (mActivity == null || TextUtils.isEmpty(tempPath) || TextUtils.isEmpty(newFileName)) {
            callback.onFileCallback(false);
            return;
        }

        File tempFile = new File(tempPath);
        String path = dir.getPath() + REAL_PATH;
        File realfolder = new File(path);

        if (realfolder.exists() == false) {
            realfolder.mkdir();
        }

        File newFile = new File(path + newFileName + ".mp4");

//        saveVideo(mActivity, path);

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

        if (mActivity == null || TextUtils.isEmpty(filePath)) {
            callback.onFileCallback(false);
            return;
        }

        try {

            File file = new File(filePath);

            if (file.exists()) {

                if (file.delete()) {

//                    mActivity.getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    MediaScannerConnection.scanFile(mActivity.getApplicationContext(),
                            new String[]{Uri.fromFile(file).toString()}, null,
                            (fPath, uri) -> {
                                LogUtil.d("xxx", "Scanned " + fPath + ":");
                                LogUtil.d("xxx", "-> uri=" + uri);
                            });
                    callback.onFileCallback(true);

                } else {

                    callback.onFileCallback(false);
                }

            } else {

                callback.onFileCallback(false);
            }

        } catch (Exception e) {

            callback.onFileCallback(false);
            e.printStackTrace();
        }

    }


    /**
     * 비디오 파일 삭제 uri
     */
    public static void deleteVideo(Activity mActivity, Uri uri, CommonInterface.OnFileCallback callback) {

        if (uri == null || mActivity == null) {
            callback.onFileCallback(false);
            return;
        }

        try {

           int result = mActivity.getContentResolver().delete(uri, null, null);
           LogUtil.d("ggg", " remove = " + result);
           if (result > 0) {
               callback.onFileCallback(true);
           }

        }catch (Exception e) {

            e.printStackTrace();
            callback.onFileCallback(false);
        }

//        Cursor c = mActivity.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, "_data='" + filePath + "'", null, null);
//        c.moveToNext();
//        int id = c.getInt(0);
//        Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
//
//        int idColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
//        long idd = c.getLong(idColumn);
//        Uri uri2 = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(idd));


    }

    /**
     * 파일 이름 및 저장경로를 만듭니다.
     */
    public static String getVideoFilePath() {

        String path = dir.getPath() + TEMP_PATH;
        File dst = new File(path);
        if (!dst.exists()) dst.mkdirs();

        return path + System.currentTimeMillis() + ".mp4";
    }

    /**
     * temp 폴더 아래에 있는 파일 삭제
     */
    public static void deleteTempFiles(Activity mActivity) {

//        String path = dir.getPath() + "/DCIM/" + Constants.Temp_Folder_Name + "/";
        String path = dir.getPath() + TEMP_PATH;
        File temp = new File(path);

        if (temp.exists()) {

            File[] childs = temp.listFiles();

            if (childs != null) {

                for (File file : childs) {

                    if (file.delete()) {
                        LogUtil.d("fff", " file delete ok ");
                        if (mActivity != null) {
                            LogUtil.d("fff", " isfinish = " + mActivity.isFinishing());
                            LogUtil.d("fff", " isDestroyed = " + mActivity.isDestroyed());
                            mActivity.getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                        }

                    }else {
                         LogUtil.d("fff", " file delete fail ");
//                        Toast.makeText(mActivity, "temp 파일들 삭제 실패", Toast.LENGTH_SHORT).show();

                    }

                }


            }
        }

    }



    public static void saveVideo(Activity activity, String filepath) {
        String videoFileName = "video_" + System.currentTimeMillis() + ".mp4";

        ContentValues valuesvideos;
        valuesvideos = new ContentValues();
//        valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
        valuesvideos.put(MediaStore.Video.Media.TITLE, videoFileName);
        valuesvideos.put(MediaStore.Video.Media.DISPLAY_NAME, videoFileName);
        valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        valuesvideos.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        valuesvideos.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
        valuesvideos.put(MediaStore.Video.Media.DATA, filepath);
//        valuesvideos.put(MediaStore.Video.Media.IS_PENDING, 1);
        ContentResolver resolver = activity.getContentResolver();
//        Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri uriSavedVideo = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, valuesvideos);


//        ParcelFileDescriptor pfd;
//
//        try {
//            pfd = mContext.getContentResolver().openFileDescriptor(uriSavedVideo, "w");
//
//            FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());
//
//// Get the already saved video as fileinputstream from here
//            File storageDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "Folder");
//            File imageFile = new File(storageDir, "Myvideo");
//
//            FileInputStream in = new FileInputStream(imageFile);
//
//
//            byte[] buf = new byte[8192];
//            int len;
//            while ((len = in.read(buf)) > 0) {
//
//                out.write(buf, 0, len);
//            }
//
//
//            out.close();
//            in.close();
//            pfd.close();
//
//
//
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//
//
//        valuesvideos.clear();
//        valuesvideos.put(MediaStore.Video.Media.IS_PENDING, 0);
//        mContext.getContentResolver().update(uriSavedVideo, valuesvideos, null, null);
    }
}
