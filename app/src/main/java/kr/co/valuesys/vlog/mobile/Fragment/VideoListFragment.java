package kr.co.valuesys.vlog.mobile.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;

import kr.co.valuesys.vlog.mobile.Application.MobileApplication;
import kr.co.valuesys.vlog.mobile.Common.CommonInterface;
import kr.co.valuesys.vlog.mobile.Common.LogUtil;
import kr.co.valuesys.vlog.mobile.Model.VideoInfo;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.VideoListAdapter;
import kr.co.valuesys.vlog.mobile.databinding.FragmentVideoListBinding;


public class VideoListFragment extends Fragment implements CommonInterface.OnCallbackEmptyVideo, CommonInterface.OnCallbackEmptyVideoToList,
        CommonInterface.OnLoadingCallback {

    private FragmentVideoListBinding binding;
    private VideoListAdapter adapter;
    private ArrayList<VideoInfo> info;

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
        adapter = new VideoListAdapter(getActivity(), this, this);
        binding.videoListRecyclerview.setAdapter(adapter);

        adapter.setUp(VideoInfo.getVideo(getActivity(), true, this));

        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        LogUtil.d("main list fragment", " onResume");

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

    AlertDialog loading;

    @Override
    public void onLoading(boolean show) {

        if (loading == null) {
            loading = MobileApplication.showProgress(null, null, getActivity()).create();
        }

            if (show) {
                loading.show();
            }else {
                loading.dismiss();
            }

    }


// 비디오가 없다는 뷰 보여주기 또는 숨기기
    private void showEmptyView(boolean show) {

        if (show) {
            binding.videoListEmptyview.setVisibility(View.VISIBLE);
        }else {
            binding.videoListEmptyview.setVisibility(View.GONE);
        }
    }

    public void scrolltoVideo() {

        if (MobileApplication.getContext().getmSelectDay() != null) {

            Calendar cal = Calendar.getInstance();

            for (int i = 0; i < info.size(); i++) {

                cal.setTime(info.get(i).getDate());

                if (CalendarDay.from(cal).equals(MobileApplication.getContext().getmSelectDay()) ) {

                    LogUtil.d("main", " selected = " + MobileApplication.getContext().getmSelectDay() + "  position = " + i);
                    binding.videoListRecyclerview.smoothScrollToPosition(i);
                    MobileApplication.getContext().setmSelectDay(null);
                }

            }

        }

    }


    private class updateVideo extends AsyncTask<Void, Void, Void> {

        AlertDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = MobileApplication.showProgress(null, null, getActivity()).create();
            loading.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            info = VideoInfo.getVideo(getActivity(), true,  null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (info != null) {
                adapter.setUp(info);
                loading.dismiss();
            }
        }
    }

    public void refreshVideo() {

        new updateVideo().execute();

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
