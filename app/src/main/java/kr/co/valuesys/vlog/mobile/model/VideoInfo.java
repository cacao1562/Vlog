package kr.co.valuesys.vlog.mobile.model;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kr.co.valuesys.vlog.mobile.common.CommonInterface;
import kr.co.valuesys.vlog.mobile.common.Constants;
import kr.co.valuesys.vlog.mobile.common.LogUtil;

public class VideoInfo {


    private String title;
    private Bitmap img;
    private Uri uri;
    private Date date;
    private String data;

    public VideoInfo(String title, Bitmap img, String data, Uri uri, Date date) {
        this.title = title;
        this.img = img;
        this.uri = uri;
        this.data = data;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImg() {
        return img;
    }

    public Uri getUri() {
        return uri;
    }

    public Date getDate() {
        return date;
    }

    public String getData() { return data; }

    public void setImg(Bitmap img) { this.img = img; }

    // 전체 비디오 목록 에서 앨범 이름이 TestVideo 인 것만 리스트에 추가
    public static List<VideoInfo> getVideo(Context context, boolean getThumbnail, CommonInterface.OnCallbackEmptyVideo callback) {

        String[] proj = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.ALBUM,
                MediaStore.Video.Media.DATE_TAKEN
        };

//        LogUtil.d("aaa", "path 1 = " + Environment.getExternalStorageDirectory().getAbsoluteFile().getPath() );
//        LogUtil.d("aaa", "path 2 = " + Environment.getExternalStorageDirectory().getPath() );
//        LogUtil.d("aaa", "path 3 = " + MediaStore.Video.Media.EXTERNAL_CONTENT_URI );

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath() + "/DCIM/TestVideo/");
//        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/DCIM/TestVideo/");

        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, MediaStore.Video.VideoColumns.DATE_TAKEN + " DESC");



//        assert cursor != null;
        /** cursor가 null 이거나 미디어가 없을때 */
        if (cursor == null) {
            return null;
        }

        List<VideoInfo> videoList = new ArrayList<>();

        while (cursor.moveToNext()) {

            if (cursor.getString(3) != null) {

                /** 폴더 이름이 valuesys 인 것만 가져옴 */
                if (cursor.getString(3).equals(Constants.Real_Folder_Name)) {

                    String title = cursor.getString(1);
                    title = title.replace(".mp4", "");
                    String data = cursor.getString(2);

//                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
//                    Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), id, MediaStore.Video.Thumbnails.MINI_KIND, null);
//
//                    if (bitmap == null) {
//                        continue;
//                    }
                    // bitmap  w = 242  h = 512 = MINI_KIND



                    Bitmap thumbnail = null;

                    if (getThumbnail) {

                        // 썸네일 크기 변경할 때.
                        try {
                            // 썸네일 추출후 리사이즈해서 다시 비트맵 생성
                            Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
//                            thumbnail = ThumbnailUtils.extractThumbnail(bitmap2, 1024, 512);
                            thumbnail = ThumbnailUtils.extractThumbnail(bitmap2, 960, 480);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (thumbnail == null) {
                            continue;
                        }

                    }

//                    LogUtil.d("aaa", "resize bitmap  w = " + thumbnail.getWidth() + "  h = " + thumbnail.getHeight() );

                    long dateTime = cursor.getLong(4);

                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                    long id = cursor.getLong(idColumn);
                    Uri uri2 = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
//                    Uri uri3 = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                    LogUtil.d("uuu", " uri = " + Uri.parse(data));
                    LogUtil.d("uuu", " uri2 = " + uri2);


//                    videoList.add(new VideoInfo(title, thumbnail, data, Uri.parse(data), new Date(dateTime)));
                    videoList.add(new VideoInfo(title, thumbnail, data, uri2, new Date(dateTime)));

//                LogUtil.d("aaa", "cursor getstring id = " + cursor.getString(0) );
//                LogUtil.d("aaa", "cursor getstring title = " + title );
//                LogUtil.d("aaa", "cursor getstring data = " + data );
//                LogUtil.d("aaa", "cursor getstring album = " + cursor.getString(3) );
//                LogUtil.d("aaa", "cursor getstring DATE_TAKEN = " + cursor.getString(4) );
//
//                LogUtil.d("aaa", "cursor date 22 === " + format_time );

                }

            }

        }
        cursor.close();

        if (callback != null) {

            if (videoList.size() == 0) {

                callback.onEmptyVideo(true);

            } else {

                callback.onEmptyVideo(false);

            }
        }



// date 날짜 기준으로 비디오 리스트를 내림차순 정렬
//        Collections.sort(videoList, new Comparator<VideoInfo>() {
//            @Override
//            public int compare(VideoInfo o1, VideoInfo o2) {
//                return o2.getDate().compareTo(o1.getDate());
//            }
//        });
        LogUtil.d("xxx", " return size = " + videoList.size() );
        return videoList;
    }
}
