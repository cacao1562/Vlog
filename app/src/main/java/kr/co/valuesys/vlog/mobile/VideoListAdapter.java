package kr.co.valuesys.vlog.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

import kr.co.valuesys.vlog.mobile.Common.CommonInterface;
import kr.co.valuesys.vlog.mobile.Common.LogUtil;
import kr.co.valuesys.vlog.mobile.Common.SimpleAlert;
import kr.co.valuesys.vlog.mobile.Dialog.VideoPlayDialog;
import kr.co.valuesys.vlog.mobile.Model.VideoInfo;
import kr.co.valuesys.vlog.mobile.databinding.VideoItemBinding;


public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListHolder> {

    private Activity activity;
    private Context context;
    private ArrayList<VideoInfo> mVideoInfo = new ArrayList<>();
    private CommonInterface.OnCallbackEmptyVideoToList mCallbackToList;



    public VideoListAdapter(Activity activity, CommonInterface.OnCallbackEmptyVideoToList callbackToList) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.mCallbackToList = callbackToList;
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
        VideoInfo videoInfo = mVideoInfo.get(position);
        String title = videoInfo.getTitle();
        Bitmap img = videoInfo.getImg();
        String date = videoInfo.getDate();
        Uri uri = videoInfo.getUri();

        binding.itemTitleTextview.setText(title);
        binding.itemThumbnailImgview.setImageBitmap(img);
        binding.itemDateTextview.setText(date);


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

            binding.itemThumbnailImgview.setOnClickListener(view -> {

                VideoPlayDialog dialog = VideoPlayDialog.newInstance(mVideoInfo.get(getAdapterPosition()).getUri().toString());
                dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
                dialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), "dialog");

//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, "video/*");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
            });

            binding.itemDeleteImgButton.setOnClickListener(v -> {

                AlertDialog alert = new SimpleAlert().createAlert(activity, "삭제 하시겠습니까?", true, dialog -> {

                    File file = new File(mVideoInfo.get(getAdapterPosition()).getUri().toString());

                    if (file.exists()) {

                        if (file.delete()) {

                            activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

                            setUp(VideoInfo.getVideo(activity, new CommonInterface.OnCallbackEmptyVideo() {
                                @Override
                                public void onEmptyVideo(boolean show) {
                                    mCallbackToList.onCallbackToList(show);
                                }
                            }));
//                        mVideoInfo.remove(position);
//                        notifyDataSetChanged();

//                        notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });

                alert.show();

            });

        }
    }

}
