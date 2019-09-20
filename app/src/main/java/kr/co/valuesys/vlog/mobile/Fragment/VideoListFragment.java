package kr.co.valuesys.vlog.mobile.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kr.co.valuesys.vlog.mobile.Common.Constants;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.Model.VideoInfo;
import kr.co.valuesys.vlog.mobile.VideoListAdapter;
import kr.co.valuesys.vlog.mobile.databinding.FragmentVideoListBinding;


public class VideoListFragment extends Fragment {

    private FragmentVideoListBinding binding;
    private VideoListAdapter adapter;

    public static VideoListFragment newInstance() {

        VideoListFragment fragment = new VideoListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video_list, container, false);

        binding.videoListRecyclerview.setHasFixedSize(true);
        binding.videoListRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new VideoListAdapter(getActivity());
        binding.videoListRecyclerview.setAdapter(adapter);

        adapter.setUp(getSortedVideo());

        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.setUp(getSortedVideo());
    }


// date 날짜 기준으로 비디오 리스트를 내림차순 정렬
    private ArrayList<VideoInfo> getSortedVideo() {

        ArrayList<VideoInfo> sortedInfo = getVideo();
        Collections.sort(sortedInfo, new Comparator<VideoInfo>() {
            @Override
            public int compare(VideoInfo o1, VideoInfo o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        return sortedInfo;
    }

// 전체 비디오 목록 에서 앨범 이름이 TestVideo 인 것만 리스트에 추가
    private ArrayList<VideoInfo> getVideo() {

        String[] proj = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.ALBUM,
                MediaStore.Video.Media.DATE_TAKEN
        };

//        Log.d("aaa", "path 1 = " + Environment.getExternalStorageDirectory().getAbsoluteFile().getPath() );
//        Log.d("aaa", "path 2 = " + Environment.getExternalStorageDirectory().getPath() );
//        Log.d("aaa", "path 3 = " + MediaStore.Video.Media.EXTERNAL_CONTENT_URI );

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsoluteFile().getPath() + "/DCIM/TestVideo/");
//        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/DCIM/TestVideo/");

        Cursor cursor = getActivity().getContentResolver().query(uri, proj, null, null, null);
        ArrayList<VideoInfo> videoList = new ArrayList<>();
        assert cursor != null;

        while (cursor.moveToNext()) {

            if (cursor.getString(3).equals(Constants.Video_Folder_Name)) {

                String title = cursor.getString(1);
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getActivity().getContentResolver(), id, MediaStore.Video.Thumbnails.MINI_KIND, null);

                if (bitmap == null) {
                    continue;
                }
//                Log.d("aaa", "bitmap  w = " + bitmap.getWidth() + "  h = " + bitmap.getHeight() );
                // 썸네일 크기 변경할 때.
//                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 1024, 1024);
                String data = cursor.getString(2);

                long dateTime = cursor.getLong(4);

                SimpleDateFormat format = new SimpleDateFormat ( "yyyy년 MM월dd일 HH시mm분ss초");
                String format_time = format.format ( new Date( dateTime ) );

                videoList.add( new VideoInfo(title, bitmap, Uri.parse(data), format_time) );

//                Log.d("aaa", "cursor getstring id = " + cursor.getString(0) );
//                Log.d("aaa", "cursor getstring title = " + title );
//                Log.d("aaa", "cursor getstring data = " + data );
//                Log.d("aaa", "cursor getstring album = " + cursor.getString(3) );
//                Log.d("aaa", "cursor getstring DATE_TAKEN = " + cursor.getString(4) );
//
//                Log.d("aaa", "cursor date 22 === " + format_time );

            }

        }

        if (videoList.size() == 0) {
            binding.videoListEmptyview.setVisibility(View.VISIBLE);
        }else {
            binding.videoListEmptyview.setVisibility(View.GONE);
        }
        cursor.close();
        return videoList;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
