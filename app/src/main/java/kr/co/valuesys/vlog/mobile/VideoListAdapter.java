package kr.co.valuesys.vlog.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;

import kr.co.valuesys.vlog.mobile.Application.MobileApplication;
import kr.co.valuesys.vlog.mobile.Common.CommonInterface;
import kr.co.valuesys.vlog.mobile.Common.FileManager;
import kr.co.valuesys.vlog.mobile.Common.SimpleAlert;
import kr.co.valuesys.vlog.mobile.DialogFragment.VideoPlayDialog;
import kr.co.valuesys.vlog.mobile.Model.VideoInfo;
import kr.co.valuesys.vlog.mobile.databinding.VideoItemBinding;


public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListHolder> {

    private Activity activity;
    private Context context;
    private ArrayList<VideoInfo> mVideoInfo = new ArrayList<>();
    private CommonInterface.OnCallbackEmptyVideoToList mCallbackToList;
    private CommonInterface.OnLoadingCallback mCallbackLoading;
    private String userNamee;


    public VideoListAdapter(Activity activity, CommonInterface.OnCallbackEmptyVideoToList callbackToList, CommonInterface.OnLoadingCallback onLoadingCallback) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.mCallbackToList = callbackToList;
        this.mCallbackLoading = onLoadingCallback;
        this.userNamee = MobileApplication.getContext().getLoginkName();
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
        String date = MobileApplication.convertDateToString(videoInfo.getDate());
//        Uri uri = videoInfo.getUri();

        binding.itemTitleTextview.setText(title);
        binding.itemThumbnailImgview.setImageBitmap(img);
        binding.itemDateTextview.setText(date);
        binding.itemUserNameTextview.setText(userNamee + "  |  ");

        long time = (new Date().getTime() - videoInfo.getDate().getTime()) / (1000 * 60 * 60);
        if (time == 0) {
            binding.itemElapsedTimeTextview.setText("방금전");
        }else if (time >= 24) {
            binding.itemElapsedTimeTextview.setText((time / 24) + " 일 전");
        }else {
            binding.itemElapsedTimeTextview.setText(time + "시간 전");
        }


    }

    @Override
    public int getItemCount() {
        return mVideoInfo.size();
    }

    public void setUp(ArrayList<VideoInfo> videoInfos) {
        this.mVideoInfo = videoInfos;

        activity.runOnUiThread(() -> {
            notifyDataSetChanged();
        });

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

// 설치된 비디오 앱으로 연결
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, "video/*");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
            });


            binding.itemDeleteImgButton.setOnClickListener(v -> {

                AlertDialog alert = SimpleAlert.createAlert(activity, "삭제 하시겠습니까?", true, dialog -> {

                    dialog.dismiss();
                    mCallbackLoading.onLoading(true);
                    new removeLoading(getAdapterPosition()).execute();


//                        FileManager.deleteVideo(activity, mVideoInfo.get(getAdapterPosition()).getUri().toString(), result -> {
//
//                            if (result) {
//
////                            ProgressDialog progress = ProgressDialog.newInstance();
////                            progress.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
////                            progress.setCancelable(false);
////                            progress.show(((AppCompatActivity) activity).getSupportFragmentManager(), "tag");
//
//
//                                setUp(VideoInfo.getVideo(activity, true, show -> {
//
//                                    mCallbackToList.onCallbackToList(show);
//                                    mCallbackLoading.onLoading(false);
//
//                                }));
//
//                            }
//
//
//                        });

//
//                    dialog.dismiss();

                });

                alert.show();

            });

        }
    }


    private class removeLoading extends AsyncTask<Void, Void, Void> {

        private int position;

        public removeLoading(int index) {
            position = index;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            FileManager.deleteVideo(activity, mVideoInfo.get(position).getUri().toString(), result -> {

                if (result) {

                    setUp(VideoInfo.getVideo(activity, true, show -> {

                        mCallbackToList.onCallbackToList(show);
                        mCallbackLoading.onLoading(false);

                    }));

                }

            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
