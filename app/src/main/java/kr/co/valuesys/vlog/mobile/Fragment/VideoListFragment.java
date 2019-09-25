package kr.co.valuesys.vlog.mobile.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import kr.co.valuesys.vlog.mobile.Common.LogUtil;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.Model.VideoInfo;
import kr.co.valuesys.vlog.mobile.VideoListAdapter;
import kr.co.valuesys.vlog.mobile.databinding.FragmentVideoListBinding;


public class VideoListFragment extends Fragment implements VideoInfo.CallbackEmpty, VideoListAdapter.CallbackToList {

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
        binding.videoListRecyclerview.addItemDecoration(new RecyclerDecoration(20));
        adapter = new VideoListAdapter(getActivity(), this);
        binding.videoListRecyclerview.setAdapter(adapter);

        adapter.setUp(VideoInfo.getVideo(getActivity(), this));

        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.setUp(VideoInfo.getVideo(getActivity(), this));
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


// VideoInfo에서 넘겨주는 callback
// 비디오 개수가 0개면 true
    @Override
    public void onEmptyVideo(boolean show) {
        showEmptyView(show);
    }


// VideoListAdapter 에서 비디오 삭제했을때 callback
// 비디오 개수가 0개면 true
    @Override
    public void onCallbackToList(boolean show) {
        showEmptyView(show);
    }


// 비디오가 없다는 뷰 보여주기 또는 숨기기
    private void showEmptyView(boolean show) {

        if (show) {
            binding.videoListEmptyview.setVisibility(View.VISIBLE);
        }else {
            binding.videoListEmptyview.setVisibility(View.GONE);
        }
    }


    // recyclerview cell 아이템 간격
    public class RecyclerDecoration extends RecyclerView.ItemDecoration {

        private final int divHeight;

        public RecyclerDecoration(int divHeight) {
            this.divHeight = divHeight;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = divHeight;
            }

        }
    }


}
