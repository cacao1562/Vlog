package kr.co.valuesys.vlog.mobile.Model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kr.co.valuesys.vlog.mobile.Common.CommonInterface;
import kr.co.valuesys.vlog.mobile.Common.Constants;
import kr.co.valuesys.vlog.mobile.Common.LogUtil;

public class VideoInfo {


    private String title;
    private Bitmap img;
    private Uri uri;
    private Date date;

    public VideoInfo(String title, Bitmap img, Uri uri, Date date) {
        this.title = title;
        this.img = img;
        this.uri = uri;
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


// 전체 비디오 목록 에서 앨범 이름이 TestVideo 인 것만 리스트에 추가
    public static ArrayList<VideoInfo> getVideo(Context context, CommonInterface.OnCallbackEmptyVideo callback) {

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

        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);

        ArrayList<VideoInfo> videoList = new ArrayList<>();

        assert cursor != null;

        while (cursor.moveToNext()) {

            if (cursor.getString(3) != null) {

                if (cursor.getString(3).equals(Constants.Real_Folder_Name)) {

                    String title = cursor.getString(1);
                    title = title.replace(".mp4", "");
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), id, MediaStore.Video.Thumbnails.MINI_KIND, null);

                    if (bitmap == null) {
                        continue;
                    }
                    // bitmap  w = 242  h = 512 = MINI_KIND

                    String data = cursor.getString(2);

                    Bitmap thumbnail = null;
                    // 썸네일 크기 변경할 때.
                    try {
                        // 썸네일 추출후 리사이즈해서 다시 비트맵 생성
                        Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                        thumbnail = ThumbnailUtils.extractThumbnail(bitmap2, 1024, 512);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (thumbnail == null) {
                        continue;
                    }
//                    LogUtil.d("aaa", "resize bitmap  w = " + thumbnail.getWidth() + "  h = " + thumbnail.getHeight() );

                    long dateTime = cursor.getLong(4);

                    SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월dd일 HH시mm분ss초");
                    String format_time = format.format(new Date(dateTime));

                    videoList.add(new VideoInfo(title, thumbnail, Uri.parse(data), new Date(dateTime)));

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

        if (videoList.size() == 0) {
//            binding.videoListEmptyview.setVisibility(View.VISIBLE);
            if (callback != null) {
                callback.onEmptyVideo(true);
            }

        } else {
//            binding.videoListEmptyview.setVisibility(View.GONE);
            if (callback != null) {
                callback.onEmptyVideo(false);
            }

        }
        cursor.close();

// date 날짜 기준으로 비디오 리스트를 내림차순 정렬
        Collections.sort(videoList, new Comparator<VideoInfo>() {
            @Override
            public int compare(VideoInfo o1, VideoInfo o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        return videoList;
    }
}
