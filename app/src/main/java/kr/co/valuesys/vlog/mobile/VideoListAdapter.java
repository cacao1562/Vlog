package kr.co.valuesys.vlog.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import kr.co.valuesys.vlog.mobile.Model.VideoInfo;
import kr.co.valuesys.vlog.mobile.databinding.VideoItemBinding;


public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListHolder> {

    private Activity activity;
    private Context context;
    private ArrayList<VideoInfo> mVideoInfo = new ArrayList<>();

    public VideoListAdapter(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    @NonNull
    @Override
    public VideoListAdapter.VideoListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        VideoItemBinding binding = VideoItemBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        return new VideoListHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListAdapter.VideoListHolder videoListHolder, int position) {

        VideoItemBinding binding = videoListHolder.binding;
        VideoInfo videoInfo  = mVideoInfo.get(position);
        String title = videoInfo.getTitle();
        Bitmap img = videoInfo.getImg();
        String date = videoInfo.getDate();
        Uri uri = videoInfo.getUri();

        binding.itemTitleTextview.setText(title);
        binding.itemThumbnailImgview.setImageBitmap(img);
        binding.itemDateTextview.setText(date);
        //다이얼로그로 동영상의 Uri를 보내며 다이얼로그를 띄우는코드.
        binding.itemThumbnailImgview.setOnClickListener(view -> {
            Toast.makeText(context, "show video " , Toast.LENGTH_SHORT).show();
//            ShowVideoDialog dialog = new ShowVideoDialog(activity, uri, activity);
//            dialog.show();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mVideoInfo.size();
    }

    public void setUp(ArrayList<VideoInfo> videoInfos) {
        this.mVideoInfo = videoInfos;
        notifyDataSetChanged();
    }

    public class VideoListHolder extends RecyclerView.ViewHolder {

        private VideoItemBinding binding;

        public VideoListHolder(@NonNull VideoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
