package kr.co.valuesys.vlog.mobile.dialogFragment;

import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import kr.co.valuesys.vlog.mobile.common.LogUtil;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.DialogVideoplayBinding;

public class VideoPlayDialog extends DialogFragment implements TextureView.SurfaceTextureListener {

    private static final String ARG_MESSAGE = "message";

    private DialogVideoplayBinding binding;

    //    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;

    public VideoPlayDialog() { }

    public static VideoPlayDialog newInstance(String uri) {

        VideoPlayDialog dialog = new VideoPlayDialog();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, uri);
        dialog.setArguments(args);
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_videoplay, container, false);
//        mSurfaceHolder = binding.videoPlaySurfaceView.getHolder();
//        mSurfaceHolder.addCallback(this);
        binding.videoPlaySurfaceView.setSurfaceTextureListener(this);

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 삳태바, 네이게이션바 투명
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getDialog().getWindow().setStatusBarColor(Color.TRANSPARENT);
        getDialog().getWindow().setNavigationBarColor(Color.TRANSPARENT);

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

            mMediaPlayer.start();
            binding.videoPlayPlayButton.setVisibility(View.GONE);

        });

        binding.videoPlayBackButton.setOnClickListener(v -> {
            dismiss();
        });

        binding.videoPlayHeartCheckBox.setOnClickListener(v -> {

            if (binding.videoPlayHeartCheckBox.isChecked()) {

            }else {

            }

        });
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d("ddd", "onPause");
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d("ddd", "onResume");
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

//        mSurfaceHolder.removeCallback(this);
//        mSurfaceHolder = null;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        LogUtil.d("ddd", "onSurfaceTextureAvailable");
        Surface s = new Surface(surface);

        try {
            mMediaPlayer= new MediaPlayer();
            String path = getArguments().getString(ARG_MESSAGE);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setLooping(true);

            mMediaPlayer.setSurface(s);
            mMediaPlayer.prepare();
//            mMediaPlayer.setOnBufferingUpdateListener(this);
//            mMediaPlayer.setOnCompletionListener(this);
//            mMediaPlayer.setOnPreparedListener(this);
//            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.start();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


}
