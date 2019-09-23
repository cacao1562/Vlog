package kr.co.valuesys.vlog.mobile.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import kr.co.valuesys.vlog.mobile.Activity.BlankActivity;
import kr.co.valuesys.vlog.mobile.Common.Constants;
import kr.co.valuesys.vlog.mobile.InputFileNameDialog;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.Common.SimpleAlert;
import kr.co.valuesys.vlog.mobile.databinding.FragmentCameraBinding;


public class CameraFragment extends Fragment implements View.OnClickListener,
                                                        MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener,
                                                        InputFileNameDialog.OnInputDialogListener, BlankActivity.OnBackPressedListener {

    private static final String TAG = "CameraFragment";
    //Superbrain 대신 원하는 폴더이름을 만들면 됩니다.
    private static final String DETAIL_PATH = "DCIM/" + Constants.Video_Folder_Name + "/";

    private FragmentCameraBinding binding;

    // 카메라 광각, 전면, 후면
    private static final String CAM_WHAT = "2";
    private static final String CAM_FRONT = "1";
    private static final String CAM_REAR = "0";

    private String mCamId;

    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;
    private CameraManager mCameraManager;

    private Size mVideoSize;
    private Size mPreviewSize;
    private CaptureRequest.Builder mCaptureRequestBuilder;

    private int mSensorOrientation;

    private Semaphore mSemaphore = new Semaphore(1);

    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;

    private MediaRecorder mMediaRecorder;

    private String mNextVideoAbsolutePath;
    private boolean mIsRecordingVideo;

    private int mScreen_width;
    private int mScreen_height;

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        display.getRealSize(size);
        mScreen_width = size.x;
        mScreen_height = size.y;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false);
        mCamId = CAM_REAR;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

// 삳태바 투명
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        getActivity().getWindow().setNavigationBarColor(Color.TRANSPARENT);

        binding.pictureBtn.setOnClickListener(this);
        binding.switchImgBtn.setOnClickListener(this);

        binding.closeImgbutton.setOnClickListener(v -> {

            if (!mIsRecordingVideo && mNextVideoAbsolutePath != null) {

                AlertDialog alert = new SimpleAlert().createAlert(getActivity(), "촬영된 영상을 삭제하고 목록으로 돌아가시겠습니까?", true, dialog -> {

                    deleteVideo();
                    dialog.dismiss();
                    getActivity().finish();

                });

                alert.show();

            }else if ( !mIsRecordingVideo && mNextVideoAbsolutePath == null) {

                getActivity().finish();
            }

        });

        binding.removeVideoBtn.setOnClickListener(v -> {

            AlertDialog alert = new SimpleAlert().createAlert(getActivity(), "촬영된 영상을 삭제하시겠습니까?", true, dialog -> {

                resetUI();
                deleteVideo();
                startPreview();
                dialog.dismiss();

            });

            alert.show();

        });

        binding.saveVideoBtn.setOnClickListener(v -> {

            InputFileNameDialog inputFileNameDialog = InputFileNameDialog.newInstance();
            inputFileNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
            inputFileNameDialog.show(this.getChildFragmentManager(), "dialog");
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        assert getActivity() != null;
    }

    @Override
    public void onResume() {
        super.onResume();

        startBackgroundThread();

        if (binding.preview.isAvailable()) {

            Log.d(TAG, "==================================== onResume ===  preview isAvailable true");
            openCamera(binding.preview.getWidth(), binding.preview.getHeight());

        } else {

            Log.d(TAG, "==================================== onResume ===  preview isAvailable false");
            binding.preview.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {

        Log.d(TAG, "==================================== onPause ===");

        if (!mIsRecordingVideo && mNextVideoAbsolutePath != null) {


        }else if (!mIsRecordingVideo && mNextVideoAbsolutePath == null) {


        }else if (mIsRecordingVideo) {

            if (mNextVideoAbsolutePath != null) {

                mIsRecordingVideo = false;

                stopTimer();
                binding.recordTimeTxtView.setText("00L00");
                binding.pictureBtn.setText(R.string.record);

                try {
                    mMediaRecorder.stop();
                }catch (Exception e) {
                    e.printStackTrace();
                    Log.d("exception", "e = " + e.toString() );
                }

                deleteVideo();
            }

        }

        closeCamera();
        stopBackgroundThread();
//        if (mNextVideoAbsolutePath != null) {
//            stopRecordingVideo();
//        }
//
//        closeCamera();
//        stopBackgroundThread();
        super.onPause();
    }

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.d(TAG, "onSurfaceTextureAvailable  w = " + width + "  h  = " + height );
            openCamera(binding.preview.getWidth(), binding.preview.getHeight());
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.d(TAG, "onSurfaceTextureSizeChanged  w = " + width + "  h  = " + height );
            if(!mIsRecordingVideo) configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            startPreview();
            mSemaphore.release();
            if (null != binding.preview) {
                configureTransform(binding.preview.getWidth(), binding.preview.getHeight());
            }

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            mSemaphore.release();
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            mSemaphore.release();
            camera.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }
    };


    //카메라 기능 호출
    private void openCamera(int width, int height) {

        Activity activity = getActivity();
        assert activity != null;
        mCameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

        try {

            if (!mSemaphore.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }

            CameraCharacteristics cc = mCameraManager.getCameraCharacteristics(mCamId);
            StreamConfigurationMap scm = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = cc.get(CameraCharacteristics.SENSOR_ORIENTATION);

            if (scm == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }

            Size[] ms = scm.getOutputSizes(MediaRecorder.class); // 기기가 촬영할 수 있는 해상도 범위
            Size[] ss = scm.getOutputSizes(SurfaceTexture.class); // 프리뷰 해상도 범위

            for (Size s : ms) {
                Log.d(TAG, "Media size //  w = " + s.getWidth() + "  h = " + s.getHeight() );
            }

            for (Size s : ss) {
                Log.d(TAG, "SurfaceTexture size //  w = " + s.getWidth() + "  h = " + s.getHeight() );
            }

            mVideoSize = chooseVideoSize(scm.getOutputSizes(MediaRecorder.class));

// 전면카메라 프리뷰 비율이 안맞아서 16:9 비율로 고정 (다른 기기도 테스트 해봐야함)
//            mPreviewSize = new Size(1280, 720);
//            Display display = getActivity().getWindowManager().getDefaultDisplay();
//            Point size = new Point();
//            display.getSize(size);
//            int dwidth = size.x;
//            int dheight = size.y;
//
//            Log.d(TAG , " display size // w = " + dwidth + "  h = " + dheight );
//            double dd = dwidth * 1.7777777778;
//            mPreviewSize = new Size((int) dd, dwidth);

//            if (mCamId.equals(CAM_FRONT) ) {
//
//                mPreviewSize = new Size(1280, 720);
//
//            }else {
//
////                Display display = getActivity().getWindowManager().getDefaultDisplay();
////                Point size = new Point();
////                display.getSize(size);
////                int dwidth = size.x;
////                int dheight = size.y;
////
////                Log.d(TAG , " display size // w = " + dwidth + "  h = " + dheight );
////                double dd = dwidth * 1.7777777778;
////                mPreviewSize = new Size((int) dd, dwidth);
//                mPreviewSize = chooseOptimalSize(scm.getOutputSizes(SurfaceTexture.class), width, height, mVideoSize);
//            }
            mPreviewSize = chooseOptimalSize(scm.getOutputSizes(SurfaceTexture.class), width, height, mVideoSize);

            Log.d(TAG , " video size // w = " + mVideoSize.getWidth() + "  h = " + mVideoSize.getHeight() );
            Log.d(TAG , " preview size // w = " + mPreviewSize.getWidth() + "  h = " + mPreviewSize.getHeight() );

            int orientation = getResources().getConfiguration().orientation;

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

                Log.d(TAG , " preview LAND" );
                binding.preview.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            } else {

                Log.d(TAG , " preview Portraint" );
                binding.preview.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                Log.d(TAG, "======== onResume ===  preview // w = " + mPreviewSize.getHeight() + " h = " + mPreviewSize.getWidth());
            }

            configureTransform(width, height);
            mMediaRecorder = new MediaRecorder();
            mCameraManager.openCamera(mCamId, mStateCallback, mBackgroundHandler);

        } catch (CameraAccessException | SecurityException | NullPointerException | InterruptedException e) {
            Log.d("exception", "e = " + e.toString() );
            e.printStackTrace();
            activity.finish();
        }


    }

//어떤 기기는 해상도가 높은 내림차순으로 정렬되어있고 어떤것은 해상도가 낮은 것부터 오름차순으로 정렬되어 있음
    private Size chooseVideoSize(Size[] choices) {

        for (Size size : choices) {
// 해상도에 맞게 설정하면 될듯?
//            if(size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080){
//                return size;
//            }
            if (size.getWidth() == mScreen_height && size.getHeight() == mScreen_width) {
                return size;
            }
            if ( Math.abs(size.getWidth() - mScreen_height) <= 50 && Math.abs(size.getHeight() - mScreen_width) <= 50 ) {
                return size;
            }
            if ((double) size.getWidth() / size.getHeight() == (double) mScreen_height / mScreen_width) {
                return size;
            }
            if(size.getWidth() == size.getHeight() * 16 / 9 && size.getWidth() <= 1920) {
                return size;
            }
        }
        return choices[choices.length - 1];
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {

        Log.d(TAG, "// chooseOptimalSize // w = " + width + "  h = " + height + "  ratio  w = " + aspectRatio.getWidth() + " ratio h = " + aspectRatio.getHeight());
        List<Size> bigEnough = new ArrayList<>();
        List<Size> notBigEnough = new ArrayList<>();

        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();

        for (Size ops : choices) {

//            if(ops.getWidth() <= 1920 && ops.getHeight() <= 1080 && ops.getHeight() == ops.getWidth() * h / w && (ops.getWidth() >= width && ops.getHeight() >= height)) {
//                bigEnough.add(ops);
//            }else {
//                notBigEnough.add(ops);
//            }
            if (ops.getWidth() == aspectRatio.getWidth() && ops.getHeight() == aspectRatio.getHeight() ) {
                return ops;
            }
            if(ops.getHeight() == ops.getWidth() * h / w && (ops.getWidth() >= width && ops.getHeight() >= height)) {
                bigEnough.add(ops);
            }
        }

//        if (bigEnough.size() > 0) {
//            return Collections.min(bigEnough, new CompareSizesByArea());
//        } else {
//            return choices[0];
//        }

        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
//        } else if (notBigEnough.size() > 0) {
//            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }


// 카메라 닫기
    private void closeCamera() {

        try {

            mSemaphore.acquire();
            closePreviewSession();

            if (null != mCameraDevice) {

                mCameraDevice.close();
                mCameraDevice = null;
            }

            if (null != mMediaRecorder) {

                mMediaRecorder.release();
                mMediaRecorder = null;
            }


        } catch (InterruptedException ie) {
            ie.printStackTrace();

        } finally {
            mSemaphore.release();
        }

    }

//미리보기 기능
    private void startPreview() {

        if(null == mCameraDevice || !binding.preview.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            closePreviewSession();
            SurfaceTexture texture = binding.preview.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface previewSurface = new Surface(texture);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface), new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mCameraCaptureSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Activity activity = getActivity();
                    assert activity != null;
                    Toast.makeText(activity, "onConfigureFailed", Toast.LENGTH_SHORT).show();
                }

            }, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d("exception", "e = " + e.toString() );
        }
    }

    private void updatePreview() {

        if (null == mCameraDevice) {
            return;
        }
        try {
            setUpCaptureRequestBuilder(mCaptureRequestBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d("exception", "e = " + e.toString() );
        }

    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }


    private void configureTransform(int viewWidth, int viewHeight) {

        Log.d(TAG, "configureTransform  w = " + viewWidth + "  h  = " + viewHeight );

        Activity activity = getActivity();

        if (null == binding.preview || null == mPreviewSize || null == activity) {
            return;
        }

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getWidth(), mPreviewSize.getHeight());

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {

            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);

            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth()
            );
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }

        activity.runOnUiThread(() -> binding.preview.setTransform(matrix));

    }

    private void startBackgroundThread() {

        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {

        mBackgroundThread.quitSafely();

        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void closePreviewSession() {

        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
    }

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    //영상녹화 설정
    private void setUpMediaRecorder() throws IOException {

        final Activity activity = getActivity();

        if(null == activity) return;

        if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
            mNextVideoAbsolutePath = getVideoFilePath();
        }

//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//
//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//
//        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
//
//        mMediaRecorder.setVideoFrameRate(30);
//
//
//
//        mMediaRecorder.setVideoEncodingBitRate(10000000);
//        mMediaRecorder.setAudioSamplingRate(16000);

//        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
//        mMediaRecorder.setProfile(profile);
//        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());

        mMediaRecorder.setVideoFrameRate(30);

        mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);

        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setAudioSamplingRate(16000);

//        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

//촬영 맥스 시간 설정 1000 = 1초
        mMediaRecorder.setMaxDuration(15000);
        mMediaRecorder.setOnInfoListener(this);
        mMediaRecorder.setOnErrorListener(this);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        switch (mSensorOrientation) {

            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;

            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
        }

        mMediaRecorder.prepare();

    }

    //파일 이름 및 저장경로를 만듭니다.
    private String getVideoFilePath() {

        final File dir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        String path = dir.getPath() + "/" + DETAIL_PATH;
        File dst = new File(path);
        if(!dst.exists()) dst.mkdirs();

        return path + System.currentTimeMillis() + ".mp4";
    }

    //녹화시작
    private void startRecordingVideo() {

        if (null == mCameraDevice || !binding.preview.isAvailable() || null == mPreviewSize) {
            return;
        }
        assert getActivity() != null;

        try {
            closePreviewSession();
            setUpMediaRecorder();
            SurfaceTexture texture = binding.preview.getSurfaceTexture();
            assert texture != null;
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);

            List<Surface> surfaces = new ArrayList<>();

            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mCaptureRequestBuilder.addTarget(previewSurface);

            Surface recordSurface = mMediaRecorder.getSurface();
            surfaces.add(recordSurface);
            mCaptureRequestBuilder.addTarget(recordSurface);

            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {

                    mCameraCaptureSession = session;
                    updatePreview();
                    getActivity().runOnUiThread(() -> {
                        binding.pictureBtn.setText(R.string.stop);
                        mIsRecordingVideo = true;
                        try {
                            mMediaRecorder.start();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                    Activity activity = getActivity();
                    if (null != activity) {
                        Toast.makeText(activity, "onConfigureFailed", Toast.LENGTH_SHORT).show();
                    }
                }

            }, mBackgroundHandler);

            binding.switchImgBtn.setVisibility(View.INVISIBLE);
            binding.closeImgbutton.setVisibility(View.INVISIBLE);
//            timer();
            startTimer();

        } catch (CameraAccessException | IOException e) {
            e.printStackTrace();
            Log.d("exception", "e = " + e.toString() );
        }

    }

    //녹화 중지
    private void stopRecordingVideo() {

        mIsRecordingVideo = false;
//        binding.pictureBtn.setText(R.string.record);

        stopTimer();
        binding.pictureBtn.setVisibility(View.INVISIBLE);
        binding.removeVideoBtn.setVisibility(View.VISIBLE);
        binding.saveVideoBtn.setVisibility(View.VISIBLE);
        binding.closeImgbutton.setVisibility(View.VISIBLE);

//        try {
//            mCameraCaptureSession.stopRepeating();
//            mCameraCaptureSession.abortCaptures();
//        }catch (Exception e) {
//            e.printStackTrace();
//            Log.d("exception", "e = " + e.toString() );
//        }

        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("exception", "e = " + e.toString() );
        }


        Activity activity = getActivity();

        if (null != activity) {

            if (mNextVideoAbsolutePath != null) {

                Toast.makeText(activity, "Video saved: " + mNextVideoAbsolutePath, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Video saved: " + mNextVideoAbsolutePath);
                File file = new File(mNextVideoAbsolutePath);
// 아래 코드가 없으면 갤러리 저장 적용이 안됨.
                if(!file.exists()) file.mkdir();
                getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            }

        }

//        mNextVideoAbsolutePath = null;

//        startPreview();

    }

//녹화 최대 시간에 도달하면 녹화 일시중지 시킨다
    private void pauseRecording() {

        mIsRecordingVideo = false;
        binding.pictureBtn.setVisibility(View.INVISIBLE);
//        binding.pictureBtn.setText(R.string.record);
//        stop();
        stopTimer();

        binding.removeVideoBtn.setVisibility(View.VISIBLE);
        binding.saveVideoBtn.setVisibility(View.VISIBLE);

        try {
            mCameraCaptureSession.stopRepeating();
            mCameraCaptureSession.abortCaptures();
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("exception", "e = " + e.toString() );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

// pause 함수가 api24이상 부터 지원, 안드로이드 7.0 (하위 버전은 어떻게 해야할지 다시 봐야함)
            try {

                mMediaRecorder.pause();

            }catch (Exception e) {
                e.printStackTrace();
                Log.d("exception", "e = " + e.toString() );
            }

        }
    }

    //카메라 전, 후, 광각 변경
// 본인 카메라에 맞게 적용하면 됨.
    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

// 녹화 버튼
            case R.id.pictureBtn:

                if (mIsRecordingVideo) {
                    stopRecordingVideo();
//                    pauseRecording();
                } else {
                    startRecordingVideo();
                }
                break;

// 카메라 전, 후면 전환 버튼
            case R.id.switchImgBtn:

                switch (mCamId) {

                    case CAM_REAR:
                        mCamId = CAM_FRONT;
                        break;

                    case CAM_FRONT:
                        mCamId = CAM_REAR;
                        break;
//                    case CAM_WHAT:
//                        mCamId = CAM_REAR;
//                        break;
                }

                closeCamera();
                openCamera(binding.preview.getWidth(), binding.preview.getHeight());
                break;

        }

    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        Log.d("listener", "onError // waht = " + what + "  extra = " + extra );
        if (what == MediaRecorder.MEDIA_ERROR_SERVER_DIED) {
            Log.d("listener", "onError // MEDIA_ERROR_SERVER_DIED " );
        }
    }

// max duration 최대 촬영 시간이 되면 콜백
    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {

        Log.d("listener", "onInfo // what = " + what + " extra = " + extra );

        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
//            mr.stop();
            stopRecordingVideo();
//            pauseRecording();
        }
    }

// 파일 이름 입역하는 dialog 에서 저장버튼 누를시 들어오는 콜백 , 입력한 문자열을 넘겨 받음
    @Override
    public void onClickSave(String newFileName) {

        resetUI();

        if (mNextVideoAbsolutePath == null) { return; }

        File file = new File( mNextVideoAbsolutePath );
        File dir = Environment.getExternalStorageDirectory().getAbsoluteFile();
        String path = dir.getPath() + "/" + DETAIL_PATH;

// 사용자가 입력한 이름으로 파일이름 변경
        File fileNew = new File( path + newFileName + ".mp4" );

        if( file.exists() ) {

            if (file.renameTo(fileNew) ) {

                getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            }

        }else { }

        Activity activity = getActivity();

        if (null != activity) {

            Toast.makeText(activity, "Video saved: " + fileNew.getPath(), Toast.LENGTH_SHORT).show();

// 아래 코드가 없으면 갤러리 저장 적용이 안됨.
            if (fileNew.exists()) {
                getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileNew)));
            }

        }

        mNextVideoAbsolutePath = null;
//        startPreview();
//        getActivity().finish();
    }



    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView call");

        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private Timer timer;
    private int time = 0;

    private void startTimer() {

        time = 0;
        timer = new Timer();                  // 1초 후에 1초 간격으로 실행
        timer.scheduleAtFixedRate(new SliderTimer(), 1000, 1000);
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
        binding.pictureBtn.setText(R.string.record);
        binding.switchImgBtn.setVisibility(View.VISIBLE);
        binding.recordTimeTxtView.setText("00:00");

    }

    private void deleteVideo() {

        if (mNextVideoAbsolutePath != null) {

            try {

                File file = new File( mNextVideoAbsolutePath );

                if (file.exists() ) {

                    if (file.delete() ) {

                        getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                        mNextVideoAbsolutePath = null;
                        Log.d(TAG, "======= delete succcess ");
                    }else {
                        Log.d(TAG, "======= delete fail ");
                    }

                }

            }catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

// BlankActivity에서 back키 눌렀을때 callback
    @Override
    public void onBackPressed() {

        if (!mIsRecordingVideo && mNextVideoAbsolutePath != null) {

            AlertDialog alert = new SimpleAlert().createAlert(getActivity(), "촬영된 영상을 삭제하고 목록으로 돌아가시겠습니까?", true, dialog -> {

                deleteVideo();
                dialog.dismiss();
                getActivity().finish();

            });

            alert.show();

        }else if (!mIsRecordingVideo && mNextVideoAbsolutePath == null) {

            getActivity().finish();
        }

    }

}


