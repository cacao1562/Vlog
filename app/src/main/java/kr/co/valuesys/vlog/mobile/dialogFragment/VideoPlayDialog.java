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

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import java.io.IOException;

import kr.co.valuesys.vlog.mobile.common.LogUtil;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.DialogVideoplayBinding;

public class VideoPlayDialog extends DialogFragment implements TextureView.SurfaceTextureListener {

    private static final String ARG_MESSAGE = "message";

    private DialogVideoplayBinding binding;

    //    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;
    private Runnable runnable;
    private Handler handler;

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
        handler = new Handler();

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

                startPlay();
                binding.videoPlayPlayButton.setVisibility(View.GONE);
            }
        });

        binding.videoPlayPlayButton.setOnClickListener(v -> {

            startPlay();
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

        binding.videoPlaySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isMoveSeek) {
                    if (mMediaPlayer != null) {
                        mMediaPlayer.seekTo(seekBar.getProgress());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.pause();
                    isMoveSeek = true;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayer != null) {
                    isMoveSeek = false;
                    mMediaPlayer.seekTo(seekBar.getProgress());
                    startPlay();
                }
            }
        });
    }


    private boolean isMoveSeek;


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
                startPlay();
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
            mMediaPlayer = new MediaPlayer();
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


            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                    changeSeekbar();
                }
            });

            binding.videoPlaySeekbar.setMax(mMediaPlayer.getDuration());
            LogUtil.d("sss", "  getDuration = " + mMediaPlayer.getDuration());

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
//        LogUtil.d("sss", "onSurfaceTextureUpdated  pos = " + mMediaPlayer.getCurrentPosition());
//        binding.videoPlaySeekbar.setProgress(mMediaPlayer.getCurrentPosition());
    }

    private void startPlay() {
        binding.videoPlayPlayButton.setVisibility(View.GONE);
        mMediaPlayer.start();
        changeSeekbar();
    }

    private void changeSeekbar() {

        if (mMediaPlayer != null) {
//            LogUtil.d("sss", " changeSeekbar = " + mMediaPlayer.getCurrentPosition() );
            binding.videoPlaySeekbar.setProgress(mMediaPlayer.getCurrentPosition());
            if (mMediaPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        changeSeekbar();
                    }
                };
                handler.postDelayed(runnable, 10);
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}
