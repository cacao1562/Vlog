package kr.co.valuesys.vlog.mobile.dialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.valuesys.vlog.mobile.application.MobileApplication;
import kr.co.valuesys.vlog.mobile.common.Constants;
import kr.co.valuesys.vlog.mobile.common.FileManager;
import kr.co.valuesys.vlog.mobile.common.LogUtil;
import kr.co.valuesys.vlog.mobile.common.SimpleAlert;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.FragmentCamera2Binding;

public class Camera2Fragment extends DialogFragment {

    private FragmentCamera2Binding binding;
    private CountDownTimer mCountDownTimer;
    private final int Max_duration = 15000;

    private Timer timer;
    private int time = 0;

    private String mTempVideoPath;

    public static Camera2Fragment newInstance() {
        return new Camera2Fragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();

                // 녹화를 다하고 저장 할지 삭제할지 선택하는 대기 상태 일때
//                if (!mIsRecordingVideo && mNextVideoAbsolutePath != null) {
//
//                    AlertDialog alert = SimpleAlert.createAlert(getActivity(), getString(R.string.back_alert_msg), true, dialog -> {
//
//                        deleteVideo();
//                        dialog.dismiss();
//                        dismiss();
//
//                    });
//
//                    alert.show();
//
//                    // 녹화 하기 전 상태
//                }else if (!mIsRecordingVideo && mNextVideoAbsolutePath == null) {
//
//                    dismiss();
//
//                    // 녹화 중인 상태
//                }else if (mIsRecordingVideo && mNextVideoAbsolutePath != null) {
//
//                }
            }
        };
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
//        display.getSize(size);
        display.getRealSize(size);
//        mScreen_width = size.x;
//        mScreen_height = size.y;
//        LogUtil.d(TAG, "screen w = " + mScreen_width + " h = " + mScreen_height );

        long maxTime = 15000;
        // 0.01초 간격으로 프로그레스 값 업떼이트 ( 1000 = 1초 )
        mCountDownTimer = new CountDownTimer(maxTime, 10) {

            @Override
            public void onTick(long millisUntilFinished) {

                int value = (int) ( maxTime - millisUntilFinished);
                binding.progressBar.setProgress( value );
            }

            @Override
            public void onFinish() {
                binding.progressBar.setProgress( Max_duration );
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }

        mCountDownTimer.cancel();
        mCountDownTimer = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera2, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        LogUtil.d("vvv", "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        LogUtil.d("vvv", "onResume");
        super.onResume();
    }

    private CameraListener mCameraListener = new CameraListener() {
        @Override
        public void onCameraOpened(@NonNull CameraOptions options) {
            super.onCameraOpened(options);
            LogUtil.d("vvv", "onCameraOpened");

        }

        @Override
        public void onCameraClosed() {
            super.onCameraClosed();
            LogUtil.d("vvv", "onCameraClosed");
        }

        @Override
        public void onCameraError(@NonNull CameraException exception) {
            super.onCameraError(exception);
            LogUtil.d("vvv", "onCameraError");
        }

        @Override
        public void onPictureTaken(@NonNull PictureResult result) {
            super.onPictureTaken(result);
            LogUtil.d("vvv", "onPictureTaken");
        }

        @Override
        public void onVideoTaken(@NonNull VideoResult result) {
            super.onVideoTaken(result);
            LogUtil.d("vvv", "onVideoTaken");
            LogUtil.d("vvv", "getSize = " + result.getSize());
            LogUtil.d("vvv", "getMaxSize = " + result.getMaxSize());
            LogUtil.d("vvv", "getMaxDuration = " + result.getMaxDuration());

            binding.camera.close();
            // refresh gallery
            MediaScannerConnection.scanFile(getContext(),
                    new String[]{result.getFile().toString()}, null,
                    (filePath, uri) -> {
                        Log.i("ExternalStorage", "Scanned " + filePath + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    });
        }

        @Override
        public void onOrientationChanged(int orientation) {
            super.onOrientationChanged(orientation);
            LogUtil.d("vvv", "onOrientationChanged");
        }

        @Override
        public void onAutoFocusStart(@NonNull PointF point) {
            super.onAutoFocusStart(point);
            LogUtil.d("vvv", "onAutoFocusStart");
        }

        @Override
        public void onAutoFocusEnd(boolean successful, @NonNull PointF point) {
            super.onAutoFocusEnd(successful, point);
            LogUtil.d("vvv", "onAutoFocusEnd");
        }

        @Override
        public void onZoomChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onZoomChanged(newValue, bounds, fingers);
            LogUtil.d("vvv", "onZoomChanged");
        }

        @Override
        public void onExposureCorrectionChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers);
            LogUtil.d("vvv", "onExposureCorrectionChanged");
        }

        @Override
        public void onVideoRecordingStart() {
            super.onVideoRecordingStart();
            LogUtil.d("vvv", "onVideoRecordingStart");
        }

        @Override
        public void onVideoRecordingEnd() {
            super.onVideoRecordingEnd();
            LogUtil.d("vvv", "onVideoRecordingEnd");

            stopTimer();
            mCountDownTimer.cancel();

            binding.pictureBtn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);

            binding.removeVideoBtn.setVisibility(View.VISIBLE);
            binding.saveVideoBtn.setVisibility(View.VISIBLE);
            binding.closeImgbutton.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

// 삳태바 투명
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getDialog().getWindow().setStatusBarColor(Color.TRANSPARENT);
        getDialog().getWindow().setNavigationBarColor(Color.TRANSPARENT);

        binding.camera.setLifecycleOwner(this);
//        camera.setVideoMaxDuration(120 * 1000); // max 2mins
        binding.camera.setVideoMaxDuration(15 * 1000); // max 15 secs
//        camera.addCameraListener(new CameraListener() {
//
//            @Override
//            public void onVideoTaken(@NonNull VideoResult result) {
//                super.onVideoTaken(result);
//                VideoPreviewActivity.setVideoResult(result);
//                Intent intent = new Intent(MainActivity.this, VideoPreviewActivity.class);
//                startActivity(intent);
//
//                // refresh gallery
//                MediaScannerConnection.scanFile(MainActivity.this,
//                        new String[]{result.getFile().toString()}, null,
//                        (filePath, uri) -> {
//                            Log.i("ExternalStorage", "Scanned " + filePath + ":");
//                            Log.i("ExternalStorage", "-> uri=" + uri);
//                        });
//            }
//        });
        binding.camera.addCameraListener(mCameraListener);

        binding.pictureBtn.setOnClickListener(v -> {

            if (binding.camera.isTakingVideo()) {
                binding.camera.stopVideo();
//                fabVideo.setImageResource(R.drawable.ic_videocam_black_24dp);
                return;
            }
            binding.pictureBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_start_on));
            binding.switchImgBtn.setVisibility(View.INVISIBLE);
            binding.closeImgbutton.setVisibility(View.INVISIBLE);
//            mIsRecordingVideo = true;
            mTempVideoPath = FileManager.getVideoFilePath();
            startTimer();
            mCountDownTimer.start();

            File file = new File(mTempVideoPath);
//            if (!file.exists()) {
//                file.mkdir();
//            }
            binding.camera.takeVideoSnapshot(file);

//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.US);
//            String currentTimeStamp = dateFormat.format(new Date());
//            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "CameraViewFreeDrawing";
//            File outputDir= new File(path);
//            outputDir.mkdirs();
//            File saveTo = new File(path + File.separator + currentTimeStamp + ".mp4");
//            camera.takeVideoSnapshot(saveTo);

//            fabVideo.setImageResource(R.drawable.ic_stop_black_24dp);
        });

        binding.switchImgBtn.setOnClickListener(v -> {

            if (binding.camera.isTakingPicture() || binding.camera.isTakingVideo()) {
                return;
            }
            switch (binding.camera.toggleFacing()) {
                case BACK:
//                    fabFront.setImageResource(R.drawable.ic_camera_front_black_24dp);
                    break;

                case FRONT:
//                    fabFront.setImageResource(R.drawable.ic_camera_rear_black_24dp);
                    break;
            }
        });

        binding.closeImgbutton.setOnClickListener(v -> {

            if (TextUtils.isEmpty(mTempVideoPath) == false) {

                AlertDialog alert = SimpleAlert.createAlert(getActivity(), getString(R.string.back_alert_msg), true, dialog -> {

                    deleteVideo();
                    dialog.dismiss();
                    dismiss();

                });

                alert.show();

            }else {

                dismiss();
            }

        });

        binding.removeVideoBtn.setOnClickListener(v -> {

            AlertDialog alert = SimpleAlert.createAlert(getActivity(), getString(R.string.remove_video_alert_msg), true, dialog -> {

                resetUI();
                deleteVideo();
//                startPreview();
                if (binding.camera.isOpened() == false) {
                    binding.camera.open();
                }
                dialog.dismiss();

            });

            alert.show();

        });

        binding.saveVideoBtn.setOnClickListener(v -> {

            InputFileNameDialog inputFileNameDialog = InputFileNameDialog.newInstance();
            inputFileNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
            Bundle args = new Bundle();
            args.putString(Constants.ARG_Key, mTempVideoPath);
            inputFileNameDialog.setArguments(args);
            inputFileNameDialog.show(this.getChildFragmentManager(), "dialog");
        });


        binding.progressBar.setMax(Max_duration);

        long maxTime = 15000;
        // 0.01초 간격으로 프로그레스 값 업떼이트 ( 1000 = 1초 )
        mCountDownTimer = new CountDownTimer(maxTime, 10) {

            @Override
            public void onTick(long millisUntilFinished) {

                int value = (int) ( maxTime - millisUntilFinished);
                binding.progressBar.setProgress( value );
            }

            @Override
            public void onFinish() {
                binding.progressBar.setProgress( Max_duration );
            }
        };

        binding.testButton.setOnClickListener(v -> {

            if (binding.kakaoImgview.getVisibility() == View.VISIBLE) {
                binding.kakaoImgview.setVisibility(View.INVISIBLE);
            }else {
                binding.kakaoImgview.setVisibility(View.VISIBLE);
            }

            binding.touchesView.setSomeView(binding.kakaoImgview);
        });

        binding.test2Button.setOnClickListener(v -> {

            if (binding.lottieCat.getVisibility() == View.VISIBLE) {
                binding.lottieCat.setVisibility(View.INVISIBLE);
            }else {
                binding.lottieCat.setVisibility(View.VISIBLE);
            }

            binding.touchesView.setSomeView(binding.lottieCat);

        });

    }

    private void startTimer() {

        time = 0;
        timer = new Timer();                  // 1초 후에 1초 간격으로 실행
        timer.scheduleAtFixedRate(new Camera2Fragment.SliderTimer(), 1000, 1000);
    }

    private void stopTimer() {
        time = 0;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    private class SliderTimer extends TimerTask {

        @Override
        public void run() {

            time += 1;

            long min = time / 60;
            long sec = time % 60;

            String sMin;
            String sSec;

            if (min < 10) sMin = "0" + min;
            else sMin = String.valueOf(min);

            if (sec < 10) sSec = "0" + sec;
            else sSec = String.valueOf(sec);

            String elapseTime = sMin + ":" + sSec;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.recordTimeTxtView.setText(elapseTime);
                }
            });


        }
    }

    private void resetUI() {

        binding.removeVideoBtn.setVisibility(View.INVISIBLE);
        binding.saveVideoBtn.setVisibility(View.INVISIBLE);
        binding.pictureBtn.setVisibility(View.VISIBLE);
        binding.pictureBtn.setImageDrawable(getResources().getDrawable(R.drawable.btn_start_de));
        binding.switchImgBtn.setVisibility(View.VISIBLE);
        binding.recordTimeTxtView.setText("00:00");
        binding.progressBar.setProgress(0);
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void deleteVideo() {

        FileManager.deleteVideo(getActivity(), mTempVideoPath, result -> {
            if (result) {
                mTempVideoPath = null;
            }else {
                Toast.makeText(getActivity(), "파일 삭제 실패", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
