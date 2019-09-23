package kr.co.valuesys.vlog.mobile.Dialog;

import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.DialogVideoplayBinding;

public class VideoPlayDialog extends DialogFragment implements SurfaceHolder.Callback{

    private static final String ARG_MESSAGE = "message";

    private DialogVideoplayBinding binding;

    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;

    public VideoPlayDialog() { }

    public static VideoPlayDialog newInstance(String uri) {

        VideoPlayDialog dialog = new VideoPlayDialog();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, uri);
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_videoplay, container, false);
        mSurfaceHolder = binding.videoPlaySurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.videoPlaySurfaceView.setOnClickListener(v -> {

            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                binding.videoPlayPlayButton.setVisibility(View.VISIBLE);
            }else {
                mMediaPlayer.start();
                binding.videoPlayPlayButton.setVisibility(View.GONE);
            }
        });

        binding.videoPlayPlayButton.setOnClickListener(v -> {

        });

        binding.videoPlayBackButton.setOnClickListener(v -> {
            dismiss();
        });
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        } else {
            mMediaPlayer.reset();
        }

        try {

//            String path = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
            String path = getArguments().getString(ARG_MESSAGE);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setLooping(true);
            //mediaPlayer.setVolume(0, 0); //볼륨 제거
            mMediaPlayer.setDisplay(mSurfaceHolder); // 화면 호출
            mMediaPlayer.prepare(); // 비디오 load 준비

            //mediaPlayer.setOnCompletionListener(completionListener); // 비디오 재생 완료 리스너

            mMediaPlayer.start();

        } catch (Exception e) {
            Log.e("MyTag","surface view error : " + e.getMessage());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        mSurfaceHolder.removeCallback(this);
        mSurfaceHolder = null;
    }
}
