package kr.co.valuesys.vlog.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import kr.co.valuesys.vlog.mobile.application.MobileApplication;
import kr.co.valuesys.vlog.mobile.common.CommonInterface;
import kr.co.valuesys.vlog.mobile.common.FileManager;
import kr.co.valuesys.vlog.mobile.common.SimpleAlert;
import kr.co.valuesys.vlog.mobile.dialogFragment.VideoPlayDialog;
import kr.co.valuesys.vlog.mobile.model.VideoInfo;
import kr.co.valuesys.vlog.mobile.databinding.VideoItemBinding;


public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListHolder> {

    private Activity activity;
    private Context context;
    private ArrayList<VideoInfo> mVideoInfo = new ArrayList<>();
    private CommonInterface.OnCallbackEmptyVideoToList mCallbackToList;
    private CommonInterface.OnLoadingCallback mCallbackLoading;
    private CommonInterface.OnRemoveCallback mCallbackRemove;
    private String userNamee;


    public VideoListAdapter(Activity activity,
                            CommonInterface.OnCallbackEmptyVideoToList callbackToList,
                            CommonInterface.OnLoadingCallback onLoadingCallback,
                            CommonInterface.OnRemoveCallback onRemoveCallback) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.mCallbackToList = callbackToList;
        this.mCallbackLoading = onLoadingCallback;
        this.mCallbackRemove = onRemoveCallback;
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


        // 시간을 1시간 단위로 나눠서 0 이면 1시안 이내이고
        // 24이상이면 하루가 넘어서 24이상은 다시 24로 나누어서 일자로 표
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

    public void setUpInsert(ArrayList<VideoInfo> videoInfos) {
        this.mVideoInfo = videoInfos;

        activity.runOnUiThread(() -> {
            notifyItemInserted(0);

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
//                    new RemoveLoading(getAdapterPosition()).execute();
                    mCallbackRemove.onRemove(getAdapterPosition());
                });

                alert.show();

            });

        }
    }


    private class RemoveLoading extends AsyncTask<Void, Void, Void> {

        private int position;

        public RemoveLoading(int index) {
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

                        mCallbackLoading.onLoading(false);
                        mCallbackToList.onCallbackToList(show);

                    }));

                }else {

                    mCallbackLoading.onLoading(false);
                    Toast.makeText(activity, "파일 삭제 실패", Toast.LENGTH_SHORT).show();

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
